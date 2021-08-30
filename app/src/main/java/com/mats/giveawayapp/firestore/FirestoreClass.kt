package com.mats.giveawayapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.ui.activities.*
import com.mats.giveawayapp.ui.fragments.BaseFragment
import com.mats.giveawayapp.ui.fragments.DashboardFragment
import com.mats.giveawayapp.ui.fragments.ItemFragment
import com.mats.giveawayapp.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The "user" is collection name. If the collection is already created then it will not
        // create the same one again.
        userInfo.id?.let {
            mFirestore.collection(Constants.USER)
                // Document ID for users fields. Here the document it is new ID.
                .document(it)
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we went
                // to merge later on instead of replacing the fields.
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while registering the user.",
                        e
                    )
                }
        }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign currentID if it is not null or else it will be blank.
        var currentID = ""
        if (currentUser != null) {
            currentID = currentUser.uid
        }

        return currentID
    }

    fun getItemsList(fragment: ItemFragment) {
        mFirestore.collection(Constants.ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Items List", document.documents.toString())
                val itemsList: ArrayList<Item> = ArrayList()
                for (i in document.documents) {
                    val item = i.toObject(Item::class.java)
                    item!!.item_id = i.id

                    itemsList.add(item)
                }

                when(fragment) {
                    is ItemFragment -> {
                        fragment.successItemsListFromFireStore(itemsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the item list
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting item list, e")
            }
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFirestore.collection(Constants.USER)
            // The document id to get the Fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MY_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // Key:Value logged_in_username: firstname lastname
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.email}"
                    //"${user.firsName} ${user.lastName}"
                )
                editor.apply()

                // START
                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
                // END
            }
            .addOnFailureListener {
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details."
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.USER).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity ->
                        // user profile success
                        activity.userProfileUpdateSuccess()
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. and print the error in log
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while update the user details.",
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                                        activity,
                                        imageFileURI)
        )
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddItemActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener{ exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddItemActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadItemDetails(activity: AddItemActivity, itemInfo: Item) {
        mFirestore.collection(Constants.ITEMS)
            .document()
            .set(itemInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it
                activity.itemUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the item details.",
                    e
                )
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFirestore.collection(Constants.ITEMS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val itemsList: ArrayList<Item> = ArrayList()
                for (i in document.documents) {
                    val item = i.toObject(Item::class.java)!!
                    item.item_id = i.id
                    itemsList.add(item)
                }
                fragment.successItemsListFromFireStore(itemsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard item list
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard item list, e")
            }
    }

    fun deleteItem(fragment: ItemFragment, itemId: String) {
        mFirestore.collection(Constants.ITEMS)
            .document(itemId)
            .delete()
            .addOnSuccessListener {
                fragment.itemDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the item.",
                    e
                )
            }
    }

    fun getItemDetails(activity: Activity, itemId: String) {
        mFirestore.collection(Constants.ITEMS)
            .document(itemId)
            .get()
            .addOnSuccessListener { document ->
                val item = document.toObject(Item::class.java)
                when(activity) {
                    is ItemDetailsActivity -> {
                        activity.itemDetailsSuccess(item!!)
                    }
                    is AddItemActivity -> {
                        activity.itemDetailsSuccess(item!!)
                    }
                }

            }
            .addOnFailureListener { e->
                // Hide the progress dialog if there is an error
                when(activity) {
                    is ItemDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddItemActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting the product details.",
                e)
            }
    }
}