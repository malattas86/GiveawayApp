package com.mats.giveawayapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mats.giveawayapp.MyApplication
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityCheckoutBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.Address
import com.mats.giveawayapp.models.CartItem
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.models.Order
import com.mats.giveawayapp.ui.adapters.CartItemsListAdapter
import com.mats.giveawayapp.utils.Constants

class CheckoutActivity : BaseActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var onlineStatus: MyApplication = MyApplication()

    private var mAddressDetails: Address? = null
    private lateinit var mItemsList: ArrayList<Item>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.addressType
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text =
                resources.getString(
                    R.string.display_address,
                    mAddressDetails!!.address,
                    mAddressDetails!!.zipCode
                )
            //binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.visibility = View.VISIBLE
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            else {
                binding.tvCheckoutOtherDetails.visibility = View.GONE
            }
            binding.tvMobileNumber.text = mAddressDetails?.mobileNumber
        }

        getItemsList()

        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }

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

        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllItemsList(this)
    }

    fun successItemsListFromFireStore(itemsList: ArrayList<Item>) {
        mItemsList = itemsList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (item in mItemsList) {
            for (cartItem in cartList) {
                if (item.item_id == cartItem.item_id) {
                    cartItem.stock_quantity = item.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(
            this, this, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter

        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity?.toInt()!!
            if (availableQuantity > 0) {
                val price = item.price?.toDouble()!!
                val quantity = item.cart_quantity?.toInt()!!
                mSubTotal += (price * quantity)
            }
        }

        binding.tvCheckoutSubTotal.text = resources.getString(
            R.string.display_price, mSubTotal.toString())
        binding.tvCheckoutShippingCharge.text = "$10.0"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            binding.tvCheckoutTotalAmount.text = resources.getString(
                R.string.display_price, mTotalAmount.toString())
        }
        else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mAddressDetails != null) {
            val order = Order(
                user_id = FirestoreClass().getCurrentUserID(),
                items = mCartItemsList,
                address = mAddressDetails,
                title = "My Order ${System.currentTimeMillis()}",
                image = mCartItemsList[0].images[0],
                sub_total_amount = mSubTotal.toString(),
                shipping_charge = "10.0",
                total_amount = mTotalAmount.toString()
            )

            FirestoreClass().placeOrder(this, order)
        }
    }

    fun orderPlacedSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            "Your order placed successfully.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}