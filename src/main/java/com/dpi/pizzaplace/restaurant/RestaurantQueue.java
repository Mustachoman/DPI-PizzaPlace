/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.restaurant;

import com.dpi.pizzaplace.entities.RestaurantOrder;
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
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.Observable;

/**
 *
 * @author Marijn
 */
public final class RestaurantQueue extends Observable {

    private static final String RESTAUTRANT_QUEUE = "restauranttest";
    private final Channel channel;
    
    public RestaurantQueue(String restaurantLocation) throws IOException, TimeoutException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.connectToQueue(restaurantLocation);
    }

    public void connectToQueue(String restaurantLocation) throws IOException, TimeoutException {
        channel.exchangeDeclare(RESTAUTRANT_QUEUE, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();
        
        //"NL", "BE", etc..
        channel.queueBind(queueName, RESTAUTRANT_QUEUE, restaurantLocation);

        System.out.println("Awaiting orders...");
        
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                ObjectMapper om = new ObjectMapper();
                RestaurantOrder incomingOrder = om.readValue(message, RestaurantOrder.class);
                System.out.println(incomingOrder.toString());
                addOrder(incomingOrder);
            }

        };
        channel.basicConsume(queueName, false, consumer);
    }
    
    private void addOrder(RestaurantOrder o){
        this.setChanged();  
        this.notifyObservers(o);
    }
}
