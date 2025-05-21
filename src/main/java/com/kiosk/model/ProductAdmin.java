package com.kiosk.model;

public class ProductAdmin {
    private long id;
    private String productName;
    private Double small;
    private Double medium;
    private Double large;
    private String img;
    private boolean availability;

    // Constructor
    public ProductAdmin(long id, String productName, Double small, Double medium, Double large, String img, boolean availability) {
        this.id = id;
        this.productName = productName;
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.img = img;
        this.availability = availability;
    }

    public ProductAdmin() {
		// TODO Auto-generated constructor stub
	}

	// Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getSmall() {
        return small;
    }

    public void setSmall(Double small) {
        this.small = small;
    }

    public Double getMedium() {
        return medium;
    }

    public void setMedium(Double medium) {
        this.medium = medium;
    }

    public Double getLarge() {
        return large;
    }

    public void setLarge(Double large) {
        this.large = large;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
