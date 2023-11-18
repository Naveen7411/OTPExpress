package com.example.smscode.API;

import org.json.JSONException;

public interface APIResponseListener
{
    void onAPIResponse(JsonResponseWrapper response) throws JSONException;
}
