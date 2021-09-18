package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

data class Item(
    val user_id: String? = "",
    val user_email: String? = "",
    val profile_username: String? = "",
    val profile_image: String? = "",
    val title: String? = "",
    val description: String? = "",
    val price: String? = "",
    val stock_quantity: String? = "",
    val images: ArrayList<String?> = ArrayList(),
    var item_id: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList() as ArrayList<String?>,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(user_email)
        parcel.writeString(profile_username)
        parcel.writeString(profile_image)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(price)
        parcel.writeString(stock_quantity)
        parcel.writeStringList(images)
        parcel.writeString(item_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }

}