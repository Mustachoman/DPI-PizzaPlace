package com.dpi.pizzaplace.customer;

import com.dpi.pizzaplace.entities.Order;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class OrderController implements Initializable {

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
    
    private OrderQueue oq;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String pizzaType = this.txtPizzaType.getText();
        String orderAddress = this.txtOrderAddress.getText();
        String orderTime = this.txtOrderTime.getText();

        Order order = new Order(pizzaType, orderAddress, orderTime);
        if (this.PlaceOrderOnQueue(order)) {
            label.setText(order.toString());
        } else {
            label.setText("Error. Order not placed.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.oq = new OrderQueue();
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
}
