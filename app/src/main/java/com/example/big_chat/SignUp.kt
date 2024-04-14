package com.example.big_chat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.big_chat.databinding.ActivitySignUpBinding
import com.example.big_chat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        supportActionBar?.title = "Назад"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val backArrowDrawable = resources.getDrawable(android.R.drawable.ic_menu_revert)
        supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)

        mAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            uri?.let {
                storageRef.child("user").putFile(it)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                val imgUrl = url.toString()

                                signUp(name, email, password, imgUrl)
                            }
                    }
            }
        }
    }

    private fun signUp(name: String, email: String, password: String, image: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    addUserToDatabase(name, email, uid, password, image)
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp, "Какая-то ошибка", Toast.LENGTH_SHORT).show()
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

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
        return true
    }
}