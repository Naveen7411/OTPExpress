package com.example.smscode.Utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.smscode.R;

/**
 * BroadcastReceiver for detecting changes in network connectivity.
 * When there is no internet connection, it displays a dialog prompting the user to retry.
 */
public class NetworkChangeListener extends BroadcastReceiver {
    /**
     * Called when a change in network connectivity is detected.
     *
     * @param context The context in which the receiver is running.
     * @param intent  The intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Common.isConnectedToInternet(context)){
            // Build an AlertDialog to notify the user about the lack of internet connection

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog,null);
            builder.setView(layout_dialog);
            // Retry button in the dialog
            AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);

            //show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            // Set the dialog to appear at the center of the screen
            dialog.getWindow().setGravity(Gravity.CENTER);

            // Retry button click listener
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context,intent);
                }
            });

        }
    }
}
