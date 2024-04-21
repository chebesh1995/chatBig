package com.example.big_chat

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.big_chat.databinding.FragmentSignUpBinding
import com.example.big_chat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNavController()
        storageRef = FirebaseStorage.getInstance().getReference("Images")
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.appLogo.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        binding.pictureBt.setOnClickListener {
            pickImage.launch("image/*")
        }

        mAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (uri != null) {
                storageRef.child("user").putFile(uri!!)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                val imgUrl = url.toString()
                                signUp(name, email, password, imgUrl)
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Handle upload error
                        Toast.makeText(requireContext(), "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // No image selected, proceed with empty image URL
                signUp(name, email, password, "")
            }
        }
    }

    private fun signUp(name: String, email: String, password: String, image: String) {
        val controller = findNavController()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    addUserToDatabase(name, email, uid, password, image)
                   controller.navigate(R.id.mainChatFragment)
                } else {
                    Toast.makeText(requireContext(), "Какая-то ошибка", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        password: String,
        image: String
    ) {
        firebaseRef = FirebaseDatabase.getInstance().reference
        firebaseRef.child("user").child(uid).setValue(User(name, email, uid, password, image))
    }

    /* override fun onSupportNavigateUp(): Boolean {
         val intent = Intent(this, LogInFragment::class.java)
         startActivity(intent)
         return true
     }*/
}