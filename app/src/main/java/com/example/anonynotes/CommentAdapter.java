package com.example.anonynotes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter <CommentAdapter.ViewHolder> {
    private ArrayList<Comment> commentList;
    private Context context;

    public CommentAdapter(ArrayList<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int i) {
        Comment comment = commentList.get(i);
        holder.tvReplyUsername.setText(comment.getUsername());
        holder.tvreplydateCreated.setText(comment.getDateCreated());

        String content = comment.getContent();
        holder.tvReply.setText(content);

        // Check the length of the comment content
        if (content.length() > 100) {
            holder.tvReply.setMaxLines(comment.isExpanded() ? Integer.MAX_VALUE : 3);
            holder.tvReply.setEllipsize(comment.isExpanded() ? null : TextUtils.TruncateAt.END);
            holder.tvseeMoreLess.setVisibility(View.VISIBLE);
            holder.tvseeMoreLess.setText(comment.isExpanded() ? "See less" : "See more");
        } else {
            holder.tvReply.setMaxLines(Integer.MAX_VALUE);
            holder.tvseeMoreLess.setVisibility(View.GONE);
        }

        String fullDateTime = comment.getDateCreated(); // Full timestamp from the comment

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
            // Set the formatted date in the tvreplydateCreated TextView
            holder.tvreplydateCreated.setText(formattedDate);

            // Format the time to "hh:mm a"
            String formattedTime = outputTimeFormat.format(date);
            // You can display the time somewhere or concatenate it with the date, for example:
            holder.tvreplydateCreated.setText(formattedDate + " " + formattedTime); // Showing date and time together

        } catch (ParseException e) {
            e.printStackTrace();
            // In case of an error, fallback to showing the full timestamp
            holder.tvreplydateCreated.setText(fullDateTime);
        }

        // Set up the click listener for the see more/less button
        holder.tvseeMoreLess.setOnClickListener(v -> {
            comment.setExpanded(!comment.isExpanded()); // Toggle the expanded state of the comment
            notifyItemChanged(i); // Notify the adapter to refresh this item
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReplyUsername, tvreplydateCreated, tvReply, tvseeMoreLess;
        public ImageButton heartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvreplydateCreated = itemView.findViewById(R.id.replydateCreated);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvseeMoreLess = itemView.findViewById(R.id.tvseeMoreLess);
        }
    }
}