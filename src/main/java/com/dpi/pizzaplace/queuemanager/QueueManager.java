/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.queuemanager;

import com.dpi.pizzaplace.entities.Order;
import java.io.IOException;
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
    private QueueReceiver qr;
    private QueueSender qs;

    @Override
    public void update(Observable o, Object o1) {
        this.handleOrder((Order) o1);
    }

    private void handleOrder(Order o) {
        String localRestaurants = o.getAdress();
        try {
            qs.makeOrderAvailable(o, localRestaurants);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(QueueManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.qr = new QueueReceiver();
        this.qs = new QueueSender();
        this.qr.addObserver(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
