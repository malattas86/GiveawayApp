package com.mats.giveawayapp.models

import android.net.Uri

open class ItemDetailsImage {
    open var isSelected: Boolean = false
    open lateinit var imageUri: Uri
}