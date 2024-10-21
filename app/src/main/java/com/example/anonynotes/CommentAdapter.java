package com.example.anonynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter <CommentAdapter.ViewHolder> {
    private ArrayList<Comment> commentList;
    private Context context;

    public CommentAdapter(ArrayList<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvReplyUsername.setText(comment.getUsername());
        holder.tvreplydateCreated.setText(comment.getDateCreated());
        holder.tvReply.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReplyUsername, tvreplydateCreated, tvReply;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvreplydateCreated = itemView.findViewById(R.id.replydateCreated);
            tvReply = itemView.findViewById(R.id.tvReply);
        }
    }
}
