package com.zoepapp;

public class ModelInspectionSite {
    private int inspectionSiteID;
    private String name;
    private String address;
    private String image;
    private String imageThumbnail;
    private int numOfDefects;
    private String lastInpectionDate;
    private String nextInpectionDate;
    private String type;

    // Constructor
    public ModelInspectionSite(
            int inspectionSiteID,
            String name,
            String address,
            String image,
            String imageThumbnail,
            int numOfDefects,
            String lastInpectionDate,
            String nextInpectionDate,
            String type) {
        this.inspectionSiteID = inspectionSiteID;
        this.name = name;
        this.address = address;
        this.image = image;
        this.imageThumbnail = imageThumbnail;
        this.numOfDefects = numOfDefects;
        this.lastInpectionDate = lastInpectionDate;
        this.nextInpectionDate = nextInpectionDate;
        this.type = type;
    }

    // Getter and Setter methods
    public int getInspectionSiteID() {
        return inspectionSiteID;
    }

    public void setInspectionSiteID(int inspectionSiteID) {
        this.inspectionSiteID = inspectionSiteID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public int getNumOfDefects() {
        return numOfDefects;
    }

    public void setNumOfDefects(int numOfDefects) {
        this.numOfDefects = numOfDefects;
    }

    public String getLastInpectionDate() {
        return lastInpectionDate;
    }

    public void setLastInpectionDate(String lastInpectionDate) {
        this.lastInpectionDate = lastInpectionDate;
    }

    public String getNextInpectionDate() {
        return nextInpectionDate;
    }

    public void setNextInpectionDate(String nextInpectionDate) {
        this.nextInpectionDate = nextInpectionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

