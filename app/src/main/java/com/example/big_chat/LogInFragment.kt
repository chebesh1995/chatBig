package com.example.big_chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.big_chat.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth


class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNavController()
        mAuth = FirebaseAuth.getInstance()

        checkAuth()

        binding.btnSignup.setOnClickListener {
            controller.navigate(R.id.signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        val controller = findNavController()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    controller.navigate(R.id.mainChatFragment)
                } else {
                    Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun checkAuth() {
        val controller = findNavController()
        if (mAuth.currentUser != null) {
           controller.navigate(R.id.mainChatFragment)
        }
    }
}