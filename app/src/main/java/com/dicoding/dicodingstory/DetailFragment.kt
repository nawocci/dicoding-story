package com.dicoding.dicodingstory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dicoding.dicodingstory.api.ApiClient
import com.dicoding.dicodingstory.api.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var photoImageView: ImageView
    private lateinit var descriptionTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.tv_detail_name)
        photoImageView = view.findViewById(R.id.iv_detail_photo)
        descriptionTextView = view.findViewById(R.id.tv_detail_description)

        val storyId = arguments?.getString("storyId") ?: return
        fetchStoryDetails(storyId)
    }

    private fun fetchStoryDetails(storyId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            ApiClient.apiService.getStoryDetails("Bearer $token", storyId).enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    if (response.isSuccessful && response.body() != null && !response.body()!!.error) {
                        val story = response.body()!!.story
                        nameTextView.text = story.name
                        descriptionTextView.text = story.description
                        Glide.with(this@DetailFragment).load(story.photoUrl).into(photoImageView)
                    } else {
                        Toast.makeText(context, "Failed to fetch story details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}