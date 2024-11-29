package com.dicoding.dicodingstory

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingstory.api.ApiClient
import com.dicoding.dicodingstory.api.StoriesResponse
import com.dicoding.dicodingstory.databinding.FragmentHomeBinding
import com.dicoding.dicodingstory.model.Story
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyAdapter: StoryAdapter
    private val storyList = mutableListOf<Story>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storyAdapter = StoryAdapter(storyList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = storyAdapter

        fetchStories()
    }

    private fun fetchStories() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            binding.loadingProgressBar.visibility = View.VISIBLE
            ApiClient.apiService.getStories("Bearer $token").enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(call: Call<StoriesResponse>, response: Response<StoriesResponse>) {
                    binding.loadingProgressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        storyList.clear()
                        storyList.addAll(response.body()!!.listStory)
                        storyAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    binding.loadingProgressBar.visibility = View.GONE
                    Log.e("HomeFragment", "Failed to fetch stories", t)
                    Toast.makeText(context, "Failed to fetch stories", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}