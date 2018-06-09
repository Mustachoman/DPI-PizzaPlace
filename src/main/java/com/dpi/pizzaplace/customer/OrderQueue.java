/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.customer;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marijn
 */
public class OrderQueue extends Observable {

    private static final String ORDER_QUEUE = "ordertest";
    private final Channel orderChannel;

    public OrderQueue(String customerId) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.orderChannel = connection.createChannel();
        this.subscribeToOrderStatus(customerId);
    }

    public void PlaceOrder(Order newOrder) throws IOException, TimeoutException {
        orderChannel.exchangeDeclare(ORDER_QUEUE, BuiltinExchangeType.FANOUT);

        ObjectMapper om = new ObjectMapper();
        String message = om.writeValueAsString(newOrder);

        orderChannel.basicPublish(ORDER_QUEUE, "", null, message.getBytes("UTF-8"));
        System.out.println("Placed Order '" + newOrder.toString() + "'");
    }

    private void subscribeToOrderStatus(String customerId) throws IOException {
        orderChannel.exchangeDeclare(customerId, BuiltinExchangeType.DIRECT);
        String queueName = orderChannel.queueDeclare().getQueue();
        
        orderChannel.queueBind(queueName, customerId, "");
//        orderChannel.queueDeclare(customerId, false, false, false, null);
        Consumer consumer = new DefaultConsumer(orderChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                notifyUpdate(message);
            }
        };
        orderChannel.basicConsume(queueName, false, consumer);
    }
    
    private void notifyUpdate(String statusUpdate){
        this.setChanged();
        this.notifyObservers(statusUpdate);
    }
}
