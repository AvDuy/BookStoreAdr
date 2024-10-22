package com.example.bookstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterHolder> {
    private List<Chapter> chapters;
    public ChapterAdapter(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chapter, parent, false);
        return new ChapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterHolder holder, int position) {
    holder.imv.setImageResource(chapters.get(position).getAva());
    holder.tv_title.setText(chapters.get(position).getTitle());
    holder.tv_des.setText(chapters.get(position).getDes());
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder {
        ImageView imv;
        TextView tv_title, tv_des;
        public ChapterHolder(@NonNull View itemView) {
            super(itemView);
            imv = itemView.findViewById(R.id.img1);
            tv_title = itemView.findViewById(R.id.text_up);
            tv_des = itemView.findViewById(R.id.text_down);
        }
    }

}
