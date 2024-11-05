package com.example.bookstore.models;

public class CartItem {
    private Product product;
    private int quantity;
    private double productTotal;

    public CartItem(){}

    public CartItem(Product product, int quantity, double productTotal) {
        this.product = product;
        this.quantity = quantity;
        this.productTotal = productTotal;
    }

    public Product getProduct() {return product;}

    public void setProduct(Product product) {this.product = product;}

    public int getQuantity() {return quantity;}

    public void setQuantity(int quantity) {this.quantity = quantity;}

    public double getProductTotal() {return productTotal;}

    public void setProductTotal(double productTotal) {this.productTotal = productTotal;}
}
