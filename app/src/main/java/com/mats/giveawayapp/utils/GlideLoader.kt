package com.mats.giveawayapp.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.mats.giveawayapp.R
import java.io.IOException

@GlideModule
class GlideLoader(val context: Context): AppGlideModule() {

    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image
                .placeholder(R.drawable.user) // A default place holder if image is failed to load
                .into(imageView) // the view in which the image will be loaded
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadItemPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image) // URI of the image
                .fitCenter()
                //.centerCrop() // Scale type of the image
                .into(imageView) // the view in which the image will be loaded
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}