package com.mats.giveawayapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ItemListLayoutBinding
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.ui.activities.ItemDetailsActivity
import com.mats.giveawayapp.ui.fragments.ItemFragment
import com.mats.giveawayapp.utils.Constants
import com.mats.giveawayapp.utils.GlideLoader

open class ItemsListAdapter(
    private val fragment: ItemFragment,
    private val context: Context,
    private var list: ArrayList<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemListLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false))
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadItemPicture(model.images[0]!!,
                holder.binding.ivItemImage)
            holder.binding.tvItemName.text = model.title
            holder.binding.tvItemPrice.text =
                holder.binding.root.resources.getString(R.string.display_price, model.price)

            holder.binding.ibDeleteItem.setOnClickListener {
                fragment.deleteItem(model.item_id!!, model.images)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ItemDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_ITEM_ID, model.item_id)
                intent.putExtra(Constants.EXTRA_ITEM_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(val binding: ItemListLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)
}