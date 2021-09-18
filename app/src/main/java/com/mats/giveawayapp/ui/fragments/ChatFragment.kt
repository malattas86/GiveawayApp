package com.mats.giveawayapp.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.FragmentChatBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.firestore.FirestoreRefClass
import com.mats.giveawayapp.models.User2
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader
import com.squareup.picasso.Picasso

class ChatFragment : BaseFragment() {

    private val mFireReference = FirebaseDatabase.
    getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")

    private lateinit var mMenu: Menu

    //private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
        FirestoreRefClass().setUserProfileImage(this)
    }


    fun setUserDetails(userInfo: User2?) {
        binding.userName.text = userInfo?.username
        Picasso.get().load(userInfo?.profileImage)
            .placeholder(R.drawable.ic_profile)
            .into(binding.profileImage)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textChat
        //textView.text = "it"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}