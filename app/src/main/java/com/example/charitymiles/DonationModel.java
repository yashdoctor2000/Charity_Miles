package com.example.charitymiles;

import java.io.Serializable;

public class DonationModel implements Serializable {

    private String Uid;
    private String DonorName;
    private String Date;
    private String Contact;
    private String donationItem;
    private String donationQuantity;

    private DonationModel(){

    }
    private DonationModel(String Uid, String DonorName, String Date, String Contact, String donationItem, String donationQuantity){
        this.Uid = Uid;
        this.DonorName = DonorName;
        this.Date = Date;
        this.Contact = Contact;
        this.donationItem = donationItem;
        this.donationQuantity = donationQuantity;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getDonorName() {
        return DonorName;
    }

    public void setDonorName(String donorName) {
        DonorName = donorName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getDonationItem() {
        return donationItem;
    }

    public void setDonationItem(String donationItem) {
        this.donationItem = donationItem;
    }

    public String getDonationQuantity() {
        return donationQuantity;
    }

    public void setDonationQuantity(String donationQuantity) {
        this.donationQuantity = donationQuantity;
    }


}
