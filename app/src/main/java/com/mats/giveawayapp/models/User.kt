package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

class User(
    val uid: String? = "",
    val username: String? = "",
    val email: String? = "",
    val firstname: String? = "",
    val lastname: String? = "",
    val profileImage: String? = "",
    val mobile: Long = 0,
    val gender: String? = "",
    val status: String? = "offline",
    val search: String? = "",
    val facebook:String? = "https://m.facebook.com",
    val instagram:String? = "https://m.instgram.com",
    val website:String? = "https://m.google.com",
    val profileCompleted: Int = 0
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(firstname)
        parcel.writeString(lastname)
        parcel.writeString(profileImage)
        parcel.writeLong(mobile)
        parcel.writeString(gender)
        parcel.writeString(status)
        parcel.writeString(search)
        parcel.writeString(facebook)
        parcel.writeString(instagram)
        parcel.writeString(website)
        parcel.writeInt(profileCompleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}


