package com.mats.giveawayapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "ADD YOUR FIREBASE SERVER KEY" // get firebase server key from firebase project setting
    const val CONTENT_TYPE = "application/json"
    // Collections in Cloud Firestore
    const val USERS: String = "users"
    const val ITEMS: String = "items"
    const val ADDRESSES: String = "addresses"
    const val ORDERS: String = "orders"
    const val CART_ITEMS: String = "cart_items"

    const val MY_PREFERENCES: String = "MyPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOGGED_IN_EMAIL: String = "logged_in_email"
    const val LOGGED_IN_PROFILE_IMAGE: String = "logged_in_profile_image"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val IMAGE: String = "image"
    const val FIRSTNAME: String = "firstname"
    const val LASTNAME: String = "lastname"

    const val MALE: String = "male"
    const val FEMALE: String = "female"

    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"

    const val USER_ID:String = "user_id"

    const val ITEM_IMAGE: String = "Item_Image"

    const val EXTRA_ITEM_ID: String = "extra_item_id"

    const val EXTRA_ITEM_OWNER_ID: String = "extra_item_owner_id"
    const val EXTRA_VISIT_USER_ID: String = "extra_visit_user_id"
    const val EXTRA_LOGGED_IN_ID: String = "extra_logged_in_id"
    const val EXTRA_IMAGE_URL: String = "extra_image_url"

    const val DEFAULT_CART_QUANTITY: String = "1"

    const val ITEM_ID: String = "item_id"

    const val CART_QUANTITY: String = "cart_quantity"

    const val USERNAME: String = "username"
    const val USERS_EMAILS: String = "users_emails"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"

    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"

    const val EXTRA_VISIT_USERNAME: String = "visit_username"

    const val PWD_WEAK: String = "WEAK"
    const val PWD_MEDIUM: String = "MEDIUM"
    const val PWD_STRONG: String = "STRONG"
    const val PWD_VERY_STRONG: String = "VERY STRONG"


    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: return the registered extension for the given MIME type.
         *
         * contentResolver.getType: return the MIME type of the given content URI.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun toArrayUri(stringList: ArrayList<String?>): ArrayList<Uri?> {
        val uriList: ArrayList<Uri?> = ArrayList()
        for (string in stringList) {
            uriList.add(Uri.parse(string))
        }
        return uriList
    }
}