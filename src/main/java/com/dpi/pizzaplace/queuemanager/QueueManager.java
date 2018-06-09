/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.queuemanager;

import com.dpi.pizzaplace.entities.Order;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Marijn
 */
public class QueueManager extends Application implements Observer {

    //Connect to Order queue
    //Create Restaurant queue
    private OrderQueue oq;
    private RestaurantQueue rq;
    private List<Order> orders;

    public QueueManager() {
        this.orders = new ArrayList<>();
    }

    @Override
    public void update(Observable o, Object o1) {
        Order incomingOrder = (Order) o1;
        if (!this.orders.contains(incomingOrder)) {
            this.orders.add(incomingOrder);
            this.handleOrder(incomingOrder);
        } else {
            Order foundOrder = this.orders.get(this.orders.indexOf(incomingOrder));
            if (foundOrder.getTakenBy().isEmpty()){
                foundOrder.setTakenBy(incomingOrder.getTakenBy());
            }
            this.handleOrder(foundOrder);
        }
    }

    private boolean handleOrder(Order o) {
        String localRestaurants = o.getAdress();
        try {
            return rq.updateOrderForRestaurants(o, localRestaurants);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(QueueManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.oq = new OrderQueue();
        this.rq = new RestaurantQueue();
        this.oq.addObserver(this);
        this.rq.addObserver(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
