package com.example.charitymiles;

public class OrganizationModel {
    private String id;
    private String name;
    private String imageUrl; // URL to the organization's photo
    private String donationType;

    // Default constructor required for calls to DataSnapshot.getValue(OrganizationModel.class)
    public OrganizationModel() {
    }

    // Constructor with parameters
    public OrganizationModel(String id, String name, String imageUrl, String donationType) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.donationType = donationType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getimageUrl() { return imageUrl; }
    public void setimageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDonationType() { return donationType; }
    public void setDonationType(String donationType) { this.donationType = donationType; }
}
