package com.dicoding.dicodingstory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dicoding.dicodingstory.api.ApiClient
import com.dicoding.dicodingstory.api.StoryResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.FileOutputStream

class AddStoryFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var addButton: Button
    private lateinit var instructionTextView: TextView
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.image_view)
        descriptionEditText = view.findViewById(R.id.ed_add_description)
        addButton = view.findViewById(R.id.button_add)
        instructionTextView = view.findViewById(R.id.tv_add_image_instruction)

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        addButton.setOnClickListener {
            uploadStory()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
            instructionTextView.visibility = View.GONE // Hide instruction text after image is selected
        }
    }

    private fun compressImage(uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        val compressedFile = File(requireContext().cacheDir, "compressed_image.jpg")

        FileOutputStream(compressedFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress to 80% quality
        }

        return compressedFile
    }

    private fun uploadStory() {
        val description = descriptionEditText.text.toString()
        if (imageUri == null || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please select an image and enter a description", Toast.LENGTH_SHORT).show()
            return
        }

        // Compress the image before uploading
        val compressedFile = compressImage(imageUri!!)

        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), compressedFile)
            val body = MultipartBody.Part.createFormData("photo", compressedFile.name, requestFile)
            val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)

            ApiClient.apiService.uploadStory("Bearer $token", descriptionBody, body).enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    if (response.isSuccessful && response.body() != null && !response.body()!!.error) {
                        Toast.makeText(requireContext(), "Story added successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.homeFragment) // Navigate back to home
                    } else {
                        Toast.makeText(requireContext(), "Failed to add story", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
} 