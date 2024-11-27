package com.dicoding.dicodingstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

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
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Handle login logic here (e.g., validate credentials)
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return view
    }
} 