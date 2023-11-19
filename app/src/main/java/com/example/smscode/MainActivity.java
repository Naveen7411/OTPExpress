package com.example.smscode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smscode.API.ApiCalloutValues;
import com.example.smscode.API.InboundController;

import com.example.smscode.Utility.NetworkChangeListener;

/**
 * The main activity of the SMS Code [OTP Express] application.
 *
 */
public class MainActivity extends AppCompatActivity {
    // Variables to store selected country information
    private  String selectedCountryName =null;
    private  String selectedCountryCode = null;

    // Singleton instance for API callout values
    public static ApiCalloutValues apiValues = new ApiCalloutValues();

    // Network change listener to detect network connectivity changes
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    // EditText fields for user input
    private EditText inputEditText;
    private EditText displayEditText;

    // SharedPreferences constants
    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_USER_VALUE = "userValue";


    /**
     * @author:NaveenKumar.R
     * Called when the activity is first created.
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize EditText fields
        inputEditText = findViewById(R.id.editApiKey);
        displayEditText = findViewById(R.id.displayEditText);


        // Load the saved value from SharedPreferences
        loadSavedValue();


        //Load Country DropdownValue
        Spinner countrySpinner = findViewById(R.id.countrySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner.setAdapter(adapter);


        // Set listener for the country spinner
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 selectedCountryName = parentView.getItemAtPosition(position).toString();
                 selectedCountryCode = getResources().getStringArray(R.array.country_codes)[position];
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
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
       // TextView currentBalance = findViewById(R.id.accBalText);

        //GetNumber Button
        Button getNumberBtn =  (Button) findViewById(R.id.getNumber);
        //GetOTP Button
        Button getOTPBtn =  (Button) findViewById(R.id.getOtp);
        //BanNumber Button
        Button getBanBtn =  (Button) findViewById(R.id.banNumber);

        /*
        * @desc: below Piece of code responsible for getting mobile number
        * */
        getNumberBtn.setOnClickListener(view -> {



            errorText.setText("");
            recivedNumber.setText("");
            recivedOTP.setText("");
            apiValues.ClearValue();

            if(selectedCountryCode!=null && selectedCountryName !=null && selectedCountryCode.length()>0){
                view.setEnabled(false);
                InboundController.getMobileNumber(selectedCountryCode,requestQueue,recivedNumber,errorText,MainActivity.this,(System.currentTimeMillis() + 30000));



            }else{
                Toast.makeText(getApplicationContext(),
                        "Please select the country",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * @desc: below Piece of code responsible for getting OTP for the  number
         * */
        getOTPBtn.setOnClickListener(view -> {
            errorText.setText("");
            if(apiValues.apiTocken!=null && apiValues.contactNumber!=null && apiValues.serialNumber!=null){
                view.setEnabled(false);
                InboundController.getOTP(apiValues.serialNumber,requestQueue,recivedOTP,errorText,MainActivity.this,(System.currentTimeMillis() + (30000*5)));
            }
            else{
                     Toast.makeText(getApplicationContext(),
                        "Please retrieve a phone number before requesting an OTP.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * @desc: below Piece of code responsible for Banning mobile number
         * */
        getBanBtn.setOnClickListener(view -> {


            if(apiValues.apiTocken!=null && apiValues.contactNumber!=null && apiValues.serialNumber!=null){
                view.setEnabled(false);
                InboundController.banNumber(apiValues.serialNumber,requestQueue,errorText,MainActivity.this,(System.currentTimeMillis() + (15000)));
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "Please retrieve a phone number before attempting to ban..!",
                        Toast.LENGTH_SHORT).show();
            }
        });



    }

    /**
     * Loads the previously saved value from SharedPreferences and displays it.
     */
    private void loadSavedValue() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedValue = preferences.getString(KEY_USER_VALUE, "");
        if(savedValue.length()>0){
            /*
            inputEditText.setEnabled(false);
            onSaveAPIBtn.setEnabled(false);*/
            apiValues.apiKey=savedValue;
        }
        // Display the saved value in the EditText
        displayEditText.setText(savedValue);
    }

    /**
     * Saves the user input to SharedPreferences.
     *
     * @param view The view that triggered this method (Save button).
     */
    public void onSaveKey(View view) {
        String userInput = inputEditText.getText().toString();

        // Save the user input to SharedPreferences
        saveValueToSharedPreferences(KEY_USER_VALUE, userInput);

        // Load the saved value and display it
        loadSavedValue();
    }

    /**
     * Saves a value to SharedPreferences.
     *
     * @param key   The key to associate with the value.
     * @param value The value to save.
     */
    private void saveValueToSharedPreferences(String key, String value) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
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