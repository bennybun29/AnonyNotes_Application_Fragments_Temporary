package com.example.anonynotes;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

public class ProfileAdapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    private List<Note> notes;

    // Constructor
    ProfileAdapter(Context context, List<Note> notes) {
        this.layoutInflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.custom_view, viewGroup, false);
        return new Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder viewHolder, int i) {
        // Get the current note object and bind data to TextViews
        Note note = notes.get(i);
        viewHolder.tvUsername.setText(note.getUsername());
        viewHolder.dateCreated.setText(note.getDateCreated());

        fetchHeartCount(note.getNoteId(), viewHolder.tvHeartCounter);

        boolean isLiked = isNoteLiked(viewHolder.itemView.getContext(), note.getNoteId());
        viewHolder.heartButton.setImageResource(isLiked ? R.drawable.heart_filled : R.drawable.heartbutton);
        note.setLiked(isLiked);

        String content = note.getContent();
        viewHolder.tvNote.setText(content);

        // Check the length of the note content
        if (content.length() > 100) {
            viewHolder.tvNote.setMaxLines(note.isExpanded() ? Integer.MAX_VALUE : 3);
            viewHolder.tvNote.setEllipsize(note.isExpanded() ? null : android.text.TextUtils.TruncateAt.END);
            viewHolder.seeMoreLess.setVisibility(View.VISIBLE);
            viewHolder.seeMoreLess.setText(note.isExpanded() ? "See less" : "See more");
        } else {
            viewHolder.tvNote.setMaxLines(Integer.MAX_VALUE);
            viewHolder.seeMoreLess.setVisibility(View.GONE);
        }

        // Set up the click listener for the see more/less button
        viewHolder.seeMoreLess.setOnClickListener(v -> {
            note.setExpanded(!note.isExpanded()); // Toggle the expanded state of the note
            notifyItemChanged(i); // Notify the adapter to refresh this item
        });


        // Format the created_at timestamp to show the date and time
        String fullDateTime = note.getDateCreated(); // Full timestamp from the note

        // Define the expected date format from the JSON and the target formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone for input format to UTC

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // Use 12-hour format with AM/PM
        outputTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Set timezone for output time format

        try {
            // Parse the full timestamp into a Date object
            Date date = inputFormat.parse(fullDateTime);
            // Format the date to just display "yyyy-MM-dd"
            String formattedDate = outputDateFormat.format(date);
            // Set the formatted date in the dateCreated TextView
            viewHolder.dateCreated.setText(formattedDate);

            // Format the time to "hh:mm a"
            String formattedTime = outputTimeFormat.format(date);
            // Set the formatted time in the tvTime TextView
            viewHolder.tvTime.setText(formattedTime); // Bind the time here

        } catch (ParseException e) {
            e.printStackTrace();
            // In case of an error, fallback to showing the full timestamp
            viewHolder.dateCreated.setText(fullDateTime);
            viewHolder.tvTime.setText("Time error"); // Optional: Handle time parsing error
        }

        // Add click listener for the comment button
        viewHolder.commentButton.setOnClickListener(v -> {
            // Start a new activity or handle comment action here
            Intent intent = new Intent(v.getContext(), CommentActivity.class);
            intent.putExtra("note_id", note.getNoteId()); // Assuming `note` has an ID field
            intent.putExtra("userName", note.getUsername()); // Pass additional data if needed
            intent.putExtra("dateCreated", note.getDateCreated()); // Pass additional data if needed
            intent.putExtra("content", note.getContent()); // Pass additional data if needed
            v.getContext().startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return notes.size();
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

                    // Update the heart counter on the UI thread
                    tvHeartCounter.post(() -> tvHeartCounter.setText(String.valueOf(heartCount)));
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // This method can be called to update the data from the server
    public void setNotes(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    private boolean isNoteLiked(Context context, String noteId) {
        String username = getLoggedInUsername(context);
        if (username == null) return false; // Return false if username is not found

        SharedPreferences userLikes = context.getSharedPreferences("liked_notes_" + username, Context.MODE_PRIVATE);
        return userLikes.getBoolean("note_" + noteId, false);
    }


    private String getLoggedInUsername(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("username", null);
    }



    // ViewHolder class to hold the view references
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, dateCreated, tvNote, seeMoreLess, tvTime, tvHeartCounter; // Add tvTime here
        private ImageButton commentButton, btnBack;
        boolean isExpanded = false; // Track whether the note is expanded

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            dateCreated = itemView.findViewById(R.id.dateCreated);
            tvNote = itemView.findViewById(R.id.tvNote);
            seeMoreLess = itemView.findViewById(R.id.seeMoreLess); // Reference to the "see more/less" TextView
            tvTime = itemView.findViewById(R.id.tvTime); // Initialize tvTime
            commentButton = itemView.findViewById(R.id.commentButton);
            btnBack = itemView.findViewById(R.id.btnBack);
            tvHeartCounter = itemView.findViewById(R.id.tvHeartCounter);

        }
    }
}