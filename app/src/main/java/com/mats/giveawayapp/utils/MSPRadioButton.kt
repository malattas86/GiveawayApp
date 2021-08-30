package com.mats.giveawayapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MSPRadioButton(context: Context, attributeSet: AttributeSet)
    :AppCompatRadioButton(context, attributeSet) {

    init {
        // Call the function to apply the font to the components.
        applyFont()
    }

    private fun applyFont() {
        // This is used to get the file from the assets folder and set it to the Text
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "montserrat-bold.otf")
        setTypeface(typeface)
    }
}