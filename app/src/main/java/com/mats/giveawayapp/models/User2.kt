package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

data class User2(
    val uid:String? = "",
    val username:String? = "",
    val profileImage: String? = "",
    val coverImage: String? = "",
    val status: String? = "",
    val search: String? = "",
    val facebook:String? = "",
    val instagram:String? = "",
    val website:String? = ""
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
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(username)
        parcel.writeString(profileImage)
        parcel.writeString(coverImage)
        parcel.writeString(status)
        parcel.writeString(search)
        parcel.writeString(facebook)
        parcel.writeString(instagram)
        parcel.writeString(website)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User2> {
        override fun createFromParcel(parcel: Parcel): User2 {
            return User2(parcel)
        }

        override fun newArray(size: Int): Array<User2?> {
            return arrayOfNulls(size)
        }
    }
}