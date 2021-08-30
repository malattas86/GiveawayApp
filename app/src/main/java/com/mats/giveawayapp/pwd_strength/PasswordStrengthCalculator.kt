package com.mats.giveawayapp.pwd_strength

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import com.mats.giveawayapp.R
import com.mats.giveawayapp.utils.Constants
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordStrengthCalculator: TextWatcher {

    var strengthLevel: MutableLiveData<String> = MutableLiveData()
    var strengthColor: MutableLiveData<Int> = MutableLiveData()

    var lowercase: MutableLiveData<Boolean> = MutableLiveData(false)
    var uppercase: MutableLiveData<Boolean> = MutableLiveData(false)
    var digit: MutableLiveData<Boolean> = MutableLiveData(false)
    var specialChar: MutableLiveData<Boolean> = MutableLiveData(false)
    var minLength: MutableLiveData<Boolean> = MutableLiveData(false)
    var maxLength: MutableLiveData<Boolean> = MutableLiveData(true)

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (char != null) {
            lowercase.value = char.hasLowercase()
            uppercase.value = char.hasUppercase()
            digit.value = char.hasDigit()
            specialChar.value = char.hasSpecialChar()
            calculateStrength(char)
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    private fun CharSequence.hasLowercase(): Boolean{
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowercase: Matcher = pattern.matcher(this)
        return hasLowercase.find()
    }

    private fun CharSequence.hasUppercase(): Boolean{
        val pattern: Pattern = Pattern.compile("[A-Z]")
        val hasUppercase: Matcher = pattern.matcher(this)
        return hasUppercase.find()
    }

    private fun CharSequence.hasDigit(): Boolean{
        val pattern: Pattern = Pattern.compile("[0-9]")
        val hasDigit: Matcher = pattern.matcher(this)
        return hasDigit.find()
    }

    private fun CharSequence.hasSpecialChar(): Boolean{
        val pattern: Pattern = Pattern.compile("[!@#$§%^&*()_=+{}/.<>|\\[\\]~-]")
        val hasSpecialChar: Matcher = pattern.matcher(this)
        return hasSpecialChar.find()
    }

    private fun calculateStrength(password: CharSequence) {
        if (password.length in 0..7) {
            strengthColor.value = R.color.weak
            strengthLevel.value = Constants.PWD_WEAK
            minLength.value = false
            maxLength.value = true
        } else if (password.length in 8..10) {
            minLength.value = true
            maxLength.value = true
            if (lowercase.value!! || uppercase.value!! || digit.value!! || specialChar.value!!) {
                strengthColor.value = R.color.medium
                strengthLevel.value = Constants.PWD_MEDIUM
            }
        } else if (password.length in 11..16) {
            minLength.value = true
            maxLength.value = true
            if (lowercase.value!! || uppercase.value!! || digit.value!! || specialChar.value!!) {
                if (lowercase.value!! && uppercase.value!!) {
                    strengthColor.value = R.color.strong
                    strengthLevel.value = Constants.PWD_STRONG
                }
            }
        } else if (password.length in 16..71) {
            minLength.value = true
            maxLength.value = true
            if (lowercase.value!! && uppercase.value!! && digit.value!! && specialChar.value!!) {
                strengthColor.value = R.color.bulletproof
                strengthLevel.value = Constants.PWD_VERY_STRONG
            }
        }
        else {
            minLength.value = true
            maxLength.value = false
        }
    }
}