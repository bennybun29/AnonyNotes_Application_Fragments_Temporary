package com.example.anonynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class startup_page extends AppCompatActivity {
    private Button btnGetStarted;
    private TextView tvWUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_startup_page);

        btnGetStarted = findViewById(R.id.btnGetStarted);
        tvWUsername = findViewById(R.id.tvWUsername);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("username", ""); // Default to empty string if not found

        if (username != null) {
            tvWUsername.setText(username); // Set the username in the TextView
        }

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(startup_page.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
