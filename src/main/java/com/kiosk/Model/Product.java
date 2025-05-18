package com.kiosk.Model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id == product.id &&
               Double.compare(product.small, small) == 0 &&
               Double.compare(product.medium, medium) == 0 &&
               Double.compare(product.large, large) == 0 &&
               availability == product.availability &&
               Objects.equals(productName, product.productName) &&
               Objects.equals(img, product.img);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, small, medium, large, img, availability);
    }
}
