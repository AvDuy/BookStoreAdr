package com.example.bookstore.model;

public class MyCartModel {

    String Imgurl;
    String productName;
    String productPrice;
    String totalQuantity;
    int totalPrice;
    String Del;
    private String cartId;

    public MyCartModel() {
    }

    public MyCartModel(String imgurl, String productName, String productPrice, String totalQuantity, int totalPrice, String del,String cartId) {
        this.Imgurl = imgurl;
        this.productName = productName;
        this.productPrice = productPrice;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.Del = del;
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getDel() {
        return Del;
    }

    public void setDel(String del) {
        Del = del;
    }

    public String getImgurl() {
        return Imgurl;
    }

    public void setImgurl(String imgurl) {
        Imgurl = imgurl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
