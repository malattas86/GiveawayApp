package com.mats.giveawayapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityUserProfileBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity() {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""

    private lateinit var binding:ActivityUserProfileBinding

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
            GlideLoader(this).loadUserPicture(mUserDetails.image!!, binding.ivUserPhoto)

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

    fun onClickChooseImage(@Suppress("UNUSED_PARAMETER")view: View) {
        // Here we will check if the permission is already allowed or we need to request for it.
        // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(this)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //showErrorSnackBar("The storage permission is granted.", false)
                Constants.showImageChooser(this)
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!
                        Toast.makeText(
                            this,
                            mSelectedImageFileUri.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        //binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this)
                            .loadUserPicture(mSelectedImageFileUri!!, binding.ivUserPhoto)
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
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
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

    fun onClickSave(@Suppress("UNUSED_PARAMETER")view: View) {

        if (validateUserProfileDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            if (mSelectedImageFileUri != null)
            {
                /*FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,
                Constants.USER_PROFILE_IMAGE)*/
            } else {
                updateUserProfileDetails()
            }
            //startActivity(Intent(this, DashboardActivity::class.java))
            onBackPressed()
            finish()
        }
    }
}