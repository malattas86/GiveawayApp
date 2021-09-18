package com.mats.giveawayapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivitySettingsBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private lateinit var binding: ActivitySettingsBinding
    private var mUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        initUI()
    }

    private fun initUI() {
        if (intent.hasExtra(Constants.EXTRA_ITEM_OWNER_ID)) {
            mUserID =
                intent.getStringExtra(Constants.EXTRA_ITEM_OWNER_ID)!!
        }
        if (intent.hasExtra(Constants.EXTRA_LOGGED_IN_ID)) {
            mUserID =
                intent.getStringExtra(Constants.EXTRA_LOGGED_IN_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == mUserID) {
            binding.llAddress.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.VISIBLE
            binding.tvEdit.visibility = View.VISIBLE
            binding.tvTitle.text = resources.getString(R.string.title_settings)
        } else {
            binding.llAddress.visibility = View.GONE
            binding.btnLogout.visibility = View.GONE
            binding.tvEdit.visibility = View.GONE
            binding.tvTitle.text = resources.getString(R.string.user_profile)
        }

        binding.btnLogout.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)
        binding.llAddress.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSettingsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this, mUserID)
    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user
        hideProgressDialog()

        GlideLoader(this).loadUserPicture(mUserDetails.image!!, binding.ivUserPhoto)
        "${mUserDetails.firstName} ${mUserDetails.lastName}".also {
            binding.tvName.text = it
        }
        mUserDetails.gender.also { binding.tvGender.text = it }
        mUserDetails.email.also { binding.tvEmail.text = it }
        "${mUserDetails.mobile}".also { binding.tvMobileNumber.text = it }

    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser != null)
            getUserDetails()

        else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun onClickLogout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v) {
                binding.tvEdit -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                binding.btnLogout -> {
                    onClickLogout()
                }
                binding.llAddress -> {
                    val intent = Intent(this, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}