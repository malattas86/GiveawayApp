package com.mats.giveawayapp.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityAddEditAddressBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.utils.Constants

class AddEditAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityAddEditAddressBinding

    private var mAddressDetails: com.mats.giveawayapp.models.Address? = null

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id?.isNotEmpty()!!) {
                binding.tvTitle.text = resources.getString(R.string.title_edit_address)
                binding.btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)

                binding.etFullName.setText(mAddressDetails?.name)
                binding.etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                binding.etAddress.setText(mAddressDetails?.address)
                binding.etZipCode.setText(mAddressDetails?.zipCode)
                binding.etAdditionalNote.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.addressType) {
                    Constants.HOME -> {
                        binding.rbHome.isChecked = true
                    }
                    Constants.OFFICE -> {
                        binding.rbOffice.isChecked = true
                    }
                    else -> {
                        binding.rbOther.isChecked = true
                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnSubmitAddress.setOnClickListener{
            saveAddressToFirestore()
            /*
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS) {
                getLastKnownLocation()
            } else {
                Log.e(this.javaClass.simpleName, "Google Service not supported")
                Toast.makeText(
                    this,
                    "Google Service not supported",
                    Toast.LENGTH_LONG
                ).show()
            }*/
        }

        binding.rgType.setOnCheckedChangeListener{_, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            }
            else {
                binding.tilOtherDetails.visibility = View.GONE
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddEditAddressActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * call this method for receive location
     * get location and give callback when successfully retrieve
     * function itself check location permission before access related methods
     *
     */
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val latitude =  location?.latitude
                val longitude = location?.longitude
                val addresses: List<Address>
                val geo = Geocoder(this)
                addresses = geo.getFromLocation(latitude!!, longitude!!, 1)
                // adminArea Staat, baden-wüttenberg
                //countryCode --> DE
                // postalCode --> PLZ
                // locality --> Stadt
                // getAddressLine(0) address (xxStr 1, xxxxStadt)
                binding.etZipCode.setText(addresses[0].getAddressLine(0))
                Toast.makeText(this, addresses[0].countryName, Toast.LENGTH_SHORT).show()

            }
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFullName.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_address),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_zip_code),
                    true
                )
                false
            }

            binding.rbOther.isChecked &&
                    TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_zip_code),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore() {
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' '}
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' '}
        val address: String = binding.etAddress.text.toString().trim { it <= ' '}
        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' '}
        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' '}
        val otherDetails: String = binding.etOtherDetails.text.toString().trim { it <= ' '}

        if (validateData()) {

            // show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME
                }
                binding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = com.mats.giveawayapp.models.Address(
                user_id = FirestoreClass().getCurrentUserID(),
                name = fullName,
                mobileNumber = phoneNumber,
                address = address,
                zipCode = zipCode,
                additionalNote = additionalNote,
                addressType = addressType,
                otherDetails = otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id?.isNotEmpty()!!) {
                FirestoreClass().updateAddress(this, addressModel, mAddressDetails?.id!!)
            }
            else {
                FirestoreClass().addAddress(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id!!.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_update_successfully)
        } else {
            resources.getString(R.string.msg_your_address_added_successfully)
        }

        Toast.makeText(
            this,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }
}