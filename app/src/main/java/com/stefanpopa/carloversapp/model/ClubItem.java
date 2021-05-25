package com.stefanpopa.carloversapp.model;

public class ClubItem {
    private String brand;
    private String fullName;
    private int id;
    private String model;
    private String logoImgUrl;

    public ClubItem() {
    }

    public String getLogoImgUrl() {
        return logoImgUrl;
    }

    public void setLogoImgUrl(String logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "ClubItem{" +
                "brand='" + brand + '\'' +
                ", fullName='" + fullName + '\'' +
                ", id=" + id +
                ", model='" + model + '\'' +
                ", logoImgUrl='" + logoImgUrl + '\'' +
                '}';
    }
}
