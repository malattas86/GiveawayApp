package com.mats.giveawayapp.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityLoginBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.utils.Constants

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLoginBinding.inflate(layoutInflater).also { binding = it }
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
    }

    private fun onClickToRegister() {
        startActivity(Intent(applicationContext, RegisterActivity::class.java))
    }

    private fun onClickLogin() {
        loginRegisteredUser()
    }

    private fun onClickForgotPassword() {
        startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etLoginEmail.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etLoginPassword.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser() {
        if (validateLoginDetails()) {

            // show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            val email = binding.etLoginEmail.text.toString().trim { it <= ' '}
            if (email.contains("@")) {
                loginRegisteredUserWithEmail(email)
            } else {
                FirestoreClass().getEmailFromUser(this, email)
            }
        }
    }

    fun loginRegisteredUserWithEmail(email: String) {
        // Get the text from editText and trim the space
        val password = binding.etLoginPassword.text.toString().trim { it <= ' '}

        // Log-In using FirebaseAuth
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if(FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(
                            resources.getString(R.string.pleas_verify_email),
                            true)
                        /**
                         * Here we just sign-out until the user verify his email
                         */
                        FirebaseAuth.getInstance().signOut()

                    }
                } else {
                    hideProgressDialog()
                    showErrorSnackBar(
                        task.exception!!.message.toString(),
                        true)
                }
            }
    }


    fun userLoggedInSuccess(user: User) {
        // Hide the progress dialog.
        hideProgressDialog()

        if (user.profileCompleted == 0) {
            // If Userprofile not completed redirect the user to Userprofile Screen after log in.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            // Redirect the user to Main Screen after log in.
            //startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            onBackPressed()
        }

        finish()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v) {
                binding.btnLogin -> {
                    onClickLogin()
                }

                binding.tvForgotPassword -> {
                    onClickForgotPassword()
                }

                binding.tvRegister -> {
                    onClickToRegister()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}