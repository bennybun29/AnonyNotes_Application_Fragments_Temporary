package com.example.anonynotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageButton burgerButton;
    private Button account_status_btn, change_password_btn, logout_btn, editProfileButton;
    private TextView tvUsernameProfile, tvBioProfile;
    private LinearLayout menuOptions;
    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private List<Note> notesList;
    private View blurOverLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        burgerButton = view.findViewById(R.id.burgerButton);
        tvBioProfile = view.findViewById(R.id.tvBioProfile);
        tvUsernameProfile = view.findViewById(R.id.tvUsernameProfile);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        menuOptions = view.findViewById(R.id.menu_options);
        recyclerView = view.findViewById(R.id.recyclerView);
        blurOverLay =view.findViewById(R.id.blurOverLay);

        // Initially hide the menu
        menuOptions.setVisibility(View.GONE);
        blurOverLay.setVisibility(View.GONE);

        // Toggle menu visibility with animation
        burgerButton.setOnClickListener(v -> {
            if (menuOptions.getVisibility() == View.GONE) {
                // Set the views to be initially invisible and transparent
                menuOptions.setVisibility(View.VISIBLE);
                menuOptions.setAlpha(0f);  // Transparent
                blurOverLay.setVisibility(View.VISIBLE);
                blurOverLay.setAlpha(0f);  // Transparent

                // Animate the alpha to make the views visible with a fade-in effect
                menuOptions.animate()
                        .alpha(1f)  // Fully visible
                        .setDuration(300)  // Duration of the animation in milliseconds
                        .start();
                blurOverLay.animate()
                        .alpha(1f)  // Fully visible
                        .setDuration(300)  // Same duration as the menuOptions
                        .start();
            } else {
                // Fade out animation
                menuOptions.animate()
                        .alpha(0f)  // Fully transparent
                        .setDuration(300)
                        .withEndAction(() -> menuOptions.setVisibility(View.GONE))  // Set to GONE after animation
                        .start();
                blurOverLay.animate()
                        .alpha(0f)  // Fully transparent
                        .setDuration(300)
                        .withEndAction(() -> blurOverLay.setVisibility(View.GONE))  // Set to GONE after animation
                        .start();
            }
        });


        // Close menu when clicking outside (on the blur overlay)
        blurOverLay.setOnClickListener(v -> {
            menuOptions.setVisibility(View.GONE); // Hide menu
            blurOverLay.setVisibility(View.GONE); // Hide blur overlay
        });


        account_status_btn = view.findViewById(R.id.account_status);
        change_password_btn = view.findViewById(R.id.change_password);
        logout_btn = view.findViewById(R.id.logout);

        account_status_btn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Account_Status.class);
            startActivity(intent);
        });

        change_password_btn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Change_Password.class);
            startActivity(intent);
            requireActivity().finish();
        });

        logout_btn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Logout Confirmation")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear SharedPreferences on logout
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();  // Remove the saved token
                        editor.apply();

                        // User confirmed logout, perform the logout action
                        Intent intent = new Intent(requireActivity(), LogInActivity.class);
                        startActivity(intent);
                        requireActivity().finishAffinity();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // User canceled the logout action, dismiss the dialog
                        dialog.dismiss();
                    })
                    .show();
        });



        // Initialize notes list and adapter
        notesList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(requireContext(), notesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(profileAdapter);

        // Edit profile button click
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfile.class);
            startActivity(intent);
        });

        // Fetch username from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String username = sharedPreferences.getString("username", null);
        tvUsernameProfile.setText(username);

        // Fetch user_id from shared preferences
        String userId = sharedPreferences.getString("user_id", null);

        // Fetch notes for the user
        if (userId != null) {
            fetchUserNotes(userId);
            fetchUserBio(userId);
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchUserBio(String userId) {
        String url = "http://10.0.2.2:8000/api/user/" + userId + "/bio"; // Adjust the endpoint as per your API

        // Initialize request queue
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String bio = response.getString("bio"); // Adjust the JSON key as per your API response
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (bio != null && !bio.isEmpty() && bio != "null") {
                            tvBioProfile.setText(bio); // Set the fetched bio to the TextView
                            editor.putString("profile_bio", bio);
                            editor.apply();// Save the bio in SharedPreferences
                        } else {
                            tvBioProfile.setHint("Edit your bio using Edit Profile button"); // Display the hint
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing bio", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching bio", Toast.LENGTH_SHORT).show();
                });

        // Add request to the queue
        requestQueue.add(jsonObjectRequest);
    }


    private void fetchUserNotes(String userId) {
        String url = "http://10.0.2.2:8000/api/users/" + userId + "/notes"; // Construct the URL

        // Initialize request queue
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // Create a JsonArrayRequest
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    notesList.clear(); // Clear existing notes
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject noteObject = response.getJSONObject(i);
                            String noteId = noteObject.getString("note_id");
                            String noteContent = noteObject.getString("content");
                            String noteDateCreated = noteObject.getString("created_at");
                            String noteUsername = noteObject.getString("user_name");

                            Note note = new Note(noteUsername, noteDateCreated, noteContent);
                            note.setId(noteId);
                            notesList.add(0, note);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing note", Toast.LENGTH_SHORT).show();
                        }
                    }
                    profileAdapter.notifyDataSetChanged();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching notes", Toast.LENGTH_SHORT).show();
                });

        // Add request to the queue
        requestQueue.add(jsonArrayRequest);
    }
}
