package com.mats.giveawayapp.ui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.mats.giveawayapp.BuildConfig
import com.mats.giveawayapp.MyApplication
import com.mats.giveawayapp.R
import com.mats.giveawayapp.interfacejava.ItemDetailsImagesListener
import com.mats.giveawayapp.databinding.ActivityAddItemBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.interfacejava.TvShowsListener
import com.mats.giveawayapp.models.AlertDialogItem
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.models.ItemDetailsImage
import com.mats.giveawayapp.ui.adapters.ItemDetailsImagesAdapter
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.Constants.toArrayUri
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException


class AddItemActivity : BaseActivity(), View.OnClickListener, ItemDetailsImagesListener {

    private lateinit var binding: ActivityAddItemBinding
    private var onlineStatus: MyApplication = MyApplication()
    private var mItemId: String = ""
    private var latestTmpUri: Uri? = null

    private var mSelectedImagesFileURI = ArrayList<Uri?>()
    private var mOldImages = ArrayList<String?>()
    private var mItemImagesURL = ArrayList<String?>()

    private val requestPermissionCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            takeImage()
            //selectImageFromGallery()
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val requestPermissionExternalStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                selectImageFromGallery()
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                mSelectedImagesFileURI.add(uri)
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

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {uri ->
        uri?.let {imagesURI ->

            for (imageURI in imagesURI) {
                if (!mSelectedImagesFileURI.contains(imageURI)) {

                    mSelectedImagesFileURI.add(imageURI)
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

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir)
            .apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun displayImages(images: ArrayList<Uri?>) {
        val itemDetailsImages: ArrayList<ItemDetailsImage> = ArrayList()
        for (image in images) {
            val itemDetailsImage = ItemDetailsImage()
            itemDetailsImage.imageUri = image!!

            itemDetailsImages.add(itemDetailsImage)
        }

        if (images.size > 0)
        {
            binding.ivItemImage.visibility = View.GONE
            binding.vpItemImages.visibility = View.VISIBLE

            //val adapterItems = ItemDetailsImagesAdapter(this, images)
            val adapterItems = ItemDetailsImagesAdapter(this, this, itemDetailsImages)
            binding.vpItemImages.adapter = adapterItems
            TabLayoutMediator(binding.tabLayout, binding.vpItemImages) { _,_ ->//tab, position ->

            }.attach()

        } else {
            binding.ivItemImage.visibility = View.VISIBLE
            binding.vpItemImages.visibility = View.GONE
            Picasso.get().load(images[0]!!)
                .into(binding.ivItemImage)
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

        val decimalText: TextInputEditText = binding.etItemPrice
        decimalText.addDecimalLimiter(decimalType = ',')

    }

    override fun onResume() {
        super.onResume()
        onlineStatus.onMoveToForeground()
    }

    override fun onPause() {
        super.onPause()
        onlineStatus.onMoveToBackground()
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
        //mItemImageURL = imagesURL[0]
        mItemImagesURL.addAll(mOldImages)
        mItemImagesURL.addAll(imagesURL)
        //FirestoreClass().updateImageArray(this, mItemId, imagesURL)

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
        val userEmail = this.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_EMAIL, "")!!
        val userImage = this.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_PROFILE_IMAGE, "")!!


        val item = Item(
            user_id = FirestoreClass().getCurrentUserID(),
            user_email = userEmail,
            profile_username = username,
            profile_image = userImage,
            title = binding.etItemTitle.text.toString().trim { it <= ' '},
            description = binding.etItemDescription.text.toString().trim { it <= ' '},
            price = binding.etItemPrice.text.toString().trim { it <= ' '},
            stock_quantity = binding.etItemQuantity.text.toString().trim { it <= ' '},
            images = mItemImagesURL,
            item_id = mItemId
        )

        FirestoreClass().uploadItemDetails(this, item)
    }

    private fun onClickSubmit() {
        if (validateProductDetails()) {
            uploadItemImage()
        }
    }

    private fun onClickAddUpdate()
    {
        val items = arrayOf(
            AlertDialogItem("Tack a Photo", R.drawable.ic_vector_camera),
            AlertDialogItem("Select Image from a Gallery", R.drawable.ic_vector_add_photo_white),
        )
        buildAlertDialog(items)
    }

    private fun buildAlertDialog(items: Array<AlertDialogItem>){
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(this.resources.getColor(R.color.colorSecondary, this.theme))
        gradientDrawable.cornerRadius = 75f

        val adapter: ListAdapter = object : ArrayAdapter<AlertDialogItem?>(
            this,
            android.R.layout.select_dialog_item,
            android.R.id.text1,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //Use super class to create the View
                val v = super.getView(position, convertView, parent)

                val tv = v.findViewById<View>(android.R.id.text1) as TextView
                tv.setTextColor(
                    this@AddItemActivity.resources.getColor(R.color.white,
                    this@AddItemActivity.theme))
                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(
                    items[position].icon, 0, 0, 0)

                //Add margin between image and text (support various screen densities)
                val dp5 = (5 * this@AddItemActivity.resources.displayMetrics.density + 0.5f).toInt()
                tv.compoundDrawablePadding = dp5

                return v
            }
        }


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setAdapter(adapter) { _, position ->
            when (position) {
                0 -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                        takeImage()
                    }
                    else {
                        requestPermissionCamera.launch(android.Manifest.permission.CAMERA)
                    }
                }
                1 -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                        selectImageFromGallery()
                    }
                    else {
                        requestPermissionExternalStorage.
                        launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
        builder.show()
            .window?.setBackgroundDrawable(gradientDrawable)
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
        mOldImages.addAll(item.images)
    }

    private fun getItemDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getItemDetails(this, mItemId)
    }

    private fun TextInputEditText.addDecimalLimiter(maxLimit: Int = 2, decimalType: Char = '.') {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit, decimalType)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

        })
    }

    private fun decimalLimiter(string: String, MAX_DECIMAL: Int, decimalType: Char): String {

        var str = string
        if (str[0] == decimalType) str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count{ decimalType.toString().contains(it) }

        if (decimalCount > 1)
            return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != decimalType && !after) {
                up++
            } else if (t == decimalType) {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }

    override fun onItemDetailsImagesAction(isSelected: Boolean?) {
        if (isSelected!!) {
            //binding.buttonAddToWatchlist.visibility = View.VISIBLE
            Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show()
        } else {
            //binding.buttonAddToWatchlist.visibility = View.GONE
        }
    }
}