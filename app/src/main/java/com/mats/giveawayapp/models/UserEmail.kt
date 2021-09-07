package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

open class UserEmail(
    val user: String? = "",
    val email: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserEmail> {
        override fun createFromParcel(parcel: Parcel): UserEmail {
            return UserEmail(parcel)
        }

        override fun newArray(size: Int): Array<UserEmail?> {
            return arrayOfNulls(size)
        }
    }
}