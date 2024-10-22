package com.example.bookstore;

public class Chapter {
    private int ava;
    private String title;
    private String des;

    public Chapter(int ava, String title, String des) {
        this.ava = ava;
        this.title = title;
        this.des = des;
    }

    public int getAva() {
        return ava;
    }

    public void setAva(int ava) {
        this.ava = ava;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
