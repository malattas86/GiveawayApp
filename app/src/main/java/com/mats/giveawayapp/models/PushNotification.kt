package com.mats.giveawayapp.models

import android.os.Parcel
import android.os.Parcelable

data class PushNotification(
    var data:NotificationData,
    var to: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("data"),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(to)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushNotification> {
        override fun createFromParcel(parcel: Parcel): PushNotification {
            return PushNotification(parcel)
        }

        override fun newArray(size: Int): Array<PushNotification?> {
            return arrayOfNulls(size)
        }
    }
}