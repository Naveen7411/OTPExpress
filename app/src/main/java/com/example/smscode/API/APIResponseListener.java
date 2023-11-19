package com.example.smscode.API;

import org.json.JSONException;

/**
 * An interface for classes that want to handle API responses in the SMS Code application.
 */
public interface APIResponseListener
{
    /**
     * Called when an API response is received.
     *
     * @param response The JSON response wrapper containing the API response data.
     * @throws JSONException Thrown if there is an error processing the JSON response.
     */
    void onAPIResponse(JsonResponseWrapper response) throws JSONException;
}
