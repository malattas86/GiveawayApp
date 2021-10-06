package com.mats.giveawayapp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mats.giveawayapp.firestore.FirestoreClass


open class MyApplication : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // app moved to foreground
        wasInBackground = true

        onlineStatus = "online"
        Handler(Looper.getMainLooper()).postDelayed({
            if ((onlineStatus == "online") && (oldStatus == "offline")) {
                updateStatus("online")
                oldStatus = "online"
            }
        }, 3000)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground(delayMillis: Long = 3000) {
        if (delayMillis == 0L) {
            updateStatus("offline")
        }
        // app moved to background
        wasInBackground = false

        onlineStatus = "offline"
        Handler(Looper.getMainLooper()).postDelayed({
            if ((onlineStatus == "offline") && (oldStatus == "online")) {
                updateStatus("offline")
                oldStatus = "offline"
            }
        }, delayMillis)
    }

    companion object {
        private var appContext: Context? = null
        var wasInBackground = false
        var onlineStatus = "offline"
        var oldStatus = "offline"
        fun getAppContext(): Context? {
            return appContext
        }
    }

    private fun updateStatus(status: String)
    {
        val ref = FirebaseDatabase
            .getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("users")

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        if (FirebaseAuth.getInstance().currentUser != null) {
            ref
                .child(FirestoreClass().getCurrentUserID())
                .updateChildren(hashMap)
        }
    }
}