package com.mats.giveawayapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mats.giveawayapp.databinding.ItemImagesDetailsLayoutBinding
import com.mats.giveawayapp.interfacejava.ItemDetailsImagesListener
import com.mats.giveawayapp.models.ItemDetailsImage
import com.squareup.picasso.Picasso

open class ItemDetailsImagesAdapter(
    var context: Context,
    var itemDetailsImagesListener: ItemDetailsImagesListener,
    private var itemDetailsImages: ArrayList<ItemDetailsImage>
) : RecyclerView.Adapter<ItemDetailsImagesAdapter.ItemDetailsImagesViewHolder>() {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDetailsImagesViewHolder {
        return ItemDetailsImagesViewHolder(
            ItemImagesDetailsLayoutBinding.inflate(
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
    override fun onBindViewHolder(holder: ItemDetailsImagesViewHolder, position: Int) {
        holder.bindView(itemDetailsImages[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return itemDetailsImages.size
    }

    open fun getSelectedImages(): ArrayList<ItemDetailsImage> {
        val selectedImages: ArrayList<ItemDetailsImage> = ArrayList()
        for (itemDetailsImage in itemDetailsImages) {
            if (itemDetailsImage.isSelected) {
                selectedImages.add(itemDetailsImage)
            }
        }
        return selectedImages
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class ItemDetailsImagesViewHolder(val binding: ItemImagesDetailsLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private var selectMode: Boolean = false

        private fun selectModeOnClick(itemDetailsImage: ItemDetailsImage) {
            binding.clImagesDetails.setOnClickListener {
                if (itemDetailsImage.isSelected) {
                    binding.ivItemDetailsImagesCheck.visibility = View.GONE
                    itemDetailsImage.isSelected = false
                    if (getSelectedImages().size == 0) {
                        selectMode = false
                        selectModeOnLongClick(itemDetailsImage)
                        binding.clImagesDetails.setOnClickListener(null)
                        itemDetailsImagesListener.onItemDetailsImagesAction(false)
                    }
                } else {
                    binding.ivItemDetailsImagesCheck.visibility = View.VISIBLE
                    itemDetailsImage.isSelected = true
                    itemDetailsImagesListener.onItemDetailsImagesAction(true)
                }
            }
        }

        private fun selectModeOnLongClick(itemDetailsImage: ItemDetailsImage) {
            binding.clImagesDetails.setOnLongClickListener {
                selectMode = true
                if (itemDetailsImage.isSelected) {
                    binding.ivItemDetailsImagesCheck.visibility = View.GONE
                    itemDetailsImage.isSelected = false
                    itemDetailsImagesListener.onItemDetailsImagesAction(false)
                } else {
                    binding.ivItemDetailsImagesCheck.visibility = View.VISIBLE
                    itemDetailsImage.isSelected = true
                    itemDetailsImagesListener.onItemDetailsImagesAction(true)
                }
                selectModeOnClick(itemDetailsImage)
                binding.clImagesDetails.setOnLongClickListener(null)
                bindView(itemDetailsImage)
                return@setOnLongClickListener true
            }
        }

        fun bindView(itemDetailsImage: ItemDetailsImage) {
            Picasso.get().load(itemDetailsImage.imageUri)
                .fit()
                .into(binding.ivItemDetailsImages)
            if ( itemDetailsImage.isSelected) {
                binding.ivItemDetailsImagesCheck.visibility = View.VISIBLE
            } else {
                binding.ivItemDetailsImagesCheck.visibility = View.GONE
            }
            if (!selectMode) {
                selectModeOnLongClick(itemDetailsImage)
            } else {
                selectModeOnClick(itemDetailsImage)
            }
        }
    }
}