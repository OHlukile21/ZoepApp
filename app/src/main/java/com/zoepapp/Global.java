package com.zoepapp;

public class Global {
    public static String BASE_URL = "http://app-dev.trinity-isp.co.za/api/";
	public static String LOGIN_URL = BASE_URL + "Account/Login";
	public static String ADD_SITE_URL = BASE_URL + "InspectionSite/Add";
//	public static String ADD_INCIDENT_URL = BASE_URL + "SiteIncident/Add";
public static String ADD_INCIDENT_URL = BASE_URL + "FacilityIncident/Add";
//	public static String USER_INSPECTION_SITES_URL = BASE_URL + "InspectionSite/GetInspectionSitesByUserID";
public static String USER_FACILITIES_URL = BASE_URL + "Facility/GetGetFacilitiesByBcaID";
//	public static String SITE_INCIDENTS_URL = BASE_URL + "SiteIncident/GetSiteIncidentsBySiteID";
public static String FACILITY_INCIDENTS_URL = BASE_URL + "FacilityIncident/GetSiteIncidentsByFacilityID";
	public static String SITE_INCIDENT_DELETE = BASE_URL + "SiteIncident/Delete";


}
