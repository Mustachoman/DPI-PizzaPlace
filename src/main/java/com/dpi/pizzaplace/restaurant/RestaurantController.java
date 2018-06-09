package com.dpi.pizzaplace.restaurant;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class RestaurantController implements Initializable, Observer {

    private RestaurantQueue rq;

    @FXML
    private Label label;

    @FXML
    private ListView lstOrders;

    @FXML
    private ListView lstTakenOrders;

    @FXML
    private Button btnTakeOrder;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSetLocation;

    @FXML
    private TextField txtRestaurantLocation;

    @FXML
    private TextField txtOrderStatus;

    private String restaurantId;

    ObservableList<Order> availableOrders = FXCollections.observableArrayList();
    ObservableList<Order> takenOrders = FXCollections.observableArrayList();

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        this.claimAvailableOrder((Order) this.lstOrders.getSelectionModel().getSelectedItem());
        label.setText("Hebbes");
    }

    @FXML
    private void setLocation(ActionEvent event) {
        try {
            lstOrders.setItems(this.availableOrders);
            lstTakenOrders.setItems(this.takenOrders);
            this.rq = new RestaurantQueue(this.restaurantId, this.txtRestaurantLocation.getText());
            this.rq.addObserver(this);
            this.btnSetLocation.setDisable(true);
            this.txtRestaurantLocation.setDisable(true);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RestaurantController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.restaurantId = UUID.randomUUID().toString();
    }

    private void claimAvailableOrder(Order o) throws IOException {
        o.setTakenBy(this.restaurantId);
        this.rq.claimAvailableOrder(o);
    }

    @Override
    public void update(Observable o, Object o1) {
        Order incomingOrder = (Order) o1;
        System.out.println(incomingOrder.getId());

        if (this.availableOrders.contains(incomingOrder)) {
            if (incomingOrder.getTakenBy().equals(this.restaurantId)) {
                Platform.runLater(() -> {
                    this.takenOrders.add(incomingOrder);
                });
            }
            Platform.runLater(() -> {
                this.availableOrders.remove(incomingOrder);
            });

        }
        if (incomingOrder.getTakenBy().isEmpty()) {
            Platform.runLater(() -> {
                this.availableOrders.remove(incomingOrder);
            });
        }
    }
}
