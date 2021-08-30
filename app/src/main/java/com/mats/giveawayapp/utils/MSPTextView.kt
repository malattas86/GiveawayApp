package com.mats.giveawayapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    init {
        // Call the function to apply the font to the components.
        applyFont()
    }

    private fun applyFont() {
        // This is used to get the file from the assets folder and set it to the Text
        val typeface: Typeface =
            Typeface.createFromAsset(
                context.assets,
                "montserrat-regular.otf"
            )
        setTypeface(typeface)
    }
}