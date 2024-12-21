package com.dicoding.dicodingstory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingstory.api.ApiClient
import com.dicoding.dicodingstory.data.StoryRepository
import com.dicoding.dicodingstory.databinding.FragmentHomeBinding
import com.dicoding.dicodingstory.viewmodel.StoryViewModel
import com.dicoding.dicodingstory.viewmodel.ViewModelFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeStories()
        
        // Force refresh when fragment is created
        storyAdapter.refresh()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }

        (binding.recyclerView.layoutManager as LinearLayoutManager).apply {
            isItemPrefetchEnabled = true
            initialPrefetchItemCount = 3
        }

        storyAdapter.addLoadStateListener { loadState ->
            binding.loadingProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
            
            val isListEmpty = loadState.refresh is LoadState.NotLoading && storyAdapter.itemCount == 0
            binding.emptyText.isVisible = isListEmpty
            binding.recyclerView.isVisible = !isListEmpty
            
            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(context, "Error: ${it.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        val repository = StoryRepository(ApiClient.apiService)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    private fun observeStories() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            viewModel.getStories(token).observe(viewLifecycleOwner) { pagingData ->
                storyAdapter.submitData(lifecycle, pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        storyAdapter.refresh()
    }
}