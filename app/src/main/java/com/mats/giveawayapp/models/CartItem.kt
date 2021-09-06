package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    val user_id: String? = "",
    val item_id: String? = "",
    val title: String? = "",
    val price: String? = "",
    val images: ArrayList<String?> = ArrayList(),
    var cart_quantity: String? = "",
    var stock_quantity: String? = "",
    var id: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList() as ArrayList<String?>,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(item_id)
        parcel.writeString(title)
        parcel.writeString(price)
        parcel.writeStringList(images)
        parcel.writeString(cart_quantity)
        parcel.writeString(stock_quantity)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}