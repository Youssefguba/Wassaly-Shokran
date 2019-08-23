package com.phoenix.otlobbety.Model;

import java.util.List;

public class Request {

    private String nameOfCustomer;
    private String areaOfCustomer;
    private String addressOfCustomer;
    private String streetOfCustomer;
    private String buildingOfCustomer;
    private String floorOfCustomer;
    private String apartmentOfCustomer;
    private String additionalInfoAboutCustomer;
    private String phoneNumberOfCustomer;
    private int total;
    private String status;
    private List<Order> foods; //list of food order

    public Request() {
    }

    public Request(String nameOfCustomer, String areaOfCustomer, String addressOfCustomer, String streetOfCustomer, String buildingOfCustomer, String floorOfCustomer,
                   String apartmentOfCustomer, String additionalInfoAboutCustomer, String phoneNumberOfCustomer, int total, String status, List<Order> foods) {
        this.nameOfCustomer = nameOfCustomer;
        this.areaOfCustomer = areaOfCustomer;
        this.addressOfCustomer = addressOfCustomer;
        this.streetOfCustomer = streetOfCustomer;
        this.buildingOfCustomer = buildingOfCustomer;
        this.floorOfCustomer = floorOfCustomer;
        this.apartmentOfCustomer = apartmentOfCustomer;
        this.additionalInfoAboutCustomer = additionalInfoAboutCustomer;
        this.phoneNumberOfCustomer = phoneNumberOfCustomer;
        this.total = total;
        this.status = status;
        this.foods = foods;
    }

    public String getNameOfCustomer() {
        return nameOfCustomer;
    }

    public void setNameOfCustomer(String nameOfCustomer) {
        this.nameOfCustomer = nameOfCustomer;
    }

    public String getAreaOfCustomer() {
        return areaOfCustomer;
    }

    public void setAreaOfCustomer(String areaOfCustomer) {
        this.areaOfCustomer = areaOfCustomer;
    }

    public String getAddressOfCustomer() {
        return addressOfCustomer;
    }

    public void setAddressOfCustomer(String addressOfCustomer) {
        this.addressOfCustomer = addressOfCustomer;
    }

    public String getStreetOfCustomer() {
        return streetOfCustomer;
    }

    public void setStreetOfCustomer(String streetOfCustomer) {
        this.streetOfCustomer = streetOfCustomer;
    }

    public String getBuildingOfCustomer() {
        return buildingOfCustomer;
    }

    public void setBuildingOfCustomer(String buildingOfCustomer) {
        this.buildingOfCustomer = buildingOfCustomer;
    }

    public String getFloorOfCustomer() {
        return floorOfCustomer;
    }

    public void setFloorOfCustomer(String floorOfCustomer) {
        this.floorOfCustomer = floorOfCustomer;
    }

    public String getApartmentOfCustomer() {
        return apartmentOfCustomer;
    }

    public void setApartmentOfCustomer(String apartmentOfCustomer) {
        this.apartmentOfCustomer = apartmentOfCustomer;
    }

    public String getAdditionalInfoAboutCustomer() {
        return additionalInfoAboutCustomer;
    }

    public void setAdditionalInfoAboutCustomer(String additionalInfoAboutCustomer) {
        this.additionalInfoAboutCustomer = additionalInfoAboutCustomer;
    }

    public String getPhoneNumberOfCustomer() {
        return phoneNumberOfCustomer;
    }

    public void setPhoneNumberOfCustomer(String phoneNumberOfCustomer) {
        this.phoneNumberOfCustomer = phoneNumberOfCustomer;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
