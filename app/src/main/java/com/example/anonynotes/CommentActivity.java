package com.example.anonynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentActivity extends AppCompatActivity {
    private CommentAdapter adapter;
    private ArrayList<Comment> commentList;
    private RecyclerView recyclerView;
    private String note_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        note_id = getIntent().getStringExtra("note_id");
        Log.d("CommentActivity", "Received note_id: " + note_id);

        // Initialize RecyclerView and adapter
        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(commentList, this);
        recyclerView.setAdapter(adapter);

        // Fetch comments based on noteId
        fetchComments(note_id);

        // Back button logic
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CommentActivity.this, MainActivity.class);
            intent.putExtra("selectedHomeTab", true);
            startActivity(intent);
            finishAffinity();
        });

        // Handle comment submission
        ImageButton btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> submitComment());


        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(commentList, this);
        recyclerView.setAdapter(adapter);

    }


    private void submitComment() {
        // Get the content from the input field
        EditText etCommentContent = findViewById(R.id.editTextText);
        String content = etCommentContent.getText().toString().trim();

        // Validate input
        if (content.isEmpty()) {
            etCommentContent.setError("Comment cannot be empty");
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("username", "Guest"); // Default to "Guest" if not found

        // URL for your API endpoint
        String url = "http://10.0.2.2:8000/api/comments"; // Adjust as needed

        // Create a JSON object to send with the request
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("note_id", note_id);
            jsonBody.put("user_name", username); // Replace with actual user name
            jsonBody.put("content", content);
            jsonBody.put("anonymous", false); // Adjust as needed

            Log.d("CommentActivity", "Submitting comment: " + jsonBody.toString()); // Log the JSON payload

            // Create a new request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // Create a new JsonObjectRequest for posting the comment
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("CommentActivity", "Comment submitted successfully: " + response);
                            fetchComments(note_id); // Refresh the comments list
                            etCommentContent.setText(""); // Clear the input field
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("CommentActivity", "Error submitting comment: " + error.toString());
                            if (error.networkResponse != null) {
                                Log.e("CommentActivity", "Status code: " + error.networkResponse.statusCode);
                                Log.e("CommentActivity", "Response body: " + new String(error.networkResponse.data));
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    // Add any other headers here, like an Authorization header if needed
                    return headers;
                }
            };

            // Add the request to the request queue
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fetchComments(String note_id) {
        // URL for your API endpoint
        String url = "http://10.0.2.2:8000/api/notes/" + note_id + "/comments";

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a new JsonArrayRequest
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Clear existing comments
                            commentList.clear();

                            // Loop through the JSON array to extract individual comments
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject commentObject = response.getJSONObject(i);

                                // Assuming your Comment class has appropriate constructor
                                String id = commentObject.getString("id");
                                String content = commentObject.getString("content");
                                String username = commentObject.getString("username");
                                String dateCreated = commentObject.getString("dateCreated");


                                // Log the ID to debug
                                Log.d("CommentActivity", "Fetched comment with ID: " + id);

                                Comment comment = new Comment(id, content, username, dateCreated);
                                commentList.add(comment);
                            }

                            // Notify the adapter to refresh the list
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("CommentActivity", "Error parsing comments: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CommentActivity", "Error fetching comments: " + error.getMessage());
                    }
                });

        // Add the request to the request queue
        requestQueue.add(jsonArrayRequest);
    }

}