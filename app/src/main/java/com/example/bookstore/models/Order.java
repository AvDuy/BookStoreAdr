package com.example.bookstore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String cartId;
    private String addressId;
    private String paymentMethod;
    private double totalAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Cart cart;
    // Empty constructor for Firestore
    public Order() {}

    public Order(String userId, String cartId, String addressId, String paymentMethod, double totalAmount, String status, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.cartId = cartId;
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Order(String userId, String cartId, String addressId, double totalAmount, String status, Date createdAt, Date updatedAt) {
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Order(String userId, String cartId, double totalAmount, String status, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.cartId = cartId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Order(String orderId, String userId, String cartId, double totalAmount, String status, Date createdAt, Date updatedAt, Cart cart) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartId = cartId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cart = cart;
    }

    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}

    public String getAddressId() {return addressId;}
    public void setAddressId(String addressId) {this.addressId = addressId;}

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
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    // Phương thức để lấy ảnh sản phẩm đầu tiên
    public String getFirstProductImage() {
        if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            Product product = cart.getCartItems().get(0).getProduct(); // Lấy sản phẩm đầu tiên
            if (product != null) {
                return product.getImage(); // Trả về URL ảnh
            }
        }
        return null; // Nếu không có ảnh
    }
}
