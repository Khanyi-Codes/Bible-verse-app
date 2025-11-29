package com.bibleapp;

import com.bibleapp.model.BibleVerse;
import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
public class MessageQueueService {

    private static final String BROKE_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "verse.requests";
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private Gson gson;

    public MessageQueueService() throws JMSException {
        this.gson = gson;
        setupConnection();
    }

    private void setupConnection() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKE_URL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(QUEUE_NAME);
        producer = session.createProducer(destination);
    }
    public void sendVerseRequest(String requestId) throws JMSException {
        TextMessage message = session.createTextMessage(requestId);
        producer.send(message);
        System.out.println("📨 Sent verse request: " + requestId);
    }

    public void close() throws JMSException {
        if (producer != null) producer.close();
        if (session != null) session.close();
        if (connection != null) connection.close();
    }
}
