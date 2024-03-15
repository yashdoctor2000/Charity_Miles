package com.example.charitymiles;

import java.io.Serializable;

public class OrganizationModel implements Serializable {
    private String id;
    private String orgName;
    private String imageUrl; // URL to the organization's photo
    private String donationType;

    // Default constructor required for calls to DataSnapshot.getValue(OrganizationModel.class)
    public OrganizationModel() {
    }

    // Constructor with parameters
    public OrganizationModel(String id, String orgName, String imageUrl, String donationType) {
        this.id = id;
        this.orgName = orgName;
        this.imageUrl = imageUrl;
        this.donationType = donationType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getorgName() { return orgName; }
    public void setName(String orgName) { this.orgName = orgName; }
    public String getimageUrl() { return imageUrl; }
    public void setimageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDonationType() { return donationType; }
    public void setDonationType(String donationType) { this.donationType = donationType; }
}
