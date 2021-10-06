package com.mats.giveawayapp.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService()
{
    override fun onNewToken(p0: String)
    {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken = FirebaseInstallations.getInstance().getToken(true)


        if (firebaseUser!= null)
        {
            updateToken(refreshToken.result.token)
        }
    }



    private fun updateToken(refreshToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")
        val token = Token(refreshToken!!)
        ref.child(firebaseUser!!.uid).setValue(token)
    }
}