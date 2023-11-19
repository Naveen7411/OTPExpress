package com.example.smscode.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/** @noinspection deprecation*/
public class Common {
    /**
     * Checks whether the device is connected to the internet.
     *
     * @param context The context of the application.
     * @return True if the device is connected to the internet; false otherwise.
     */
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null){
            // Check for network availability
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }


        return false;
    }
}
