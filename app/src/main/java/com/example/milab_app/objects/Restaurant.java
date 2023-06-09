package com.example.milab_app.objects;

public class Restaurant {
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String websiteURL;

    public Restaurant(String name, String address, String phoneNumber, String websiteURL) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
    }

    /* getters */
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWebsiteURL() { return websiteURL; }

}
