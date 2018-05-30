/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.queuemanager;

import com.dpi.pizzaplace.entities.Order;
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
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Marijn
 */
public class QueueSender {
    
    private static final String RESTAURANT_QUEUE = "restauranttest";
    private final Channel restaurantChannel;

    public QueueSender() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.restaurantChannel = connection.createChannel();
    }

    public void makeOrderAvailable(Order newOrder, String restaurantCategory) throws IOException, TimeoutException {
        restaurantChannel.exchangeDeclare(RESTAURANT_QUEUE, BuiltinExchangeType.DIRECT);
        
        RestaurantOrder ro = new RestaurantOrder(newOrder.getType(), newOrder.getAdress(), newOrder.getTime());

        ObjectMapper om = new ObjectMapper();
        String message = om.writeValueAsString(ro);
        
        //Send to which restaurants?
        //ex: "NL", "BE", etc..
        restaurantChannel.basicPublish(RESTAURANT_QUEUE, restaurantCategory, null, message.getBytes("UTF-8"));
        System.out.println("Made '" + newOrder.toString() + "' available for '" + restaurantCategory + "'");
    }
}
