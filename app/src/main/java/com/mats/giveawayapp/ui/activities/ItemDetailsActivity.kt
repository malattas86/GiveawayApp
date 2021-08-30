package com.mats.giveawayapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityItemDetailsBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader

class ItemDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityItemDetailsBinding

    private var mItemId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ITEM_ID)) {
            mItemId = intent.getStringExtra(Constants.EXTRA_ITEM_ID)!!
        }
        getItemDetails()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_item_menu, menu)
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
        hideProgressDialog()
        GlideLoader(this).loadItemPicture(
            item.image!!,
            binding.ivItemDetailImage
        )
        binding.tvItemDetailsTitle.text = item.title
        binding.tvItemDetailsPrice.text = "$${item.price}"
        binding.tvItemDetailsDescription.text = item.description
        binding.tvItemDetailsAvailableQuantity.text = item.stock_quantity
    }

    private fun getItemDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getItemDetails(this, mItemId)
    }
}