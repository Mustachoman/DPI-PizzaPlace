/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpi.pizzaplace.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Marijn
 */
public class Order implements Serializable {

    private String id;
    private String customerId;
    private String type;
    private String adress;
    private String time;
    private String takenBy;

    public Order() {
    }

    public Order(String type, String adress, String time) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.adress = adress;
        this.time = time;
        this.takenBy = "";
        this.customerId = "";
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

    public String getTakenBy() {
        return takenBy;
    }

    public void setTakenBy(String takenBy) {
        this.takenBy = takenBy;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Type: " + this.type + ", Address: " + this.adress + ", Time: " + this.time;
    }

    /**
     * Check if another object has the same id
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Order that = (Order) obj;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
