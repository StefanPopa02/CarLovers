package com.stefanpopa.carloversapp.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class NewCarItem {
    private String docId;
    private String userId;
    private String letter;
    private String brand;
    private String model;
    private String year;
    private String version;
    private String engine;
    private String caracteristici;
    private List<String> carPhoto;
    private String brandLogo;
    private Timestamp timeAdded;

    public NewCarItem() {
    }

    public NewCarItem(String letter, String brand, String model, String year, String version, String engine, String caracteristici, List<String> carPhoto, String brandLogo) {
        this.letter = letter;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.version = version;
        this.engine = engine;
        this.caracteristici = caracteristici;
        this.carPhoto = carPhoto;
        this.brandLogo = brandLogo;
    }


    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getCaracteristici() {
        return caracteristici;
    }

    public void setCaracteristici(String caracteristici) {
        this.caracteristici = caracteristici;
    }

    public List<String> getCarPhoto() {
        return carPhoto;
    }

    public void setCarPhoto(List<String> carPhoto) {
        this.carPhoto = carPhoto;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    @Override
    public String toString() {
        return "NewCarItem{" +
                "letter='" + letter + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", version='" + version + '\'' +
                ", engine='" + engine + '\'' +
                ", caracteristici='" + caracteristici + '\'' +
                ", carPhoto='" + carPhoto + '\'' +
                ", brandLogo='" + brandLogo + '\'' +
                '}';
    }
}
