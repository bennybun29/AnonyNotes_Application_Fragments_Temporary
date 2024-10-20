package com.example.anonynotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class EditProfile_Bio extends AppCompatActivity {
    private ImageButton btnBackToEditProfile, btnConfirmChanges;
    private EditText etBio;
    private TextView character_count;
    private SharedPreferences sharedPreferences;
    private String token;
    private String user_id;
    private boolean isBioChanged = false; // Flag to track if bio has been changed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile_bio);

        // Initialize UI components
        btnBackToEditProfile = findViewById(R.id.btnBackToEditProfile);
        btnConfirmChanges = findViewById(R.id.btnConfirmChanges);
        character_count = findViewById(R.id.character_count);
        etBio = findViewById(R.id.etBio);

        // Initialize SharedPreferences and retrieve token and user_id
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sharedPreferences.getString("auth_token", null);
        user_id = sharedPreferences.getString("user_id", null);

        if (token == null || user_id == null) {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load current bio from SharedPreferences
        String currentBio = sharedPreferences.getString("profile_bio", "");
        etBio.setText(currentBio);

        // Add TextWatcher to track changes in bio
        etBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Track if bio was changed
                isBioChanged = !charSequence.toString().equals(currentBio);
                int length = charSequence.length();
                character_count.setText(length + "/40");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed here
            }
        });

        // Set up button click listeners
        btnBackToEditProfile.setOnClickListener(v -> handleBackPress());

        btnConfirmChanges.setOnClickListener(v -> {
            String newBio = etBio.getText().toString();
            if (!newBio.isEmpty() && newBio.length() <= 40) {
                updateUserBioOnServer(newBio);
            } else {
                Toast.makeText(EditProfile_Bio.this, "Bio cannot be empty or exceed 40 characters", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        handleBackPress(); // Show dialog when back is pressed
    }

    private void handleBackPress() {
        if (isBioChanged) {
            showUnsavedChangesDialog(); // Show dialog only if there are unsaved changes
        } else {
            Intent intent = new Intent(EditProfile_Bio.this, EditProfile.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    private void updateUserBioOnServer(String newBio) {
        String url = "http://10.0.2.2:8000/api/user/" + user_id;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("bio", newBio);
            jsonBody.put("auth_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("message").equals("User updated successfully")) {
                                // Update SharedPreferences after success
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("profile_bio", newBio);
                                editor.apply();

                                Toast.makeText(EditProfile_Bio.this, "Bio updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfile_Bio.this, EditProfile.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(EditProfile_Bio.this, "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfile_Bio.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(EditProfile_Bio.this)
                .setTitle("Unsaved Changes")
                .setMessage("Are you sure you want to leave? Unsaved changes will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(EditProfile_Bio.this, EditProfile.class);
                    startActivity(intent);
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
