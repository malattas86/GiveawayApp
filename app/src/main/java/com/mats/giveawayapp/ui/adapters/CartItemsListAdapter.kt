package com.mats.giveawayapp.ui.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ItemCartLayoutBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.CartItem
import com.mats.giveawayapp.ui.activities.CartListActivity
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader

open class CartItemsListAdapter(
    private val activity: Activity,
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItem: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Called when RecyclerView needs a new [RecyclerView.ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemCartLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [RecyclerView.ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [RecyclerView.ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context)
                .loadItemPicture(model.images[0]!!,
                    holder.binding.ivCartItemImage)
            holder.binding.tvCartItemTitle.text = model.title
            holder.binding.tvCartItemPrice.text =
                holder.binding.root.resources.getString(R.string.display_price, model.price)
            holder.binding.tvCartQuantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.binding.ibRemoveCartItem.visibility = View.GONE
                holder.binding.ibAddCartItem.visibility = View.GONE

                if (updateCartItem) {
                    holder.binding.ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    holder.binding.ibDeleteCartItem.visibility = View.GONE
                }

                holder.binding.tvCartQuantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            }
            else {
                if (updateCartItem) {
                    holder.binding.ibRemoveCartItem.visibility = View.VISIBLE
                    holder.binding.ibAddCartItem.visibility = View.VISIBLE
                    holder.binding.ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    holder.binding.ibRemoveCartItem.visibility = View.GONE
                    holder.binding.ibAddCartItem.visibility = View.GONE
                    holder.binding.ibDeleteCartItem.visibility = View.GONE
                }

                holder.binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondaryText
                    )
                )
            }

            holder.binding.ibDeleteCartItem.setOnClickListener {
                when(context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }
                FirestoreClass().removeItemFromCart(context, model.id!!)
            }

            holder.binding.ibRemoveCartItem.setOnClickListener {
                if (model.cart_quantity == "1") {
                    showAlertDialogToDeleteItem(model.id!!)
                } else {
                    val cartQuantity: Int = model.cart_quantity?.toInt()!!

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // show progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id!!, itemHashMap)
                }
            }

            holder.binding.ibAddCartItem.setOnClickListener {
                val cartQuantity: Int = model.cart_quantity?.toInt()!!

                if (cartQuantity < model.stock_quantity?.toInt()!!) {
                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // show progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id!!, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ),
                            true
                        )
                    }
                }
            }

        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(val binding: ItemCartLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    private fun showAlertDialogToDeleteItem(itemId: String) {
        val builder = AlertDialog.Builder(context)
        // set title for alert dialog
        builder.setTitle(context.resources.getString(R.string.delete_dialog_title))
        // set message for alert dialog
        builder.setMessage(context.resources.getString(R.string.msg_delete_itemCart_dialog))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // performing positive action
        builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialogInterface, _ ->
            when(activity) {
                is CartListActivity -> {
                    activity.showProgressDialog(context.resources.getString(R.string.please_wait))
                }
            }
            FirestoreClass().removeItemFromCart(context, itemId)
            dialogInterface.dismiss()
        }

        // performing negative action
        builder.setNegativeButton(context.resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}