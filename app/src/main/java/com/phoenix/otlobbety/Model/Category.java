package com.phoenix.otlobbety.Model;

public class Category {
    private String CategoryName;
    private String Name;
    private String Image;
    private String DeliveryTime;

    public Category() {
    }

    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    public Category(String categoryName, String name, String image, String deliveryTime) {
        CategoryName = categoryName;
        Name = name;
        Image = image;
        DeliveryTime = deliveryTime;
    }

    public Category(String categoryName, String name, String image) {
        CategoryName = categoryName;
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        DeliveryTime = deliveryTime;
    }
}
