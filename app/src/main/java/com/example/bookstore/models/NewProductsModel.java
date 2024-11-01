package com.example.bookstore.models;

public class NewProductsModel {

    String description;
    String name;
    String rating;
    int price;
    String img_url;


    public NewProductsModel() {
    }

    @Override
    public String toString() {
        return "NewProductsModel{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", rating='" + rating + '\'' +
                ", price=" + price +
                ", img_url='" + img_url + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
