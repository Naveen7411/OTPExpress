package com.example.smscode.API;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a wrapper for JSON responses from API calls.
 */
public class JsonResponseWrapper {
    // Fields to store various components of the JSON response

    /**
     * The status code indicating the outcome of the API call.
     */
    String code;
    /**
     * A human-readable message providing additional information about the API call.
     */
    String msg;
    /**
     * A token retrieved from the API response, if applicable.
     */
    String token;

    /**
     * The overall success status of the API call.
     */
    Boolean status;

    /**
     * An array of JSON data included in the response, if applicable.
     */
    JSONArray  data;
}


