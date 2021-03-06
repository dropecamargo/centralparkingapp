package com.koiti.centralparking.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by @dropecamargo.
 */
public class Parking {

    private String id;
    private String name;
    private String image;
    private String address;
    private String phone;
    private int capacity;
    private int availability;
    private String schedule;
    private String rates;
    private String agreement;
    private String monthly;
    private String email;

    private Double latitude;
    private Double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public String getMonthly() {
        return monthly;
    }

    public void setMonthly(String monthly) {
        this.monthly = monthly;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String toJSON() {

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("name", getName());
            jsonObject.put("address", getAddress());
            jsonObject.put("phone", getPhone());
            jsonObject.put("image", getImage());
            jsonObject.put("schedule", getSchedule());
            jsonObject.put("capacity", getCapacity());
            jsonObject.put("availability", getAvailability());
            jsonObject.put("rates", getRates());
            jsonObject.put("agreement", getAgreement());
            jsonObject.put("monthly", getMonthly());
            jsonObject.put("email", getEmail());
            jsonObject.put("latitude", getLatitude());
            jsonObject.put("longitude", getLongitude());
        } catch (JSONException e) { }
        return jsonObject.toString();
    }
}
