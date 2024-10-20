package com.example.anonynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private TextView notificationBubble;
    private Handler handler = new Handler();
    private Runnable runnable;
    private final long SPLASH_DISPLAY_LENGTH = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);  // Removed EdgeToEdge.enable(this) for now

        notificationBubble = findViewById(R.id.notification_bubble);

        // Check if the auth token is already saved in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this); // Updated
        String authToken = preferences.getString("auth_token", null);

        if (authToken != null) {
            // User is already logged in, navigate to the Home Page
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity to prevent going back to splash screen
        } else {
            // Delay check for 3 seconds if not logged in
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkInternetConnection();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    private void checkInternetConnection() {
        if (isConnected()) {
            startSign_Up();
        } else {
            showNotificationBubble("Not connected to the internet");
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (isConnected()) {
                        hideNotificationBubble();
                        startSign_Up();
                    } else {
                        handler.postDelayed(runnable, 3000);
                    }
                }
            };
            handler.postDelayed(runnable, 3000);
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showNotificationBubble(String message) {
        notificationBubble.setText(message);
        notificationBubble.setVisibility(View.VISIBLE);
    }

    private void hideNotificationBubble() {
        notificationBubble.setVisibility(View.GONE);
    }

    private void startSign_Up() {
        Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
