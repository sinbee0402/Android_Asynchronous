package com.example.coroutine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coroutine.databinding.GridViewItemBinding

class ViewAdapter(
    private var images: MutableList<ResultImage>
) : RecyclerView.Adapter<ViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: ResultImage) {
            Glide.with(binding.root.context)
                .load(image.previewURL)
                .centerCrop()
                .into(binding.image)
        }
    }

    private fun setData(image: MutableList<ResultImage>) {
        this.images = image
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_view_item, parent, false)
        return ItemViewHolder(GridViewItemBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(images[position])
    }
}