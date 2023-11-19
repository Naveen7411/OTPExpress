package com.example.smscode.API;

import static com.example.smscode.MainActivity.apiValues;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.smscode.MainActivity;
import com.example.smscode.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Controller class for handling inbound API calls and related actions.
 */
public class InboundController {
    // static  final String  API_KEY= apiValues.apiKey;

    // Base URL for API calls
     static final String API_URL="http://www.phantomunion.com:10023/pickCode-api/push/";


    /**
     * API call to get the access token for the transaction.
     *
     * @param reqQu             The Volley request queue.
     * @param responseListener The listener for handling API responses.
     */
     public static void getAccessToken( RequestQueue reqQu  ,APIResponseListener responseListener){
         String API_Key = apiValues.apiKey;
        JsonResponseWrapper jsonResponseWrapper = new JsonResponseWrapper();
        String prepareAPIurl = API_URL+"ticket?key="+API_Key;

         Log.d("getAccess URL", prepareAPIurl);
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,prepareAPIurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Update",response.toString());
                try{
                    String code = response.getString("code");
                    String message = response.getString("message");

                    if("200".equals(code)){
                        JSONObject data = response.getJSONObject("data");
                        String token = data.getString("token");
                        jsonResponseWrapper.status=true;
                        jsonResponseWrapper.code=code;
                        jsonResponseWrapper.msg=message;
                        jsonResponseWrapper.token=token;
                        apiValues.apiTocken=token;

                    }else {
                        jsonResponseWrapper.status=false;
                        jsonResponseWrapper.msg=message;
                    }

                }catch(JSONException  e){
                    jsonResponseWrapper.status=false;
                    jsonResponseWrapper.msg="Error: Something went Wrong, API Connected but issue in getting the token"+ e.getMessage();
                }
                // Notify the response listener

                try {
                    responseListener.onAPIResponse(jsonResponseWrapper);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Update",error.toString());
                jsonResponseWrapper.status=false;
                jsonResponseWrapper.msg="Something went Wrong, while trying to connect with API "+error.toString() ;
                // Notify the response listener
                try {
                    responseListener.onAPIResponse(jsonResponseWrapper);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        });

         reqQu.add(jsonObjectRequest);


    }
    //API Callout to get the Mobile number fro the given token

    /**
     * API call to get a phone number based on the access token and country code.
     *
     * @param response          The API response wrapper containing the access token.
     * @param countryCode       The country code for which the phone number is requested.
     * @param reqQu             The Volley request queue.
     * @param responseListener The listener for handling API responses.
     */
    public  static void getNumber(JsonResponseWrapper response,String countryCode, RequestQueue reqQu  ,APIResponseListener responseListener){



        JsonResponseWrapper jsonResponseWrapper = new JsonResponseWrapper();

        String prepareAPIurl = API_URL+"buyCandy?token="+response.token+"&businessCode=10079&quantity=1&country="+countryCode+"&effectiveTime=120";

        Log.d("Number API", prepareAPIurl+"");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,prepareAPIurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("success", response+"");
                try{
                    String code = response.getString("code");
                    String message = response.getString("message");

                    if("200".equals(code)){
                        jsonResponseWrapper.status=true;
                        jsonResponseWrapper.code=code;
                        jsonResponseWrapper.msg=message;
                        JSONObject data = response.getJSONObject("data");;
                        jsonResponseWrapper.data= data.getJSONArray("phoneNumber");
                        apiValues.currentBalance= data.getString("balance");

                    }else {
                        jsonResponseWrapper.status=false;
                        jsonResponseWrapper.msg=message;
                    }

                }catch(JSONException  e){
                    jsonResponseWrapper.status=false;
                    jsonResponseWrapper.msg="Error: Something went Wrong, API Connected but issue in getting the Number";

                }
                // Notify the response listener

                try {
                    responseListener.onAPIResponse(jsonResponseWrapper);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error 2 nd call",error.toString());
                jsonResponseWrapper.status=false;
                jsonResponseWrapper.msg="Something went Wrong, while trying to connect with Get Number API";
                // Notify the response listener
                try {
                    responseListener.onAPIResponse(jsonResponseWrapper);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        reqQu.add(jsonObjectRequest);
    }

    /**
     * Initiates the process of getting a mobile number, including handling access token retrieval.
     *
     * @param countryCode       The country code for which the phone number is requested.
     * @param reqQu             The Volley request queue.
     * @param succesText        The TextView to display the successful result.
     * @param errorText         The TextView to display error messages.
     * @param context           The context of the application.
     * @param retryTime         The time until which retry attempts can be made.
     */
    public static void getMobileNumber( String countryCode,RequestQueue reqQu,TextView succesText,TextView errorText , Context context,long retryTime){



        getAccessToken(reqQu, new APIResponseListener() {
            @Override
            public void onAPIResponse(JsonResponseWrapper response) {


                Log.d("Get Tocken Status", response.status+"");
                if(response.status)
                {
                   getNumber(response, countryCode, reqQu, new APIResponseListener() {
                       @Override
                       public void onAPIResponse(JsonResponseWrapper response)  {
                           Log.d("Get Number Status ", response.status+"");
                           if(response.status){


                               JSONArray phoneNumberArray = response.data;
                               if(phoneNumberArray.length()>0){
                                   try{
                                 JSONObject  phoneNumber = phoneNumberArray.getJSONObject(0);

                                       succesText.setText(phoneNumber.getString("number"));
                                       apiValues.contactNumber=phoneNumber.getString("number");
                                       apiValues.serialNumber=phoneNumber.getString("serialNumber");

                                       // Enable the UI element on the main thread
                                       enableUIElement(context,1,apiValues.currentBalance);
                                       showNotification(context, "New Phone Number", "Received: "+phoneNumber.getString("number") ,1);

                                   }catch (JSONException je){
                                       // Enable the UI element on the main thread
                                       enableUIElement(context,1,null);
                                       errorText.setText("Error : error in parsing the json"+je.getMessage());
                                   }
                               }
                       }
                           else{

                               // If not successful, make another API call
                               if(System.currentTimeMillis()<= retryTime)
                                    getMobileNumber(countryCode, reqQu, succesText, errorText,context,retryTime);
                               else{
                                   // Enable the UI element on the main thread
                                   enableUIElement(context,1,null);
                                   errorText.setText("Time out for requesting the number.Please try again..!");
                               }

                           }
                       }
                   });
                }else {
                    // Enable the UI element on the main thread
                    enableUIElement(context,1,null);
                    errorText.setText(response.msg);
                }
            }
        });




    }




    /**
     * API call to get an OTP (One-Time Password) based on the serial number and access token.
     *
     * @param serialNumber      The serial number associated with the phone number.
     * @param reqQu             The Volley request queue.
     * @param succesText        The TextView to display the successful result.
     * @param errorText         The TextView to display error messages.
     * @param context           The context of the application.
     * @param retryTime         The time until which retry attempts can be made.
     */
    public static void getOTP(String serialNumber,RequestQueue reqQu,TextView succesText,TextView errorText , Context context,long retryTime){

       String prepareAPIurl = API_URL+"sweetWrapper?token="+apiValues.apiTocken+"&serialNumber="+serialNumber;

        Log.d("OTP: API ",prepareAPIurl);
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,prepareAPIurl, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Log.d("OTP Recived",response.toString() +("Retry==>"+retryTime+"Actual==>"+System.currentTimeMillis()));
               try{
                   String code = response.getString("code");
                   String message = response.getString("message");

                   if("200".equals(code)){
                       JSONObject data = response.getJSONObject("data");
                       JSONArray verificationCodeAry = data.getJSONArray("verificationCode");
                    if(verificationCodeAry.length()>0){
                        JSONObject  verificationCode = verificationCodeAry.getJSONObject(0);
                        String optValue=verificationCode.getString("vc");
                       if(!(optValue.length()>0)){
                           // If not successful, make another API call
                           if(retryTime>= System.currentTimeMillis())
                               getOTP(serialNumber, reqQu, succesText, errorText,context,retryTime);
                           else{
                               enableUIElement(context,2,null);
                               errorText.setText("Time out for requesting OTP.,Please try again..!");
                           }

                       }else {
                           // Enable the UI element on the main thread
                           enableUIElement(context,2,null);
                           apiValues.vcOtp = optValue;
                           succesText.setText(optValue);
                           showNotification(context, "OTP Recived", "Msg: "+optValue ,2);

                       }
                    }
                   }else {
                       // If not successful, make another API call
                       if(retryTime>= System.currentTimeMillis())
                           getOTP(serialNumber, reqQu, succesText, errorText,context,retryTime);
                       else{
                           enableUIElement(context,2,null);
                           errorText.setText("Time out for requesting OTP.,Please try again..!");
                       }

                   }

               }catch(JSONException  e){
                   enableUIElement(context,2,null);
                  errorText.setText("Error: Something went Wrong, API Connected but issue in getting the OTP /n "+ e.getMessage());
               }


           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               enableUIElement(context,2,null);
               errorText.setText("Something went Wrong, while trying to connect with GET OTP API "+error.toString() );

           }

       });
       reqQu.add(jsonObjectRequest);
   }
    /**
     * API call to ban a phone number based on the serial number and access token.
     *
     * @param serialNumber      The serial number associated with the phone number.
     * @param reqQu             The Volley request queue.
     * @param errorText         The TextView to display error messages.
     * @param context           The context of the application.
     * @param retryTime         The time until which retry attempts can be made.
     */
   public static void banNumber(String serialNumber,RequestQueue reqQu,TextView errorText , Context context,long retryTime){
       String prepareAPIurl = API_URL+"redemption?token="+apiValues.apiTocken+"&serialNumber="+serialNumber+"&feedbackType=A&description=fail number";
        Log.d("BAN: API ",prepareAPIurl);
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,prepareAPIurl, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Log.d("BAN Response",response.toString() +("Retry==>"+retryTime+"Actual==>"+System.currentTimeMillis()));
               try{
                   String code = response.getString("code");
                   String message = response.getString("message");

                   if("200".equals(code)){
                       Toast.makeText(context.getApplicationContext(),
                               apiValues.contactNumber+" : number has been successfully banned.",
                               Toast.LENGTH_SHORT).show();


                       enableUIElement(context,3,null);
                   }else {
                       // If not successful, make another API call
                       if(retryTime>= System.currentTimeMillis())
                           banNumber(serialNumber, reqQu, errorText,context,retryTime);
                       else{
                           enableUIElement(context,3,null);
                           errorText.setText("Time out for Ban API Call.,Please try again..!");
                       }

                   }

               }catch(JSONException  e){
                   enableUIElement(context,3,null);
                   errorText.setText("Error: Something went Wrong, API Connected but issue in Ban /n "+ e.getMessage());
               }


           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               enableUIElement(context,3,null);
               errorText.setText("Something went Wrong, while trying to connect with GET BAN API "+error.toString() );


           }

       });
       reqQu.add(jsonObjectRequest);


   }


    /**
     * Displays a notification with the given title and message.
     *
     * @param context   The context of the application.
     * @param title     The title of the notification.
     * @param message   The message content of the notification.
     * @param notifyId  The ID for notification identification.
     */
    //Notification
    private static void showNotification(Context context, String title, String message, int notifyId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if Android version is Oreo or higher, and create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        // Set sound for the notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create an explicit intent for an Activity in your app.

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.baseline_contactless_24)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSoundUri)
               .setStyle(new NotificationCompat.BigTextStyle()
                      .bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);



        // Show the notification
        notificationManager.notify(notifyId, builder.build()); // You can use a unique ID for each notification
    }


    /**
     * Enables UI elements on the main thread based on the provided index.
     *
     * @param context   The context of the application.
     * @param btnIndex  The index indicating which UI element to enable.
     * @param bal       The balance information to be displayed (can be null).
     */
    //Button Enable
    // Method to enable UI element on the main thread
    private static void enableUIElement(Context context,int btnIndex,String bal) {
        ((Activity) context).runOnUiThread(() -> {
            // Enable your UI element here, e.g., a button
            switch (btnIndex){
                case 1: Button getNumberBtn = ((Activity) context).findViewById(R.id.getNumber);
                        getNumberBtn.setEnabled(true);
                        break;
                case 2:  Button getOTPBtn = ((Activity) context).findViewById(R.id.getOtp);
                    getOTPBtn.setEnabled(true);
                    break;
                case 3:

                    TextView recivedNumber = ((Activity) context).findViewById(R.id.recivedNumber);
                    TextView recivedOTP = ((Activity) context).findViewById(R.id.recivedOTP);
                    TextView currentBalance = ((Activity) context).findViewById(R.id.accBalText);


                    recivedNumber.setText("");
                    recivedOTP.setText("");
                    currentBalance.setText("Current Balance : - ");
                    apiValues.ClearValue();

                    Button getBanBtn = ((Activity) context).findViewById(R.id.banNumber);
                    getBanBtn.setEnabled(true);
                    break;
            }

            if(bal!=null)
            {
                TextView currentBalance = ((Activity) context).findViewById(R.id.accBalText);
                currentBalance.setText("Current Balance : "+bal);
            }

        });
    }



}
