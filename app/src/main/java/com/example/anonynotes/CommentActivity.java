    package com.example.anonynotes;

    import android.annotation.SuppressLint;
    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.preference.PreferenceManager;
    import android.text.Editable;
    import android.text.TextUtils;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.View;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

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

    import java.io.IOException;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;
    import java.util.Scanner;
    import java.util.TimeZone;

    import okhttp3.Call;
    import okhttp3.Callback;
    import okhttp3.MediaType;
    import okhttp3.OkHttpClient;
    import okhttp3.RequestBody;


    public class CommentActivity extends AppCompatActivity {
        private CommentAdapter adapter;
        private ArrayList<Comment> commentList;
        private RecyclerView recyclerView;
        private String note_id, userName, dateCreated, content, time;
        private TextView tvTitle, tvHeartCounter, tvMainUsername, tvdateCreated, tvNoteTime, tvNote, seeMoreLess, tvcharCount;
        private boolean isExpanded = false;
        private EditText etCommentContent;
        private Spinner spinnerSort;
        private ImageButton heartButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_comment);

            note_id = getIntent().getStringExtra("note_id");
            userName = getIntent().getStringExtra("userName");
            dateCreated = getIntent().getStringExtra("dateCreated");
            content = getIntent().getStringExtra("content");
            time = getIntent().getStringExtra("time");
            Log.d("CommentActivity", "Received: " + note_id + " " + userName + " " + dateCreated + " " + content);
            tvTitle = findViewById(R.id.tvTitle);
            tvMainUsername = findViewById(R.id.tvMainUsername);
            tvdateCreated = findViewById(R.id.tvdateCreated);
            tvNoteTime = findViewById(R.id.tvNoteTime);
            tvNote = findViewById(R.id.tvNote);
            seeMoreLess = findViewById(R.id.seeMoreLess);
            tvHeartCounter = findViewById(R.id.tvHeartCounter);
            fetchHeartCount(note_id, tvHeartCounter);
            tvTitle.setText(userName + "'s Note Comments");
            tvMainUsername.setText(userName);
            tvNote.setText(content);

            ImageButton heartButton = findViewById(R.id.heartButton);
            boolean isLiked = getIntent().getBooleanExtra("isLiked", false);
            heartButton.setImageResource(isLiked ? R.drawable.heart_filled : R.drawable.heartbutton);

            // Parse and format the dateCreated using SimpleDateFormat
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the timezone for input format

            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // 12-hour format with AM/PM
            outputTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Set the timezone for output format

            try {
                // Parse the full timestamp into a Date object
                Date date = inputFormat.parse(dateCreated);

                // Format the date and time to display them separately
                String formattedDate = outputDateFormat.format(date); // e.g., "2023-10-23"
                String formattedTime = outputTimeFormat.format(date); // e.g., "04:30 PM"

                // Set the formatted date and time in the TextViews
                tvdateCreated.setText(formattedDate);
                tvNoteTime.setText(formattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
                // In case of an error, show the full timestamp as fallback
                tvdateCreated.setText(dateCreated);
                tvNoteTime.setText("Time error");
            }

            if (content.length() > 100) {
                tvNote.setMaxLines(3);
                tvNote.setEllipsize(TextUtils.TruncateAt.END);
                seeMoreLess.setVisibility(View.VISIBLE);
                seeMoreLess.setText("See more");
            } else {
                tvNote.setMaxLines(Integer.MAX_VALUE);
                seeMoreLess.setVisibility(View.GONE);
            }

            // Set up the click listener for the see more/less button
            seeMoreLess.setOnClickListener(v -> {
                isExpanded = !isExpanded; // Toggle the expanded state
                if (isExpanded) {
                    tvNote.setMaxLines(Integer.MAX_VALUE);
                    tvNote.setEllipsize(null);
                    seeMoreLess.setText("See less");
                } else {
                    tvNote.setMaxLines(3);
                    tvNote.setEllipsize(TextUtils.TruncateAt.END);
                    seeMoreLess.setText("See more");
                }
            });


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
                onBackPressed();
            });


            ImageButton btnSend = findViewById(R.id.btnSend);
            btnSend.setOnClickListener(v -> {
                submitComment();

                // Hide the keyboard
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });


            etCommentContent = findViewById(R.id.etReply);
            tvcharCount = findViewById(R.id.tvcharCount);

            if (userName != null) {
                etCommentContent.setHint("Commenting on " + userName + "'s Note");
            } else {
                etCommentContent.setHint("Commenting on Username's Note");
            }


            etCommentContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 200) {
                        etCommentContent.setText(s.subSequence(0, 200)); // Trim to 200 characters
                        etCommentContent.setSelection(200); // Move cursor to the end
                        Toast.makeText(CommentActivity.this, "Limit reached! Max 200 characters.", Toast.LENGTH_SHORT).show();
                    }
                    tvcharCount.setText(s.length() + "/200");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });



            //Start of newest oldest
            spinnerSort = findViewById(R.id.spinnerSort);

            // Create an ArrayAdapter for the spinner
            ArrayAdapter<CharSequence> sortadapter = ArrayAdapter.createFromResource(
                    this, R.array.sort_options, android.R.layout.simple_spinner_item);
            sortadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSort.setAdapter(sortadapter);

            // Set the listener for the spinner
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                    String selectedItem = parent.getItemAtPosition(i).toString();
                    if (selectedItem.equals("Newest")) {
                        // Sort comments by newest
                        sortCommentsByNewest();
                    } else if (selectedItem.equals("Oldest")) {
                        // Sort comments by oldest
                        sortCommentsByOldest();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing if no selection is made
                }
            });

            commentList = new ArrayList<>();
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommentAdapter(commentList, this);
            recyclerView.setAdapter(adapter);

        }

        private void sortCommentsByNewest() {
            Collections.sort(commentList, new Comparator<Comment>() {
                @Override
                public int compare(Comment c1, Comment c2) {
                    return c2.getDateCreated().compareTo(c1.getDateCreated()); // Newest first
                }
            });
            adapter.notifyDataSetChanged(); // Refresh the RecyclerView
        }

        private void sortCommentsByOldest() {
            Collections.sort(commentList, new Comparator<Comment>() {
                @Override
                public int compare(Comment c1, Comment c2) {
                    return c1.getDateCreated().compareTo(c2.getDateCreated()); // Oldest first
                }
            });
            adapter.notifyDataSetChanged(); // Refresh the RecyclerView
        }


        // Update submitComment method
        private void submitComment() {
            // Get the content from the input field
            String content = etCommentContent.getText().toString().trim();

            // Validate the input
            if (content.isEmpty()) {
                etCommentContent.setError("Comment cannot be empty");
                return;
            }

            // Retrieve the logged-in user's username
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String username = preferences.getString("username", "Guest");

            // Send the comment to the server
            sendCommentToServer(note_id, username, content, false);

            // Clear the input field after submission
            etCommentContent.setText("");
        }

        private void fetchHeartCount(String noteId, TextView tvHeartCounter) {
            new Thread(() -> {
                try {
                    URL url = new URL("http://10.0.2.2:8000/api/notes/" + noteId + "/hearts/count");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Check if the request was successful
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Parse the response
                        Scanner scanner = new Scanner(conn.getInputStream());
                        StringBuilder response = new StringBuilder();
                        while (scanner.hasNext()) {
                            response.append(scanner.nextLine());
                        }
                        scanner.close();

                        // Assuming the response contains the count as a JSON object, like: {"count": 10}
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        int heartCount = jsonResponse.getInt("heart_count");

                        tvHeartCounter.post(() -> tvHeartCounter.setText(String.valueOf(heartCount)));
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Method to send a comment to the server
        @SuppressLint("StaticFieldLeak")
        private void sendCommentToServer(String noteId, String username, String content, boolean anonymous) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Submitting comment...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    OkHttpClient client = new OkHttpClient();
                    MediaType JSON = MediaType.get("application/json; charset=utf-8");

                    JSONObject json = new JSONObject();
                    try {
                        json.put("note_id", noteId);
                        json.put("user_name", username);
                        json.put("content", content);
                        json.put("anonymous", anonymous ? 1 : 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String token = preferences.getString("auth_token", null);

                    RequestBody body = RequestBody.create(json.toString(), JSON);
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("http://10.0.2.2:8000/api/notes/" + noteId + "/comments")
                            .post(body)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();

                    try (okhttp3.Response response = client.newCall(request).execute()) {
                        return response.body() != null ? response.body().string() : null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    progressDialog.dismiss();
                    if (result != null) {
                        Toast.makeText(CommentActivity.this, "Comment submitted successfully!", Toast.LENGTH_SHORT).show();
                        // Fetch comments again after submission
                        fetchComments(noteId);
                    } else {
                        Toast.makeText(CommentActivity.this, "Failed to submit comment. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }


        // Update fetchComments method to sort comments
        private void fetchComments(String note_id) {
            String url = "http://10.0.2.2:8000/api/notes/" + note_id + "/comments";

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                commentList.clear();

                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject commentObject = response.getJSONObject(i);
                                    String id = commentObject.getString("note_id");
                                    String username = commentObject.getString("user_name");
                                    String content = commentObject.getString("content");
                                    String dateCreated = commentObject.getString("created_at");

                                    Comment comment = new Comment(id, username, content, dateCreated);
                                    commentList.add(comment);
                                }

                                // Sort comments based on the selected sorting option
                                String selectedItem = spinnerSort.getSelectedItem().toString();
                                if (selectedItem.equals("Newest")) {
                                    sortCommentsByNewest();
                                } else if (selectedItem.equals("Oldest")) {
                                    sortCommentsByOldest();
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

            requestQueue.add(jsonArrayRequest);
        }

    }
