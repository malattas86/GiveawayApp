package com.mats.giveawayapp.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import com.mats.giveawayapp.R
import com.mats.giveawayapp.models.AlertDialogItem
import com.mats.giveawayapp.ui.activities.MessageChatActivity
import com.mats.giveawayapp.ui.activities.SettingsActivity

class AlertDialog(
    context: Context,
    items: Array<AlertDialogItem>,
    userID: String?,
    userName: String?
) {
    private val mContext = context
    private val mItems = items
    private val mUserID = userID
    private val mUserName = userName
    fun build(){
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(mContext.resources.getColor(R.color.colorSecondary, mContext.theme))
        gradientDrawable.cornerRadius = 75f

        val adapter: ListAdapter = object : ArrayAdapter<AlertDialogItem?>(
            mContext,
            android.R.layout.select_dialog_item,
            android.R.id.text1,
            mItems
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //Use super class to create the View
                val v = super.getView(position, convertView, parent)

                val tv = v.findViewById<View>(android.R.id.text1) as TextView
                tv.setTextColor(mContext.resources.getColor(R.color.white, mContext.theme))
                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(mItems[position].icon, 0, 0, 0)

                //Add margin between image and text (support various screen densities)
                val dp5 = (5 * mContext.resources.displayMetrics.density + 0.5f).toInt()
                tv.compoundDrawablePadding = dp5

                return v
            }
        }


        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setTitle("")
        builder.setAdapter(adapter) { _, position ->
            when (position) {
                0 -> {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra(Constants.EXTRA_ITEM_OWNER_ID, mUserID)
                    intent.putExtra(Constants.EXTRA_VISIT_USERNAME, mUserName)
                    mContext.startActivity(intent)
                }
                1 -> {
                    val intent = Intent(mContext, SettingsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_ITEM_OWNER_ID, mUserID)
                    mContext.startActivity(intent)
                }
            }
        }
        builder.show()
            .window?.setBackgroundDrawable(gradientDrawable)
    }
}