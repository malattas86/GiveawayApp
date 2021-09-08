package com.mats.giveawayapp.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityForgotPasswordBinding
import com.mats.giveawayapp.firestore.FirestoreClass

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarForgotPasswordActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title=""
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun onClickSubmit() {
        val email = binding.etEmail.text.toString().trim { it <= ' '}

        if (email.isEmpty()) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_username_or_email), true)
        } else {
            showProgressDialog(resources.getString(R.string.please_wait))
            if (email.contains("@")) {
                forgetPasswordWithEmail(email)
            } else {
                FirestoreClass().getEmailFromUser(this, email)
            }
        }
    }

    fun forgetPasswordWithEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    // Show the toast message and finish the forgot password activity to go
                    // back to login screen
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        resources.getString(R.string.email_sent_success),
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                } else {
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    override fun onClick(v: View?) {
        if (v != null)
        {
            when(v) {
                binding.btnSubmit -> {
                    onClickSubmit()
                }
            }
        }
    }
}