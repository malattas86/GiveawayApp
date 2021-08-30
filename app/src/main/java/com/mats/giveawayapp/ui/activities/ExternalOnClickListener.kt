package com.mats.giveawayapp.ui.activities

import android.content.Context
import android.content.Intent
import android.view.View

class ExternalOnClickListener {
    companion object {
        fun onClickToLogin(packageContext: Context, @Suppress("UNUSED_PARAMETER")view: View?) {
            val intent = Intent(packageContext, LoginActivity::class.java)
            packageContext.startActivity(intent)
        }
    }
}