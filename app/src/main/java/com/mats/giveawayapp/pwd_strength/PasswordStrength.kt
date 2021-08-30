package com.mats.giveawayapp.pwd_strength

import android.graphics.Color
import com.mats.giveawayapp.R

enum class PasswordStrength(private var resId: Int, color: Int) {
    WEAK(R.string.password_strength_weak, Color.RED),
    MEDIUM(R.string.password_strength_medium, Color.argb(255, 220, 185, 0)),
    STRONG(R.string.password_strength_strong, Color.GREEN),
    VERY_STRONG(R.string.password_strength_very_strong, Color.BLUE);

    var color: Int = 0
    internal set

    init {
        this.color = color
    }

    fun getText(ctx: android.content.Context): CharSequence {
        return ctx.getText(resId)
    }


    companion object {
        // This value defines the minimum length for the password
        private var REQUIRED_LENGTH = 8

        // This value determines the maximum length of the password
        private var MAXIMUM_LENGTH = 72

        // This value determines if the password should contain special characters.
        // set is as "false" if you do not require special characters for your password field
        private var REQUIRED_SPECIAL_CHARACTERS = true

        // This value determines if the password should contain digits.
        // set is as "false" if you do not require digits for your password field
        private var REQUIRE_DIGITS = true

        // This value determines if the password should contain low case.
        // set is as "false" if you do not require low case for your password field
        private var REQUIRE_LOWER_CASE = true

        // This value determines if the password should contain upper case.
        // set is as "false" if you do not require upper case for your password field
        private var REQUIRE_UPPER_CASE = false

        fun calculateStrength(password: String): PasswordStrength {
            var currentScore = 0
            var sawUpper = false
            var sawLower = false
            var sawDigit = false
            var sawSpecial = false

            for (element in password) {

                if (!sawSpecial && !Character.isLetterOrDigit(element)) {
                    currentScore += 1
                    sawSpecial = true
                } else {
                    if (!sawDigit && Character.isDigit(element)) {
                        currentScore += 1
                        sawDigit = true
                    } else {
                        if (!sawLower || !sawUpper) {
                            if (Character.isLowerCase(element))
                                sawLower = true
                            else
                                sawUpper = true
                            if (sawLower && sawUpper)
                                currentScore += 1
                        }
                    }
                }
            }

            if (password.length > REQUIRED_LENGTH) {
                if (REQUIRED_SPECIAL_CHARACTERS && !sawSpecial
                    || REQUIRE_DIGITS && !sawDigit
                    || REQUIRE_LOWER_CASE && !sawLower
                    || REQUIRE_UPPER_CASE && !sawUpper) {
                    currentScore = 1
                } else {
                    currentScore = 2
                    if (password.length > MAXIMUM_LENGTH) {
                        currentScore = 3
                    }
                }
            } else {
                currentScore = 0
            }

            when (currentScore) {
                0 -> return WEAK
                1 -> return MEDIUM
                2 -> return STRONG
                3 -> return VERY_STRONG
            }

            return VERY_STRONG

        }
    }
}