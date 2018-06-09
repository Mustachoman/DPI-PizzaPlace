/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.restaurant;

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
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.Observable;

/**
 *
 * @author Marijn
 */
public final class RestaurantQueue extends Observable {

    private static final String RESTAUTRANT_QUEUE = "restauranttest";
    private static final String CUSTOMER_QUEUE = "customertest";
    private static final String ORDER_LISTENER_QUEUE = "orderlistener";
    private final Channel channel;

    public RestaurantQueue(String restaurantId, String restaurantLocation) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.connectToRestaurantQueue(restaurantLocation);
        this.connectToCustomerQueue(restaurantId);
    }

    public void connectToRestaurantQueue(String restaurantLocation) throws IOException, TimeoutException {
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
                Order incomingOrder = om.readValue(message, Order.class);
                System.out.println(incomingOrder.toString());
                updateIncomingOrder(incomingOrder);
            }

        };
        channel.basicConsume(queueName, false, consumer);
    }

    public void claimAvailableOrder(Order o) throws IOException {
        channel.exchangeDeclare(ORDER_LISTENER_QUEUE, BuiltinExchangeType.FANOUT);

        ObjectMapper om = new ObjectMapper();
        String message = om.writeValueAsString(o);

        channel.basicPublish(ORDER_LISTENER_QUEUE, "", null, message.getBytes("UTF-8"));
        System.out.println("Sending claim for'" + o.getId() + "'");
    }

    public void connectToCustomerQueue(String restaurantId) throws IOException {
        channel.exchangeDeclare(CUSTOMER_QUEUE, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        //"NL", "BE", etc..
        channel.queueBind(queueName, CUSTOMER_QUEUE, restaurantId);

        System.out.println("Awaiting customer id");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                ObjectMapper om = new ObjectMapper();
                Order acceptedOrder = om.readValue(message, Order.class);
                System.out.println(acceptedOrder.toString());
                updateIncomingOrder(acceptedOrder);
            }

        };
        channel.basicConsume(queueName, false, consumer);
    }

    private void updateIncomingOrder(Order o) {
        this.setChanged();
        this.notifyObservers(o);
    }

    public void sendCustomerUpdate(Order o, String orderStatus) throws IOException {
        channel.exchangeDeclare(o.getCustomerId(), BuiltinExchangeType.DIRECT);
        String message = "Status update for '" + o.getType() + "' : '" + orderStatus + "'";
        channel.basicPublish(o.getCustomerId(), "", null, message.getBytes("UTF-8"));
        System.out.println("Updated order '" + o.getId() + "' with status'" + orderStatus + "'");
    }
}
