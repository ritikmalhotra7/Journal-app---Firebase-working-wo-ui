package com.example.journalapplication;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journalapplication.databinding.ListCardviewBinding;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder>{
    public ListCardviewBinding binding;
    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListCardviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder holder, int position) {
        Journal j = journalList.get(position);
        String url;
        holder.title.setText(j.getTitle());
        holder.thoughts.setText(j.getThoughts());
        url = j.getImageUrl();
        Picasso.get().load(url).into(holder.bg);
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(j.getTime().getSeconds()*1000);
        holder.date.setText(timeAgo);
        
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,title,thoughts;
        ImageView bg;

        String userName;
        String userId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = binding.dateAdded;
            title = binding.titlecard;
            thoughts = binding.thought;
            bg = binding.bg;

        }

    }
}
