package com.dicoding.dicodingstory

import android.content.Context
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
import com.dicoding.dicodingstory.api.RegisterRequest
import com.dicoding.dicodingstory.api.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        registerButton = view.findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, email, password)
            }
        }

        return view
    }

    private fun registerUser(name: String, email: String, password: String) {
        val request = RegisterRequest(name, email, password)
        val loadingProgressBar: ProgressBar = requireView().findViewById(R.id.loadingProgressBar)
        
        loadingProgressBar.visibility = View.VISIBLE // Show loading

        ApiClient.apiService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                loadingProgressBar.visibility = View.GONE // Hide loading
                if (response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.error) {
                        Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        handleError(response.body()!!.message)
                    }
                } else {
                    handleError("Registration failed. Please try again.")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                loadingProgressBar.visibility = View.GONE // Hide loading
                handleError("Network error: ${t.message}")
            }
        })
    }

    private fun handleError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
} 