package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

data class Order(
    val user_id: String? = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address? = Address(),
    val title: String? = "",
    val image: String? = "",
    val sub_total_amount: String? = "",
    val shipping_charge: String? = "",
    val total_amount: String? = "",
    var id: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArrayList() as ArrayList<CartItem>,
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeStringList(items as ArrayList<String?>)
        parcel.writeParcelable(address, flags)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(sub_total_amount)
        parcel.writeString(shipping_charge)
        parcel.writeString(total_amount)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}