package com.kiosk.model;

public class ClientOrders {
    public static int lastId = 0;
    int id;
    String img;
    String productName;
    String size;
    double price;
    int quantity;
    String Addons;

    public ClientOrders(String img, String productName, String size, double price, int quantity, String Addons) {
        this.id = ++lastId;
        this.productName = productName;
        this.img = img;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.Addons = Addons;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int id() {
        return id;
    }

    public String img() {
        return img;
    }

    public String productName() {
        return productName;
    }

    public String size() {
        return size;
    }

    public double price() {
        return price;
    }

    public int quantity() {
        return quantity;
    }

    public String Addons() {
        return Addons;
    }
}
