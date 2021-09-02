package com.mats.giveawayapp.ui.activities

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityAddItemBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.ui.adapters.ItemDetailsImagesAdapter
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.Constants.toArrayUri
import com.mats.giveawayapp.utils.GlideLoader
import java.io.IOException


class AddItemActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddItemBinding
    private var mItemId: String = ""

    private var mSelectedImageFileURI: Uri? = null
    private var mSelectedImagesFileURI = ArrayList<Uri?>()
    private var mItemImageURL: String = ""
    private var mItemImagesURL = ArrayList<String?>()
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
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {uri ->
        uri?.let {imagesURI ->

            for (imageURI in imagesURI) {
                if (!mSelectedImagesFileURI.contains(imageURI)) {

                    mSelectedImagesFileURI.addAll(imagesURI)
                }
            }

            try {
                binding.ivAddUpdateItem.setImageResource(R.drawable.ic_vector_edit)

                displayImages(mSelectedImagesFileURI)


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

    private fun displayImages(images: ArrayList<Uri?>) {
        if (images.size > 1)
        {
            binding.ivItemImage.visibility = View.GONE
            binding.vpItemImages.visibility = View.VISIBLE

            val adapterItems = ItemDetailsImagesAdapter(this, images)
            binding.vpItemImages.adapter = adapterItems

        } else {
            binding.ivItemImage.visibility = View.VISIBLE
            binding.vpItemImages.visibility = View.GONE
            GlideLoader(this)
                .loadUserPicture(images[0]!!, binding.ivItemImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.ivAddUpdateItem.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        FirebaseApp.initializeApp(this)

        if (intent.hasExtra(Constants.ITEMS)) {
            mItemId = intent.getStringExtra(Constants.ITEMS)!!
            getItemDetails()
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddItemActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddItemActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun imageUploadSuccess(imagesURL: ArrayList<String>) {
        mItemImageURL = imagesURL[0]
        for (imageURL in imagesURL) {
            mItemImagesURL.add(imageURL)
        }
        uploadItemDetails()
    }

    fun itemUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.msg_item_upload_success),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun uploadItemImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImagesFileURI,
        Constants.ITEM_IMAGE)
    }

    private fun uploadItemDetails()
    {
        val username = this.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE)
                            .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val item = Item(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.etItemTitle.text.toString().trim { it <= ' '},
            binding.etItemDescription.text.toString().trim { it <= ' '},
            binding.etItemPrice.text.toString().trim { it <= ' '},
            binding.etItemQuantity.text.toString().trim { it <= ' '},
            mItemImagesURL
            //mItemImageURL,

        )

        FirestoreClass().uploadItemDetails(this, item, mItemImagesURL)
    }

    private fun onClickSubmit() {
        if (validateProductDetails()) {
            uploadItemImage()
        }
    }

    private fun onClickAddUpdate()
    {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            pickImages.launch("image/*")
        }
        else {
            requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }



    override fun onClick(v: View?) {
        if (v != null) {
            when(v) {
                binding.ivAddUpdateItem -> {
                    onClickAddUpdate()
                }

                binding.btnSubmit -> {
                    onClickSubmit()
                }
            }
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImagesFileURI.isEmpty()-> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_item_image),
                    true)
                false
            }

            TextUtils.isEmpty(binding.etItemTitle.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_item_title),
                true)
                false
            }

            TextUtils.isEmpty(binding.etItemPrice.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_item_price),
                    true)
                false
            }

            TextUtils.isEmpty(binding.etItemDescription.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_item_description),
                    true)
                false
            }

            TextUtils.isEmpty(binding.etItemQuantity.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_item_quantity),
                    true)
                false
            }
            else -> true
        }
    }

    fun itemDetailsSuccess(item: Item) {
        hideProgressDialog()

        displayImages(toArrayUri(item.images))
        mSelectedImagesFileURI.addAll(toArrayUri(item.images))

        with(binding) {
            etItemTitle.setText(item.title)
            etItemPrice.setText("${item.price}")
            etItemDescription.setText(item.description)
            etItemQuantity.setText(item.stock_quantity)
        }
    }

    private fun getItemDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getItemDetails(this, mItemId)
    }
}