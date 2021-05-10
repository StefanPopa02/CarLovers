package com.stefanpopa.carloversapp.model;

public class BrandItem {
    private String brandName;
    private String brandLogoImgURL;

    public BrandItem(String brandName, String brandLogoImgURL) {
        this.brandName = brandName;
        this.brandLogoImgURL = brandLogoImgURL;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandLogoImgURL() {
        return brandLogoImgURL;
    }

    public void setBrandLogoImgURL(String brandLogoImgURL) {
        this.brandLogoImgURL = brandLogoImgURL;
    }

    @Override
    public String toString() {
        return "BrandItem{" +
                "brandName='" + brandName + '\'' +
                ", brandLogoImgURL='" + brandLogoImgURL + '\'' +
                '}';
    }
}
