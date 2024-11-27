package com.dicoding.dicodingstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.dicodingstory.model.Story

class StoryAdapter(private val stories: List<Story>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.nameTextView.text = story.name
        Glide.with(holder.itemView.context).load(story.photoUrl).into(holder.photoImageView)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("storyId", story.id)
            }
            it.findNavController().navigate(R.id.detailFragment, bundle)
        }
    }

    override fun getItemCount(): Int = stories.size
}