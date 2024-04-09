package com.example.charitymiles;

import java.io.Serializable;

public class ReceiverModel implements Serializable {
    private String Uid;
    private String DonorId;
    private String OrgId;
    private String DonorName;
    private int IsStatus;
    private String OrgName;
    private String date;
    private String desAddress;
    private String donationItem;
    private String donationQuantity;
    private String time;
    private String Contact;

    public ReceiverModel(){

    }


    public ReceiverModel(String Uid, String DonorId, String OrgId, String DonorName, int IsStatus, String OrgName, String date, String desAddress, String donationItem, String donationQuantity, String time, String Contact){
        this.OrgId = OrgId;
        this.DonorName = DonorName;
        this.IsStatus = IsStatus;
        this.DonorId = DonorId;
        this.Uid = Uid;
        this.OrgName = OrgName;
        this.date = date;
        this.desAddress = desAddress;
        this.donationItem = donationItem;
        this.donationQuantity = donationQuantity;
        this.time = time;
        this.Contact = Contact;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getDonorId() {
        return DonorId;
    }

    public void setDonorId(String donorId) {
        DonorId = donorId;
    }

    public String getOrgId() {
        return OrgId;
    }

    public void setOrgId(String orgId) {
        OrgId = orgId;
    }

    public String getDonorName() {
        return DonorName;
    }

    public void setDonorName(String donorName) {
        DonorName = donorName;
    }

    public int getIsStatus() {
        return IsStatus;
    }

    public void setIsStatus(int isStatus) {
        IsStatus = isStatus;
    }

    public String getOrgName() {
        return OrgName;
    }

    public void setOrgName(String orgName) {
        OrgName = orgName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesAddress() {
        return desAddress;
    }

    public void setDesAddress(String desAddress) {
        this.desAddress = desAddress;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        this.Contact = contact;
    }
}
