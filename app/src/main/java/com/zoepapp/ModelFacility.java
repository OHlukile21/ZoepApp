package com.zoepapp;

public class ModelFacility {
    private int facilityID;
    private String name;
    private String address;
    private int numOfIncidents;
    private String type;

    // Constructor
    public ModelFacility(
            int facilityID,
            String name,
            String address,
            int numOfIncidents,   // ✔️ Corrected name
            String type) {

        this.facilityID = facilityID;
        this.name = name;
        this.address = address;
        this.numOfIncidents = numOfIncidents; // ✔️ Correct assignment
        this.type = type;
    }

    // Getter and Setter methods
    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
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

    public int getNumOfIncidents() {
        return numOfIncidents;
    }

    public void setNumOfIncidents(int numOfIncidents) {
        this.numOfIncidents = numOfIncidents;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}