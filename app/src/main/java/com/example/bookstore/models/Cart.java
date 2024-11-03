package com.example.bookstore.models;

import java.util.List;

public class Cart {
    private int cartID;
    private List<CartItem> cartItems;

    public Cart(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public int getCartID() {return cartID;}
    public void setCartID(int cartID) {this.cartID = cartID;}

    public List<CartItem> getCartItems() {return cartItems;}
    public void setCartItems(List<CartItem> cartItems) {this.cartItems = cartItems;}
}
