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
public class RestaurantQueue extends Observable {

    private static final String RESTAURANT_QUEUE = "restauranttest";
    private static final String ORDER_LISTENER_QUEUE = "orderlistener";
    private final Channel restaurantChannel;

    public RestaurantQueue() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.restaurantChannel = connection.createChannel();
        this.listenToRestaurantClaims(this.restaurantChannel);
    }

    public boolean updateOrderForRestaurants(Order newOrder, String restaurantCategory) throws IOException, TimeoutException {
        restaurantChannel.exchangeDeclare(RESTAURANT_QUEUE, BuiltinExchangeType.DIRECT);
        ObjectMapper om = new ObjectMapper();
        String message = om.writeValueAsString(newOrder);

        //Send to which restaurants?
        //ex: "NL", "BE", etc..
        restaurantChannel.basicPublish(RESTAURANT_QUEUE, restaurantCategory, null, message.getBytes("UTF-8"));
        System.out.println("Made '" + newOrder.toString() + "' available for '" + restaurantCategory + "'");
        return true;
    }

    public void listenToRestaurantClaims(Channel restaurantChannel) throws IOException {        
        restaurantChannel.exchangeDeclare(ORDER_LISTENER_QUEUE, BuiltinExchangeType.FANOUT);
        String queueName = restaurantChannel.queueDeclare().getQueue();
        restaurantChannel.queueBind(queueName, ORDER_LISTENER_QUEUE, "");
        
        Consumer consumer = new DefaultConsumer(this.restaurantChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");

                ObjectMapper om = new ObjectMapper();
                Order claimingOrder = om.readValue(message, Order.class);

                System.out.println("Restaurant '" + claimingOrder.getTakenBy() + "' wants order '" + claimingOrder.getId() + "'");
                updateOrder(claimingOrder);
            }
        };
        restaurantChannel.basicConsume(queueName, false, consumer);
    }
    
    private void updateOrder(Order o){
        this.setChanged();
        this.notifyObservers(o);
    }
}
