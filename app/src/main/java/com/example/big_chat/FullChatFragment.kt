package com.example.big_chat

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.big_chat.adapter.MessageAdapter
import com.example.big_chat.databinding.FragmentFullChatBinding
import com.example.big_chat.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FullChatFragment : Fragment() {

    private lateinit var binding: FragmentFullChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val name = arguments?.getString("name")
        val receiverUid = arguments?.getString("uid")
        val imgUriString = arguments?.getString("imgUri")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().reference
        storageRef = FirebaseStorage.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        if (imgUriString != null) {
            val imgUri = Uri.parse(imgUriString)
            Glide.with(this)
                .load(imgUri)
                .into(binding.userImage)
        }

        binding.toolbar.title = name

        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        binding.chatRv.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRv.adapter = messageAdapter

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        binding.sendBt.setOnClickListener {
            val message = binding.messageBox.text.toString()
            val messageObject = Message(message, senderUid)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            binding.messageBox.setText("")
        }
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainChatFragment::class.java)
        startActivity(intent)
        return true
    }*/
}