package com.example.smscode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smscode.API.ApiCalloutValues;
import com.example.smscode.API.InboundController;

import com.example.smscode.Utility.NetworkChangeListener;

public class MainActivity extends AppCompatActivity {

    private  String selectedCountryName =null;
    private  String selectedCountryCode = null;

    public static ApiCalloutValues apiValues = new ApiCalloutValues();
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Load Country DropdownValue
        Spinner countrySpinner = findViewById(R.id.countrySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner.setAdapter(adapter);



        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 selectedCountryName = parentView.getItemAtPosition(position).toString();
                 selectedCountryCode = getResources().getStringArray(R.array.country_codes)[position];
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });



        RequestQueue requestQueue = Volley.newRequestQueue(this);

        TextView errorText = findViewById(R.id.errorText);
        TextView recivedNumber = findViewById(R.id.recivedNumber);
        TextView recivedOTP = findViewById(R.id.recivedOTP);
        //GetNumber Button
        Button getNumberBtn =  (Button) findViewById(R.id.getNumber);
        //GetOTP Button
        Button getOTPBtn =  (Button) findViewById(R.id.getOtp);
        //BanNumber Button
        Button getBanBtn =  (Button) findViewById(R.id.banNumber);

        /*@author: Naveen Kumar R
        * @desc: below Piece of code reponsible for geting mobile number
        * */
        getNumberBtn.setOnClickListener(view -> {
            errorText.setText("");
            recivedNumber.setText("");
            recivedOTP.setText("");
            apiValues.ClearValue();

            if(selectedCountryCode!=null && selectedCountryName !=null && selectedCountryCode.length()>0){
                InboundController.getMobileNumber(selectedCountryCode,requestQueue,recivedNumber,errorText,MainActivity.this);
            }else{
                Toast.makeText(getApplicationContext(),
                        "Please select the country",
                        Toast.LENGTH_SHORT).show();
            }



        });


        getOTPBtn.setOnClickListener(view -> {

            if(apiValues.apiTocken!=null && apiValues.contactNumber!=null && apiValues.serialNumber!=null){
                InboundController.getOTP(apiValues.serialNumber,requestQueue,recivedNumber,errorText,MainActivity.this);
            }
            else{
                     Toast.makeText(getApplicationContext(),
                        "First get the number and try to get OTP",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }



}