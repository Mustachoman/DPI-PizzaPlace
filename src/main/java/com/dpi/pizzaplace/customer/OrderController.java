package com.dpi.pizzaplace.customer;

import com.dpi.pizzaplace.entities.Order;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class OrderController implements Initializable, Observer {

    @FXML
    private Label label;

    @FXML
    private Button btnPlaceOrder;

    @FXML
    private TextField txtPizzaType;

    @FXML
    private TextField txtOrderAddress;

    @FXML
    private TextField txtOrderTime;

    private String customerId;

    private OrderQueue oq;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String pizzaType = this.txtPizzaType.getText();
        String orderAddress = this.txtOrderAddress.getText();
        String orderTime = this.txtOrderTime.getText();

        Order order = new Order(pizzaType, orderAddress, orderTime);
        order.setCustomerId(customerId);
        if (this.PlaceOrderOnQueue(order)) {
            label.setText(order.toString());
        } else {
            label.setText("Error. Order not placed.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.customerId = UUID.randomUUID().toString();
            this.oq = new OrderQueue(customerId);
            this.oq.addObserver(this);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean PlaceOrderOnQueue(Order newOrder) {
        try {
            this.oq.PlaceOrder(newOrder);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public void update(Observable o, Object o1) {
        Platform.runLater(() -> {
            this.label.setText((String) o1);
        });
    }

}
