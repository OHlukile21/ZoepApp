package com.zoepapp;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


public class Utilities {

    public static boolean checkConnectivity(Context ctx) {

        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected() && ni.isAvailable())
            return true;
        else
            return false;

    }




// to close the keyboard
    public static  void closeKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false; // Null or empty emails are invalid
        }

        // Regular expression for validating an email address
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Use Pattern to compile and match the regex
        Pattern pattern = Pattern.compile(emailRegex);

        // Return true if email matches the regex, otherwise false
        return pattern.matcher(email).matches();
    }


    // Static method to show custom snackbar
    public static void showCustomSnackbar(Activity activity, String message, int duration, int icon, int textColor ,int backgroundColor, int position) {
        // Create Snackbar with a custom duration and empty message (message will be set later)
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "", duration);

        View snackbarView = snackbar.getView();

        // Inflate custom view layout for the Snackbar
        LayoutInflater inflater = LayoutInflater.from(activity);
        View customView = inflater.inflate(R.layout.custom_snackbar, null);

        // Set custom background color
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_transparent));

        // Set the card background color for the snackbar
        MaterialCardView cardView = customView.findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(ContextCompat.getColor(activity, backgroundColor));

        // Set the message text in the custom view
        TextView messageTextView = customView.findViewById(R.id.snackbar_text);
        messageTextView.setTextColor(ContextCompat.getColor(activity, textColor));
        messageTextView.setText(message);

        // Set the icon for the snackbar
        ImageView iconImageView = customView.findViewById(R.id.snackbar_icon);
        iconImageView.setImageResource(icon);

        // Set up an action if required (optional)
        TextView actionTextView = customView.findViewById(R.id.snackbar_action);
        actionTextView.setOnClickListener(v -> {
            snackbar.dismiss();  // You can perform a custom action here, for example, dismissing the snackbar
        });

        // Remove default Snackbar's text view (this is important to avoid duplication)
        snackbarLayout.removeAllViews();

        // Add the custom view to the snackbar layout
        snackbarLayout.addView(customView, 0);


        // Adjust parameters if the parent is FrameLayout
        if (snackbarView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = position; // Position at the top
            snackbarView.setLayoutParams(params);
        }
        // Show the custom snackbar
        snackbar.show();
    }

}
