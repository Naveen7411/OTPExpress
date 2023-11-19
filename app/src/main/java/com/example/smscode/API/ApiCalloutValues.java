package com.example.smscode.API;

/**
 * A data class for holding values related to API calls in the SMS Code application.
 */
public class ApiCalloutValues {
    // API key for authentication
    public String apiKey;

    // API token, contact number, serial number, and OTP values
    public String apiTocken;
    public String contactNumber;
    public String serialNumber;
    public String vcOtp;

    // Current balance information
    public String currentBalance;

    /**
     * Clears all values stored in the class.
     * This method is useful for resetting the values when needed.
     */
    public void ClearValue(){
        this.apiTocken=null;
        this.contactNumber=null;
        this.serialNumber=null;
        this.vcOtp=null;
        this.currentBalance=null;

    }


}
