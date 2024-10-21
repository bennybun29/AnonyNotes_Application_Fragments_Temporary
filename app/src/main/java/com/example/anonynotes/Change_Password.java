package com.example.anonynotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class Change_Password extends AppCompatActivity {
    private EditText ETCurrentPassword, ETNewPassword, ETConfirmPassword;
    private Button change_password_button;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences;
    private ImageButton btnBackToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize views
        ETCurrentPassword = findViewById(R.id.ETCurrentPassword);
        ETNewPassword = findViewById(R.id.ETNewPassword);
        ETConfirmPassword = findViewById(R.id.ETConfirmPassword);
        change_password_button = findViewById(R.id.change_password_button);
        btnBackToProfile = findViewById(R.id.btnBackToProfile);

        btnBackToProfile.setOnClickListener(v -> {
            // Return to MainActivity and ensure Profile tab is selected
            Intent intent = new Intent(Change_Password.this, MainActivity.class);
            intent.putExtra("selectProfileTab", true); // Pass data to indicate profile should be selected
            startActivity(intent);
            finishAffinity(); // Optional, if you want to close EditProfile activity
        });

        // Initialize Retrofit
        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                //.baseUrl("http://192.168.100.27:8000/api/")// Replace with your base URL
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Get SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_id = sharedPreferences.getString("user_id", null);
        String token = sharedPreferences.getString("auth_token", null);  // Retrieve the saved token

        // Set button click listener
        change_password_button.setOnClickListener(v -> {
            String currentPassword = ETCurrentPassword.getText().toString();
            String newPassword = ETNewPassword.getText().toString();
            String confirmPassword = ETConfirmPassword.getText().toString();

            if (validateInputs(currentPassword, newPassword, confirmPassword)) {
                // Pass the user_id along with other parameters
                ChangePasswordRequest request = new ChangePasswordRequest(user_id, currentPassword, newPassword, confirmPassword);
                changePassword(token, request);
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Change_Password.this, MainActivity.class);
        intent.putExtra("selectProfileTab", true); // Pass data to indicate profile should be selected
        startActivity(intent);
        finishAffinity(); // Optional: Close this activity completely
    }

    private boolean validateInputs(String currentPassword, String newPassword, String confirmPassword) {
        // Check if any field is empty
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the new password matches the confirm password
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the new password is different from the current password
        if (currentPassword.equals(newPassword)) {
            Toast.makeText(this, "New password must be different from the current password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void changePassword(String token, ChangePasswordRequest request) {
        // Make the call to the backend
        ChangePasswordApi service = retrofit.create(ChangePasswordApi.class);
        Call<ResponseBody> call = service.changePassword("Bearer " + token, request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(Change_Password.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Log the entire error response for debugging
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("ChangePasswordError", "Error: " + errorBody); // Log error
                        Toast.makeText(Change_Password.this, "Password change failed: " + errorBody, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("ChangePasswordError", "Exception: " + e.getMessage()); // Log exception
                    Toast.makeText(Change_Password.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }



            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Change_Password.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Retrofit API definition for changing password
    private interface ChangePasswordApi {
        @POST("change-password")  // Endpoint to change password
        Call<ResponseBody> changePassword(
                @Header("Authorization") String token,
                @Body ChangePasswordRequest request
        );
    }

    // Request body class for change password
    public class ChangePasswordRequest {
        private String user_id; // Add user_id to the request body
        private String current_password;
        private String new_password;
        private String new_password_confirmation;

        public ChangePasswordRequest(String userId, String currentPassword, String newPassword, String confirmPassword) {
            this.user_id = userId;
            this.current_password = currentPassword;
            this.new_password = newPassword;
            this.new_password_confirmation = confirmPassword;
        }
    }
}
