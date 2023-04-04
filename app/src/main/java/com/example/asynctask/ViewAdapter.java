package com.example.asynctask;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asynctask.databinding.GridViewItemBinding;

import java.util.ArrayList;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ItemViewHolder> {

    private ArrayList<ResultImage> images;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private GridViewItemBinding binding;

        public ItemViewHolder(@NonNull GridViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ResultImage image) {
            Glide.with(binding.getRoot().getContext())
                    .load(image.getPreviewURL())
                    .centerCrop()
                    .into(binding.image);
        }
    }

    public void setData(ArrayList<ResultImage> image) {
        this.images = image;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridViewItemBinding binding = GridViewItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ItemViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
