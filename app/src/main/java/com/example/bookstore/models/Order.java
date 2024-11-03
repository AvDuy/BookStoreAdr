package com.example.bookstore.models;

import java.util.Date;
import java.util.List;

public class Order {
    private String userId;
    private String cartId;
    private double totalAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    // Empty constructor for Firestore
    public Order() {}

    public Order(String userId, String cartId, double totalAmount, String status, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.cartId = cartId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getCartId() {return cartId;}
    public void setCartId(String cartId) {this.cartId = cartId;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public double getTotalAmount() {return totalAmount;}
    public void setTotalAmount(double totalAmount) {this.totalAmount = totalAmount;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

    public Date getCreatedAt() {return createdAt;}
    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}

    public Date getUpdatedAt() {return updatedAt;}
    public void setUpdatedAt(Date updatedAt) {this.updatedAt = updatedAt;}
}
