package com.dicoding.dicodingstory

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.dicoding.dicodingstory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            // Navigate to homeFragment directly if token exists
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.homeFragment)
        } else {
            // Navigate to loginFragment if no token
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.loginFragment)
        }

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            // Load the animation
            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_scale)
            // Start the animation
            view.startAnimation(scaleAnimation)
            // Navigate to addStoryFragment
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.addStoryFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                true
            }
            R.id.action_maps -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.mapsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        // Clear user data from shared preferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all data
        editor.apply()

        // Navigate to loginFragment and clear the back stack
        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragment_to_loginFragment)
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        if (navController.currentDestination?.id == R.id.homeFragment) {
            // Allow back navigation to exit the app or go to the app drawer
            finish() // Close the app
        } else {
            // Default behavior for other fragments
            super.onBackPressed()
        }
    }
}