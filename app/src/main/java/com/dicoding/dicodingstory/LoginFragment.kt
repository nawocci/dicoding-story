package com.dicoding.dicodingstory

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dicoding.dicodingstory.api.ApiClient
import com.dicoding.dicodingstory.api.LoginRequest
import com.dicoding.dicodingstory.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        registerButton = view.findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                handleError("Please fill in all fields")
            } else {
                loginUser(email, password)
            }
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return view
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)
        val loadingProgressBar: ProgressBar = requireView().findViewById(R.id.loadingProgressBar)
        
        loadingProgressBar.visibility = View.VISIBLE // Show loading

        ApiClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                loadingProgressBar.visibility = View.GONE // Hide loading
                if (response.isSuccessful && response.body() != null) {
                    val loginResult = response.body()!!.loginResult
                    saveUserData(loginResult.userId, loginResult.token)
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    handleError("Login failed. Please check your credentials.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loadingProgressBar.visibility = View.GONE // Hide loading
                handleError("Network error: ${t.message}")
            }
        })
    }

    private fun saveUserData(userId: String, token: String) {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.putString("token", token)
        editor.apply()
    }

    private fun handleError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
} 