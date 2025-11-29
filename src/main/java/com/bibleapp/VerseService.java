package com.bibleapp;

import com.bibleapp.model.BibleVerse;
import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class VerseService implements MessageListener {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String REQUEST_QUEUE = "verse.requests";
    private static final String RESPONSE_QUEUE = "verse.responses";

    private Connection connection;
    private Session session;
    private MessageProducer responseProducer;
    private VerseRepository repository;
    private Gson gson;

    public VerseService() throws JMSException {
        this.repository = new VerseRepository();
        this.gson = new Gson();
        setupConnection();
    }

    private void setupConnection() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Set up listener for requests
        Destination requestDestination = session.createQueue(REQUEST_QUEUE);
        MessageConsumer consumer = session.createConsumer(requestDestination);
        consumer.setMessageListener(this);

        // Set up producer for responses
        Destination responseDestination = session.createQueue(RESPONSE_QUEUE);
        responseProducer = session.createProducer(responseDestination);

        System.out.println("✅ VerseService is listening for requests...");
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String requestId = textMessage.getText();

                System.out.println("📖 Received request: " + requestId);

                // Get a random verse
                BibleVerse verse = repository.getRandomVerse();

                // Convert to JSON and send response
                String verseJson = gson.toJson(verse);
                TextMessage response = session.createTextMessage(verseJson);
                response.setStringProperty("requestId", requestId);
                responseProducer.send(response);

                System.out.println("✉️ Sent verse: " + verse.getReference());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close() throws JMSException {
        if (responseProducer != null) responseProducer.close();
        if (session != null) session.close();
        if (connection != null) connection.close();
    }
}