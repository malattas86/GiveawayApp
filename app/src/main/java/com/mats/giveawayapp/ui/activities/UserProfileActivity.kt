package com.mats.giveawayapp.ui.activities

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityUserProfileBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader
import com.squareup.picasso.Picasso
import java.io.IOException

class UserProfileActivity : BaseActivity() , View.OnClickListener{

    private lateinit var mUserDetails: User
    private var mSelectedImagesFileURI = ArrayList<Uri?>()
    private var mUserProfileImageURL: String = ""
    private lateinit var binding:ActivityUserProfileBinding

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            pickImages.launch("image/*")
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {imagesURI ->
            mSelectedImagesFileURI.add(imagesURI)

            try {
                binding.ivUserPhoto.setImageResource(R.drawable.ic_vector_edit)
                Picasso.get().load(mSelectedImagesFileURI[0])
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.ivUserPhoto)

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    resources.getString(R.string.image_selection_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        if (mUserDetails.profileCompleted == 0) {
            binding.tvTitle.text = resources.getString(R.string.title_profile)

        } else {
            setActionBar()
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
            Picasso.get().load(mUserDetails.image!!)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivUserPhoto)

            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.setText(mUserDetails.lastName)

            if (mUserDetails.mobile != 0L) {
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }
        }

        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)
        binding.etUsername.isEnabled = false
        binding.etUsername.setText(mUserDetails.userName)

        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarUserProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun onClickChooseImage() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            pickImages.launch("image/*")
        }
        else {
            requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            } else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        //startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        onBackPressed()
        finish()
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = binding.etFirstName.text.toString().trim { it <= ' '}

        val lastName = binding.etLastName.text.toString().trim { it <= ' '}

        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' '}

        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else
        {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty() && mUserProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (firstName.isNotEmpty() && firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRSTNAME] = firstName
        }

        if (lastName.isNotEmpty() && lastName != mUserDetails.lastName) {
            userHashMap[Constants.LASTNAME] = lastName
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun onClickSave() {

        if (validateUserProfileDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            if (mSelectedImagesFileURI[0] != null)
            {
                FirestoreClass().uploadImageToCloudStorage(this, mSelectedImagesFileURI,
                Constants.USER_PROFILE_IMAGE)
            } else {
                updateUserProfileDetails()
            }
            //startActivity(Intent(this, DashboardActivity::class.java))
            onBackPressed()
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v) {
                binding.btnSubmit -> {
                    onClickSave()
                }
                binding.ivUserPhoto -> {
                    onClickChooseImage()
                }
            }
        }
    }
}