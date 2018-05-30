package com.dpi.pizzaplace.restaurant;

import com.dpi.pizzaplace.entities.RestaurantOrder;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Button btnTakeOrder;

    @FXML
    private Button btnSetLocation;

    @FXML
    private TextField txtRestaurantLocation;

    ObservableList<RestaurantOrder> orders = FXCollections.observableArrayList();

    @FXML
    private void handleButtonAction(ActionEvent event) {
        this.takeOrderFromQueue();
        label.setText("gottem");
    }

    @FXML
    private void setLocation(ActionEvent event) {
        try {
            lstOrders.setItems(orders);
            this.rq = new RestaurantQueue(this.txtRestaurantLocation.getText());
            this.rq.addObserver(this);
            this.btnSetLocation.setDisable(true);
            this.txtRestaurantLocation.setDisable(true);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RestaurantController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private boolean takeOrderFromQueue() {
        return true;
    }

    @Override
    public void update(Observable o, Object o1) {
        this.orders.add((RestaurantOrder) o1);
    }
}
