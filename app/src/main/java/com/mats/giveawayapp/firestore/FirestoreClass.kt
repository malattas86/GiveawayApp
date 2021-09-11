package com.mats.giveawayapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mats.giveawayapp.R
import com.mats.giveawayapp.models.*
import com.mats.giveawayapp.ui.activities.*
import com.mats.giveawayapp.ui.fragments.DashboardFragment
import com.mats.giveawayapp.ui.fragments.ItemFragment
import com.mats.giveawayapp.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()
    //private val mFireReference = FirebaseDatabase.getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The "user" is collection name. If the collection is already created then it will not
        // create the same one again.
        userInfo.id?.let {
            mFirestore.collection(Constants.USERS)
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
                val itemsList: ArrayList<Item> = ArrayList()
                for (i in document.documents) {
                    val item = i.toObject(Item::class.java)
                    item!!.item_id = i.id

                    itemsList.add(item)
                }

                fragment.successItemsListFromFireStore(itemsList)

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the item list
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting item list", e)
            }
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFirestore.collection(Constants.USERS)
            // The document id to get the Fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

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
        mFirestore.collection(Constants.USERS).document(getCurrentUserID())
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

    fun uploadImageToCloudStorage(activity: Activity, imagesFileURI: ArrayList<Uri?>, imageType: String) {
        val uriList: ArrayList<String> = ArrayList()
        imagesFileURI.withIndex().forEach { (index, imageFileURI) ->
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                    activity, imageFileURI)
            )

            sRef.putFile(imageFileURI!!)
                .addOnSuccessListener { taskSnapshot ->

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            when (activity) {
                                is UserProfileActivity -> {
                                    activity.imageUploadSuccess(uri.toString())
                                }
                                is AddItemActivity -> {
                                    uriList.add(uri.toString())
                                    if (index == (imagesFileURI.size-1))
                                        activity.imageUploadSuccess(uriList)
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
    }

    fun uploadItemDetails(activity: AddItemActivity, itemInfo: Item, urlStrings: ArrayList<String?>) {
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
        val hashMap = HashMap<String, Any>()
        for (i in urlStrings) {
            hashMap["ImgLink_"+1] = i!!
        }

        /*mFirestore.collection(Constants.ITEMS)
            .document()
            .set(hashMap)*/


        /*mFireReference.reference.child("user").push().setValue(hashMap)
            .addOnSuccessListener { e->
                Log.i(
                    activity.javaClass.simpleName,
                    hashMap.toString())
            }
            .addOnFailureListener { e->
                Log.e(
                activity.javaClass.simpleName,
                "Error while uploading the item details.",
                e
                )
            }*/
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFirestore.collection(Constants.ITEMS)
            .get()
            .addOnSuccessListener { document ->

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
                Log.e(fragment.javaClass.simpleName,
                    "Error while getting dashboard item list"
                    , e)
            }
    }

    fun deleteItem(fragment: ItemFragment, itemId: String, imagesURL: ArrayList<String?>) {
        val pictureRef = FirebaseStorage.getInstance()
        imagesURL.withIndex().forEach { (index, imageURL) ->
            pictureRef
                .reference
                .child(pictureRef
                    .getReferenceFromUrl(imageURL!!).name)
                .delete()
                .addOnSuccessListener {
                    mFirestore.collection(Constants.ITEMS)
                        .document(itemId)
                        .delete()
                        .addOnSuccessListener {
                            if (index == imagesURL.size - 1)
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
                .addOnFailureListener {

                }
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

    fun addCartItems(activity: ItemDetailsActivity, addToCart: CartItem) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }

    fun checkIfItemExistInCart(activity: ItemDetailsActivity, itemId: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.ITEM_ID, itemId)
            .get()
            .addOnSuccessListener { document ->
                if (document.documents.size > 0) {
                    activity.itemExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when(activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the cart list items.",
                    e
                )
            }
    }

    fun getAllItemsList(activity: Activity) {
        mFirestore.collection(Constants.ITEMS)
            .get()
            .addOnSuccessListener { document ->

                val itemsList: ArrayList<Item> = ArrayList()
                for (i in document.documents) {
                    val item = i.toObject(Item::class.java)
                    item!!.item_id = i.id

                    itemsList.add(item)
                }

                when(activity) {
                    is CartListActivity -> {
                        activity.successItemsListFromFireStore(itemsList)
                    }
                    is CheckoutActivity -> {
                        activity.successItemsListFromFireStore(itemsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting all item list",
                    e
                )
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when(context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when(context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

    fun getEmailFromUser(context: Context, user: String) {

        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.USERNAME, user)
            .get()
            .addOnSuccessListener { document ->
                val us = document.documents
                val email = us[0].toObject(User::class.java)?.email!!
                when(context) {
                    is LoginActivity -> {
                        context.loginRegisteredUserWithEmail(email)
                    }
                    is ForgotPasswordActivity -> {
                        context.forgetPasswordWithEmail(email)
                    }
                }
            }
            .addOnFailureListener { e->
                when(context) {
                    is LoginActivity -> {
                        context.hideProgressDialog()
                    }
                    is ForgotPasswordActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while get Email from username",
                    e
                )
            }
    }

    fun checkUsernameExists(context: RegisterActivity, username: String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.USERNAME, username)
            .get()
            .addOnSuccessListener { document ->

                if (document.documents.isEmpty()){
                    context.registerDetails()
                } else {
                    context.hideProgressDialog()
                    context.showErrorSnackBar(
                        context.resources
                            .getString(R.string.err_msg_username_used), true
                    )
                }
            }
            .addOnFailureListener { e->
                context.hideProgressDialog()
                Log.e(context.javaClass.simpleName,
                "Error while Check if username exist.",
                e)
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFirestore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    fun getAddressesList(activity: AddressListActivity) {
        mFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the AddressList",
                    e
                    )
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address",
                    e
                )
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address",
                    e
                )
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFirestore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }
}