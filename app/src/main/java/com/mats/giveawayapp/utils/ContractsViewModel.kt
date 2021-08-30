package com.mats.giveawayapp.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mats.giveawayapp.R
import java.io.IOException

class ContractsViewModel : ActivityResultContract<Unit, String?>() {

    fun onPermissionsResult(result: Boolean) {
        if (result) {

        } else {
            onPermissionsResult(result)
        }
    }

    override fun createIntent(context: Context, input: Unit?): Intent {
        TODO("Not yet implemented")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getStringExtra("data")
    }
}