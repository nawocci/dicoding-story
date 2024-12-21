package com.dicoding.dicodingstory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.dicodingstory.api.ApiClient
import com.dicoding.dicodingstory.api.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fetchStoriesWithLocation()
    }

    private fun fetchStoriesWithLocation() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            ApiClient.apiService.getStoriesWithLocation("Bearer $token", 1).enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(call: Call<StoriesResponse>, response: Response<StoriesResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val stories = response.body()!!.listStory
                        stories.forEach { story ->
                            if (story.lat != 0.0 && story.lon != 0.0) {
                                val latLng = LatLng(story.lat, story.lon)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet(story.description)
                                )
                            }
                        }
                        
                        // Center the map on Indonesia
                        val indonesia = LatLng(-2.548926, 118.014863)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to fetch stories: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
} 