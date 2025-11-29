package com.bibleapp;

import com.bibleapp.model.BibleVerse;
import com.google.gson.Gson;
import io.javalin.Javalin;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BibleVerseApp {
    private static final int PORT = 7070;
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String RESPONSE_QUEUE = "verse.responses";

    private static ConcurrentHashMap<String, String> pendingRequests = new ConcurrentHashMap<>();
    private static Gson gson = new Gson();
    private static MessageQueueService queueService;
    private static VerseService verseService;

    public static void main(String[] args) throws JMSException {
        // Start services
        queueService = new MessageQueueService();
        verseService = new VerseService();

        // Start listening for responses
        startResponseListener();

        // Create Javalin web server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(PORT);

        System.out.println("Bible Verse API running on http://localhost:" + PORT);

        // REST Endpoint: Get a random verse
        app.get("/api/verse", ctx -> {
            String requestId = UUID.randomUUID().toString();
            pendingRequests.put(requestId, "pending");

            // Send request to queue
            queueService.sendVerseRequest(requestId);

            // Wait for response (with timeout)
            int attempts = 0;
            while (pendingRequests.get(requestId).equals("pending") && attempts < 50) {
                Thread.sleep(100);
                attempts++;
            }

            String response = pendingRequests.remove(requestId);
            if (response != null && !response.equals("pending")) {
                ctx.json(response);
            } else {
                ctx.status(500).result("Timeout waiting for verse");
            }
        });

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                queueService.close();
                verseService.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }

    private static void startResponseListener() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(RESPONSE_QUEUE);
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String requestId = textMessage.getStringProperty("requestId");
                    String verseJson = textMessage.getText();

                    pendingRequests.put(requestId, verseJson);
                    System.out.println("✅ Received response for: " + requestId);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}