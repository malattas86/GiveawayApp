package com.mats.giveawayapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityItemDetailsBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.CartItem
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.ui.adapters.ItemDetailsImagesAdapter
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.Constants.toArrayUri
import com.squareup.picasso.Picasso

import com.mats.giveawayapp.models.AlertDialogItem
import android.view.*
import android.widget.*
import com.mats.giveawayapp.MyApplication


class ItemDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityItemDetailsBinding
    private var onlineStatus: MyApplication = MyApplication()

    private var mItemId: String = ""
    private lateinit var mItemDetails: Item
    private lateinit var mItemOwnerId: String

    private var mImagesList: ArrayList<String?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        initUi()
    }

    override fun onResume() {
        super.onResume()
        onlineStatus.onMoveToForeground()
        getItemDetails()
    }

    override fun onPause() {
        super.onPause()
        onlineStatus.onMoveToBackground()
    }

    private fun initUi() {
        if (intent.hasExtra(Constants.EXTRA_ITEM_ID)) {
            mItemId = intent.getStringExtra(Constants.EXTRA_ITEM_ID)!!
        }

        if (intent.hasExtra(Constants.EXTRA_VISIT_USER_ID)) {
            mItemOwnerId =
                intent.getStringExtra(Constants.EXTRA_VISIT_USER_ID)!!
        }
        if (FirestoreClass().getCurrentUserID() == mItemOwnerId) {
            binding.btnAddToCart.visibility = View.GONE
            binding.llUserDetail.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.llUserDetail.visibility = View.VISIBLE
        }

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
        binding.llUserDetail.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_item_menu, menu)
        val menuItem: MenuItem = menu?.findItem(R.id.action_edit_item)!!
        menuItem.isVisible = FirestoreClass().getCurrentUserID() == mItemOwnerId
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.action_edit_item -> {
                val intent = Intent(this, AddItemActivity::class.java)
                intent.putExtra(Constants.ITEMS, mItemId)
                startActivity(intent)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarItemDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarItemDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun itemDetailsSuccess(item: Item) {
        mItemDetails = item
        for (image in item.images) {
            if (!mImagesList.contains(image)) {
                mImagesList.add(image)
            }
        }
        //mImagesList.addAll(item.images)
        //val itemDetailsImagesAdapter =
            //ItemDetailsImagesAdapter(this, toArrayUri(mImagesList))
        //binding.vpItemDetailImage.adapter = itemDetailsImagesAdapter
        TabLayoutMediator(binding.tabLayout, binding.vpItemDetailImage) { _, _ ->//tab, position ->
            //tab.text = mImagesList[position]
        }.attach()

        with(binding) {
            tvItemDetailsTitle.text = item.title
            tvItemDetailsPrice.text =
                binding.tvItemDetailsPrice.resources.getString(R.string.display_price, item.price)
            tvItemDetailsDescription.text = item.description
            tvItemDetailsAvailableQuantity.text = item.stock_quantity
            tvUsername.text = item.profile_username
            Picasso.get().load(item.profile_image).into(binding.ivImageProfile)
            if (item.images.size > 1){
                binding.tabLayout.visibility = View.VISIBLE
            }
            else {
                binding.tabLayout.visibility = View.GONE
            }
        }

        if (item.stock_quantity?.toInt() == 0) {
            hideProgressDialog()

            binding.btnAddToCart.visibility = View.GONE
            binding.tvItemDetailsAvailableQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)
            binding.tvItemDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackBarError
                )
            )
        }
        else {
            if (FirestoreClass().getCurrentUserID() == item.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this, mItemId)
            }
        }
    }

    private fun getItemDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getItemDetails(this, mItemId)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v) {
                binding.btnAddToCart -> {
                    onClickAddToCart()
                }

                binding.btnGoToCart -> {
                    onClickGoToCart()
                }

                binding.llUserDetail -> {
                    onClickUserDetails()
                }
            }
        }
    }

    private fun onClickUserDetails() {

        val items = arrayOf(
            AlertDialogItem("Send a Message", R.drawable.ic_vector_chat),
            AlertDialogItem("Visit the Profile", R.drawable.ic_vector_person_outline),
        )
        com.mats.giveawayapp.utils.AlertDialog(
            this,
            items,
            mItemDetails.user_id,
            mItemDetails.profile_username).build()
    }


    private fun onClickAddToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mItemId,
            mItemDetails.title,
            mItemDetails.price,
            mItemDetails.images,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)
    }

    private fun onClickGoToCart() {
        startActivity(Intent(this, CartListActivity::class.java))
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.msg_success_item_add_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun itemExistsInCart() {
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}