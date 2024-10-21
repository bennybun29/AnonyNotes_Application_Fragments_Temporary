package com.example.anonynotes;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.List;
import java.util.TimeZone;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Note> notes;

    // Constructor
    Adapter(Context context, List<Note> notes) {
        this.layoutInflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.custom_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // Get the current note object and bind data to TextViews
        Note note = notes.get(i);
        viewHolder.tvUsername.setText(note.getUsername());
        viewHolder.dateCreated.setText(note.getDateCreated());

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
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    // This method can be called to update the data from the server
    public void setNotes(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    // ViewHolder class to hold the view references
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, dateCreated, tvNote, seeMoreLess, tvTime; // Add tvTime here
        public ImageButton commentButton;
        boolean isExpanded = false; // Track whether the note is expanded

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            dateCreated = itemView.findViewById(R.id.dateCreated);
            tvNote = itemView.findViewById(R.id.tvNote);
            seeMoreLess = itemView.findViewById(R.id.seeMoreLess); // Reference to the "see more/less" TextView
            tvTime = itemView.findViewById(R.id.tvTime); // Initialize tvTime
            commentButton = itemView.findViewById(R.id.commentButton);
        }
    }
}