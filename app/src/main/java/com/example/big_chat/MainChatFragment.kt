package com.example.big_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.big_chat.adapter.UserAdapter
import com.example.big_chat.databinding.FragmentMainChatBinding
import com.example.big_chat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainChatFragment : Fragment() {

    private lateinit var binding: FragmentMainChatBinding
    private lateinit var userList: ArrayList<User>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainChatBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        val controller = findNavController()
        userList = ArrayList()
        val adapter = UserAdapter(requireContext(), userList, object : OnUserClickListener {

            override fun onUserClicked(user: User?) {
                val bundle = Bundle()
                bundle.putString("name", user!!.name)
                bundle.putString("uid", user.uid)
                bundle.putString("imgUri", user.imgUri)
                controller.navigate(R.id.fullChatFragment, bundle)
            }
        })


        binding.userRv.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.userRv.adapter = adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = requireActivity().findNavController(R.id.fragmentContainerView) // Assuming fragment container id is fragmentContainerView
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            navController.navigate(R.id.action_mainChatFragment_to_fragment1)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}