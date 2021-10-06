package com.mats.giveawayapp.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.models.User2
import com.mats.giveawayapp.ui.activities.RegisterActivity
import com.mats.giveawayapp.ui.fragments.ChatFragment
import com.mats.giveawayapp.utils.Constants

class FirestoreRefClass {

    private val mFireReference = FirebaseDatabase.
    getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The "user" is collection name. If the collection is already created then it will not
        // create the same one again.
        userInfo.uid.let {
            mFireReference.reference
                .child(Constants.USERS)
                .child(it!!)
                .setValue(userInfo)
                .addOnSuccessListener {
                    activity.user2RegistrationSuccess()
                }
                .addOnFailureListener { e->
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

    fun setUserProfileImage(context: ChatFragment) {
        mFireReference.reference.child(Constants.USERS)
            .child(FirestoreClass().getCurrentUserID())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && (FirebaseAuth.getInstance().currentUser != null)) {
                        val user: User2? = snapshot.getValue(User2::class.java)
                        context.setUserDetails(user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}
