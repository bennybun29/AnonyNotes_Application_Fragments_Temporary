package com.example.anonynotes;

import static androidx.core.app.PendingIntentCompat.getActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    private ImageButton btnBackToProfilePage;
    private EditText etEmail, editUsername, editBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);

        btnBackToProfilePage = findViewById(R.id.btnBackToEditProfile);
        etEmail = findViewById(R.id.etEmail);
        editBio = findViewById(R.id.editBio);
        editUsername = findViewById(R.id.editUsername);

        btnBackToProfilePage.setOnClickListener(v -> {
            // Return to MainActivity and ensure Profile tab is selected
            Intent intent = new Intent(EditProfile.this, MainActivity.class);
            intent.putExtra("selectProfileTab", true); // Pass data to indicate profile should be selected
            startActivity(intent);
            finishAffinity(); // Optional, if you want to close EditProfile activity
        });




        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("username", null);
        String email = sharedPreferences.getString("email", null);
        String bio = sharedPreferences.getString("bio", null);

        etEmail.setText(email);
        editUsername.setText(username);

        // Set bio if it exists, otherwise set the hint
        if (bio != null && !bio.isEmpty()) {
            editBio.setText(bio);
        } else {
            editBio.setHint("Add your bio");
        }

        editUsername.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfile.this, EditProfile_Username.class);
            startActivity(intent);
            finishAffinity();
        });

        editBio.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfile.this, EditProfile_Bio.class);
            startActivity(intent);
            finishAffinity();
        });


    }

}
