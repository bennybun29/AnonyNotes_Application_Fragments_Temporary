package com.example.anonynotes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Change_Password extends AppCompatActivity {
    private ImageButton btnBackToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnBackToProfile = findViewById(R.id.btnBackToProfile);


        btnBackToProfile.setOnClickListener(v -> {
            // Return to MainActivity and ensure Profile tab is selected
            Intent intent = new Intent(Change_Password.this, MainActivity.class);
            intent.putExtra("selectProfileTab", true); // Pass data to indicate profile should be selected
            startActivity(intent);
            finishAffinity(); // Optional, if you want to close EditProfile activity
        });
    }
}
