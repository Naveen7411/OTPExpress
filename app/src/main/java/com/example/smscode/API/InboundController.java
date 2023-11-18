package com.example.smscode.API;

import static com.example.smscode.MainActivity.apiValues;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smscode.MainActivity;
import com.example.smscode.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InboundController {
     static  final String  API_KEY="a4aac958a8487a56c8e64ba8eedf1b56";
     static final String API_URL="http://www.phantomunion.com:10023/pickCode-api/push/";

    //API Callout to get the AccessToken for the transcation
     public static void getAccessToken( RequestQueue reqQu  ,APIResponseListener responseListener){

        JsonResponseWrapper jsonResponseWrapper = new JsonResponseWrapper();
        String prepareAPIurl = API_URL+"ticket?key="+API_KEY;


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

    public static void getMobileNumber( String countryCode,RequestQueue reqQu,TextView succesText,TextView errorText , Context context){



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
                                       showNotification(context, "New Phone Number", "Received: "+phoneNumber.getString("number") ,1);

                                   }catch (JSONException je){
                                       errorText.setText("Error : error in parsing the json"+je.getMessage());
                                   }
                               }
                       }
                           else{

                               // If not successful, make another API call
                               getMobileNumber(countryCode, reqQu, succesText, errorText,context);
                           }
                       }
                   });
                }else {
                    errorText.setText(response.msg);
                }
            }
        });

    }



   public static void getOTP(String serialNumber,RequestQueue reqQu,TextView succesText,TextView errorText , Context context){

       String prepareAPIurl = API_URL+"sweetWrapper?token="+apiValues.apiTocken+"serialNumber="+serialNumber;

       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,prepareAPIurl, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Log.d("OTP Recived",response.toString());
               try{
                   String code = response.getString("code");
                   String message = response.getString("message");

                   if("200".equals(code)){
                       JSONObject data = response.getJSONObject("data");
                       JSONArray verificationCodeAry = data.getJSONArray("token");
                    if(verificationCodeAry.length()>0){
                        JSONObject  verificationCode = verificationCodeAry.getJSONObject(0);
                        String optValue=verificationCode.getString("vc");
                       if(!(optValue.length()>0)){
                           // If not successful, make another API call
                           getMobileNumber(serialNumber, reqQu, succesText, errorText,context);
                       }else {
                           apiValues.vcOtp = optValue;
                           showNotification(context, "OTP Recived", "Msg: "+optValue ,2);

                       }
                    }
                   }else {
                       // If not successful, make another API call
                       getMobileNumber(serialNumber, reqQu, succesText, errorText,context);
                   }

               }catch(JSONException  e){

                  errorText.setText("Error: Something went Wrong, API Connected but issue in getting the OTP"+ e.getMessage());
               }


           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               errorText.setText("Something went Wrong, while trying to connect with GET OTP API "+error.toString() );

           }

       });
       reqQu.add(jsonObjectRequest);
   }

   public static void banNumber(){

   }



    //Notification
    private static void showNotification(Context context, String title, String message,int notifyId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if Android version is Oreo or higher, and create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        // Set sound for the notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX);



        // Show the notification
        notificationManager.notify(notifyId, builder.build()); // You can use a unique ID for each notification
    }
}
