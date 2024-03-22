package com.example.charitymiles;

import java.io.Serializable;

public class OrganizationModel implements Serializable {
    private String Uid;
    private String orgName;
    private String imageUrl; // URL to the organization's photo
    private String donationType;
    private String address;
    private String orgDescription;
    private String orgContact;
    private String orgVision;


    // Default constructor required for calls to DataSnapshot.getValue(OrganizationModel.class)
    public OrganizationModel() {
    }

    // Constructor with parameters
    public OrganizationModel(String Uid, String orgName, String imageUrl, String donationType, String address, String orgDescription, String orgContact, String orgVision) {
        this.Uid = Uid;
        this.orgName = orgName;
        this.imageUrl = imageUrl;
        this.donationType = donationType;
        this.address = address;
        this.orgDescription = orgDescription;
        this.orgContact = orgContact;
        this.orgVision = orgVision;
    }

    // Getters and Setters
    public String getUid() { return Uid; }
    public void setUid(String Uid) { this.Uid = Uid; }
    public String getorgName() { return orgName; }
    public void setName(String orgName) { this.orgName = orgName; }
    public String getimageUrl() { return imageUrl; }
    public void setimageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDonationType() { return donationType; }
    public void setDonationType(String donationType) { this.donationType = donationType; }
    public String getAddress(){ return address;}
    public void setAddress(String address){this.address = address;}
    public String getOrgDescription(){ return orgDescription;}
    public void setOrgDescription(String orgDescription){this.orgDescription=orgDescription;}
    public String getOrgContact(){return orgContact;}
    public void setOrgContact(String orgContact){ this.orgContact = orgContact;}

    public String getorgVision() { return orgVision; }
    public void setOrgVision(String orgVision) { this.orgVision = orgVision; }
}
