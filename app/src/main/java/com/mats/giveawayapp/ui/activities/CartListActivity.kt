package com.mats.giveawayapp.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityCartListBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.CartItem
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.ui.adapters.CartItemsListAdapter

class CartListActivity : BaseActivity() {

    private lateinit var binding: ActivityCartListBinding
    private lateinit var mItemsList: ArrayList<Item>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCartListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (item in mItemsList) {
            for (cart in cartList) {
                if (item.item_id == cart.item_id) {

                    cart.stock_quantity = item.stock_quantity

                    if (item.stock_quantity?.toInt() == 0) {
                        cart.cart_quantity = item.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (cartList.size > 0) {
            binding.rvCartItemList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemList.layoutManager = LinearLayoutManager(this)
            binding.rvCartItemList.setHasFixedSize(true)

            val adapterItems = CartItemsListAdapter(this, cartList)
            binding.rvCartItemList.adapter = adapterItems

            var subTotal = 0.0

            for (item in cartList) {
                val availableQuantity = item.stock_quantity?.toInt()!!

                if (availableQuantity > 0) {
                    val price = item.price?.toDouble()!!
                    val quantity = item.cart_quantity?.toInt()!!

                    subTotal += (price * quantity)
                }
            }
            binding.tvSubTotal.text =
                binding.root.resources.getString(R.string.display_price, subTotal.toString())
            binding.tvShippingCharge.text = "$10.0"

            if (subTotal > 0 ) {
                binding.llCheckout.visibility = View.VISIBLE
                binding.tvTotalAmount.text = binding.root.resources
                    .getString(R.string.display_price, (subTotal + 10.0).toString())
            } else {
                binding.llCheckout.visibility = View.GONE
            }

        } else {
            binding.rvCartItemList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    private fun getCartItemList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }

    override fun onResume() {
        super.onResume()
        getItemList()
    }

    fun successItemsListFromFireStore(itemsList: ArrayList<Item>) {
        hideProgressDialog()
        mItemsList = itemsList

        getCartItemList()
    }

    private fun getItemList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllItemsList(this)
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString((R.string.msg_item_removed_successfully)),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemList()
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemList()
    }
}