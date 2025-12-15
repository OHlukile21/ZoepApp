package com.zoepapp;

public class ModelFacilityIncident {
    private int facilityIncidentID;
    private String findings;
    private String timeFrame;
    private String remedialAction;
    private String imageThumbnail;
    private String severity;
    private String image;
    // Constructor
    public ModelFacilityIncident(
            int facilityIncidentID,
            String findings,
            String timeFrame,
            String image,
            String imageThumbnail,
            String remedialAction,
            String severity) {
        this.facilityIncidentID = facilityIncidentID;
        this.findings = findings;
        this.timeFrame = timeFrame;
        this.image = image;
        this.imageThumbnail = imageThumbnail;
        this.remedialAction = remedialAction;
        this.severity = severity;
    }

    // Getter and Setter methods
    public int getFacilityIncidentID() {
        return facilityIncidentID;
    }

    public void setFacilityIncidentID(int siteIncidentID) {
        this.facilityIncidentID = siteIncidentID;
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

    public String getRemedialAction() {
        return remedialAction;
    }

    public void setRemedialAction(String remedialAction) {
        this.remedialAction = remedialAction;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}

