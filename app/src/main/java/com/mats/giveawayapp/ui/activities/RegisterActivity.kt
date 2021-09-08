package com.mats.giveawayapp.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityRegisterBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.User
import com.mats.giveawayapp.pwd_strength.PasswordStrengthCalculator
import com.mats.giveawayapp.utils.Constants

class RegisterActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private var mColor: Int = R.color.weak
    private var mUsernameAlreadyUsed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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

        binding.etRegisterPassword.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                binding.llPwdStrength.visibility = GONE
                binding.llPwdCheckStrength.visibility = GONE
            }
        }

        binding.btnRegister.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)

        observersPassword()

    }
    private fun observersPassword() {
        val passwordStrengthCalculator = PasswordStrengthCalculator()
        binding.etRegisterPassword.addTextChangedListener(passwordStrengthCalculator)

        // Observers
        passwordStrengthCalculator.strengthLevel.observe(this, { strengthLevel ->
            displayStrengthLevel(strengthLevel)
        })
        passwordStrengthCalculator.strengthColor.observe(this, { strengthColor ->
            mColor = strengthColor
        })
        passwordStrengthCalculator.lowercase.observe(this, { value ->
            displayPasswordSuggestions(value, binding.ivLowercase, binding.tvLowercase)
        })
        passwordStrengthCalculator.uppercase.observe(this, { value ->
            displayPasswordSuggestions(value, binding.ivUppercase, binding.tvUppercase)
        })
        passwordStrengthCalculator.digit.observe(this, { value ->
            displayPasswordSuggestions(value, binding.ivDigit, binding.tvDigit)
        })
        passwordStrengthCalculator.specialChar.observe(this, { value ->
            displayPasswordSuggestions(value, binding.ivSpecialChar, binding.tvSpecialChar)
        })
        passwordStrengthCalculator.minLength.observe(this, { value ->
            displayPasswordSuggestions(value, binding.ivMinChar, binding.tvMinChar)
        })
        passwordStrengthCalculator.maxLength.observe(this, { value ->
            displayPasswordSuggestions(value, binding.ivMaxChar, binding.tvMaxChar)
        })
    }
    private fun displayPasswordSuggestions(value: Boolean, imgView: ImageView, textView: TextView) {
        if (value) {
            imgView.setColorFilter(ContextCompat.getColor(this, R.color.bulletproof))
            textView.setTextColor(ContextCompat.getColor(this, R.color.bulletproof))
        } else {
            imgView.setColorFilter(ContextCompat.getColor(this, R.color.colorDarkGrey))
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGrey))
        }
    }

    private fun displayStrengthLevel(strengthLevel: String) {

        binding.passwordStrength.text = strengthLevel
        binding.passwordStrength.setTextColor(ContextCompat.getColor(this, mColor))
        when (strengthLevel) {
            Constants.PWD_WEAK -> {
                binding.pwdProgressBar.progress = 25
            }
            Constants.PWD_MEDIUM -> {
                binding.pwdProgressBar.progress = 50
            }
            Constants.PWD_STRONG -> {
                binding.pwdProgressBar.progress = 75
            }
            else -> {
                binding.pwdProgressBar.progress = 100
            }
        }

        binding.pwdProgressBar.progressDrawable.colorFilter = BlendModeColorFilterCompat
            .createBlendModeColorFilterCompat(ContextCompat.getColor(this, mColor), BlendModeCompat.SRC_ATOP)
        binding.llPwdStrength.visibility = VISIBLE
        binding.llPwdCheckStrength.visibility = VISIBLE
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarRegisterActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title=""
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etUsername.text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_username), true)
                false
            }

            (binding.etUsername.text.toString().length < 6) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_username_length), true)
                false
            }

            TextUtils.isEmpty(binding.etRegisterEmail.text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etRegisterPassword.text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.etRegisterConfirmPassword.text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            binding.etRegisterPassword.text.toString().trim{ it <= ' '}
                    != binding.etRegisterConfirmPassword.text.toString().trim { it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }

            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }

            else -> {
                // showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val username: String = binding.etUsername.text.toString().trim { it <= ' '}

            // check if username in use be another account.
            FirestoreClass().checkUsernameExists(this, username)
        }
    }

    fun registerDetails() {
        val email: String = binding.etRegisterEmail.text.toString().trim { it <= ' '}
        val password: String = binding.etRegisterPassword.text.toString().trim { it <= ' '}

        // Create an instance and create a register a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                // If the registration is successfully done
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                firebaseUser.uid,
                                userName = binding.etUsername.text.toString().trim { it <= ' '},
                                email = binding.etRegisterEmail.text.toString().trim { it <= ' '}
                            )

                            FirestoreClass().registerUser(this, user)
                        }

                } else {
                    hideProgressDialog()
                    // If the registering is not successful then show error message.
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userRegistrationSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Register Screen
        finish()
    }

    private fun onClickToLogin() {
        onBackPressed()
        //ExternalOnClickListener.onClickToLogin(this@RegisterActivity, view)
        // intent = Intent(applicationContext, LoginActivity::class.java)
        // startActivity(intent)
    }

    private fun onClickRegister() {
        registerUser()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v) {
                binding.tvLogin -> {
                    onClickToLogin()
                }

                binding.btnRegister -> {
                    onClickRegister()
                }
            }
        }
    }

    fun usernameFromFireStorage(username: String) {
        hideProgressDialog()
        mUsernameAlreadyUsed = username != ""
    }
}