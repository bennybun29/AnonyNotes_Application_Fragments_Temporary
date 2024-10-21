package com.example.anonynotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



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
        Log.d("CommentActivity", "Received comment_id: " + note_id);

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


        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(commentList, this);
        recyclerView.setAdapter(adapter);

    }

    private void fetchComments(String note_id) {
        // URL for your API endpoint
        String url = "http://10.0.2.2:8000/notes/";

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