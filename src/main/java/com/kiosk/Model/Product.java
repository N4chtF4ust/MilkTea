package com.kiosk.Model;

public class Product {
    int id;
    String productName;
    double small;
    double medium;
    double large;
    String img;
    boolean availability;

    public Product(int id, String productName, double small, double medium, double large, String img, boolean availability) {
        this.id = id;
        this.productName = productName;
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.img = img;
        this.availability = availability;
    }

    public int id() {
        return id;
    }

    public String productName() {
        return productName;
    }

    public double small() {
        return small;
    }

    public double medium() {
        return medium;
    }

    public double large() {
        return large;
    }

    public String img() {
        return img;
    }

    public boolean availability() {
        return availability;
    }


}