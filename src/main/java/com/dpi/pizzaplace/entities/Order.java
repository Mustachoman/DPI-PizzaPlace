/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.entities;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Marijn
 */
public class Order implements Serializable {
    
    private String id;
    private String type;
    private String adress;
    private String time;
    
    public Order(){}

    public Order(String type, String adress, String time) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.adress = adress;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
