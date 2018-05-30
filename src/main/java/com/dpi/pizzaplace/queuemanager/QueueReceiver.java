/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.queuemanager;

import com.dpi.pizzaplace.entities.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Marijn
 */
public final class QueueReceiver extends Observable {
    
    private static final String ORDER_QUEUE = "ordertest";
    private final Channel orderChannel;
    
    public QueueReceiver() throws IOException, TimeoutException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.orderChannel = connection.createChannel();
        this.connectToQueue(this.orderChannel);
    }

    public void connectToQueue(Channel orderChannel) throws IOException, TimeoutException {
        orderChannel.exchangeDeclare(ORDER_QUEUE, BuiltinExchangeType.FANOUT);
        String queueName = orderChannel.queueDeclare().getQueue();
        orderChannel.queueBind(queueName, ORDER_QUEUE, "");

        System.out.println("Awaiting orders...");
        
        Consumer consumer = new DefaultConsumer(orderChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                ObjectMapper om = new ObjectMapper();
                Order incomingOrder = om.readValue(message, Order.class);
                System.out.println(incomingOrder.toString());
                addOrder(incomingOrder);
            }

        };
        orderChannel.basicConsume(queueName, false, consumer);
    }
    
    private void addOrder(Order o){
        this.setChanged();  
        this.notifyObservers(o);
    }
}
