/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.entities;

import java.util.UUID;

/**
 *
 * @author Marijn
 */
public class RestaurantOrder {
     
    private String type;
    private String adress;
    private String time;
    
    public RestaurantOrder(){}

    public RestaurantOrder(String type, String adress, String time) {
        this.type = type;
        this.adress = adress;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Type: " + this.type + ", Address: " + this.adress + ", Time: " + this.time;
    }

}
