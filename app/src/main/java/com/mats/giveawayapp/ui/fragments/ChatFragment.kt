package com.mats.giveawayapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.installations.FirebaseInstallations
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.FragmentChatBinding
import com.mats.giveawayapp.firestore.FirestoreRefClass
import com.mats.giveawayapp.models.ChatList
import com.mats.giveawayapp.models.User2
import com.mats.giveawayapp.notifications.Token
import com.mats.giveawayapp.ui.activities.LoginActivity
import com.mats.giveawayapp.ui.adapters.ChatUserAdapter
import com.squareup.picasso.Picasso

class ChatFragment : BaseFragment() {

    private val mFireReference = FirebaseDatabase.
    getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")

    private var userAdapter: ChatUserAdapter? = null
    private var mUsers: List<User2>? = null
    private var usersChatList: List<ChatList>? = null
    private var firebaseUser: FirebaseUser? = null

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
    }


    fun setUserDetails(userInfo: User2?) {
        binding.userName.text = userInfo?.getUsername()
        Picasso.get().load(userInfo?.getProfileImage())
            .placeholder(R.drawable.ic_profile)
            .into(binding.profileImage)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser != null)
        {
            FirestoreRefClass().setUserProfileImage(this)
            getChatList()
        }
        else {
            //startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    private fun getChatList() {
        binding.rvChatUser.setHasFixedSize(true)
        binding.rvChatUser.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref =
            FirebaseDatabase
                .getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("ChatList")
                .child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val chatlist = dataSnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken =
            FirebaseInstallations
                .getInstance()
                .getToken(true)
                .addOnSuccessListener {
                    updateToken(it.token)
                }

        //updateToken(FirebaseInstallations.getInstance().getToken(true).result.token)
        //updateToken(FirebaseInstanceId.getInstance().token)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateToken(token: String?)
    {
        val ref =
            FirebaseDatabase
                .getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }


    private fun retrieveChatList()
    {
        mUsers = ArrayList()

        val ref =
            FirebaseDatabase
                .getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")
                .reference
                .child("users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val user = dataSnapshot.getValue(User2::class.java)

                    for (eachChatList in usersChatList!!)
                    {
                        if (user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }
                userAdapter = ChatUserAdapter(context!!, (mUsers as ArrayList<User2>), true)
                binding.rvChatUser.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}