package com.example.anonynotes;

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

public class EditProfile_Username extends AppCompatActivity {

    private ImageButton btnBackToEditProfile, btnConfirmChanges;
    private EditText etUsername;
    private TextView character_count;
    private String originalUsername; // Store the original username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile_username);

        btnBackToEditProfile = findViewById(R.id.btnBackToEditProfile);
        btnConfirmChanges = findViewById(R.id.btnConfirmChanges);
        character_count = findViewById(R.id.character_count);
        etUsername = findViewById(R.id.etBio);

        // Load current username from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        originalUsername = sharedPreferences.getString("username", null);  // Save original username
        etUsername.setText(originalUsername);

        // Character Counter
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Update character count dynamically
                int length = charSequence.length();
                character_count.setText(length + "/20");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed here
            }
        });

        // Confirm Changes Button
        btnConfirmChanges.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString();
            if (newUsername.isEmpty()) {
                Toast.makeText(EditProfile_Username.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                updateUsernameOnServer(newUsername);
            }
        });

        btnBackToEditProfile.setOnClickListener(v -> checkForUnsavedChanges());
    }

    private void updateUsernameOnServer(String newUsername) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPreferences.getString("auth_token", null);  // Get the stored token
        String user_id = sharedPreferences.getString("user_id", null);

        if (token != null) {
            // Prepare the request URL and headers
            String url = "http://10.0.2.2:8000/api/user/"+ user_id;
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("user_name", newUsername);
                jsonBody.put("auth_token", token); // Pass the token for authentication
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
                                    editor.putString("username", newUsername);
                                    editor.apply();

                                    Toast.makeText(EditProfile_Username.this, "Username updated", Toast.LENGTH_SHORT).show();
                                    // Navigate back to EditProfile or Home
                                    Intent intent = new Intent(EditProfile_Username.this, EditProfile.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(EditProfile_Username.this, "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject jsonObject = new JSONObject(responseBody);

                                if (jsonObject.has("errors")) {
                                    String errorMessage = jsonObject.getJSONArray("errors").getString(0);
                                    if (errorMessage.contains("user_name has already been taken")) {
                                        Toast.makeText(EditProfile_Username.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditProfile_Username.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(EditProfile_Username.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(EditProfile_Username.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token); // Add Authorization header
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } else {
            Toast.makeText(this, "No token found, please login again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        checkForUnsavedChanges(); // Check for unsaved changes before going back
    }

    private void checkForUnsavedChanges() {
        String currentUsername = etUsername.getText().toString();

        if (!currentUsername.equals(originalUsername)) {
            // If there are unsaved changes, show the dialog
            showUnsavedChangesDialog();
        } else {
            Intent intent = new Intent(EditProfile_Username.this, EditProfile.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(EditProfile_Username.this)
                .setTitle("Unsaved Changes")
                .setMessage("Are you sure you want to leave? Unsaved changes will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(EditProfile_Username.this, EditProfile.class);
                    startActivity(intent);
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
