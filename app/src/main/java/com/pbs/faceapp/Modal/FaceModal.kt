package com.pbs.faceapp.Modal

import android.os.Parcel
import android.os.Parcelable

data class FaceModal(
    val faceNo: Int=0,
    val smileProbability: Float = 0f,
    val leftEyeOpenProbability: Float=0f,
    val rightEyeOpenProbability: Float=0f
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat()
    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(faceNo)
        parcel.writeFloat(smileProbability)
        parcel.writeFloat(leftEyeOpenProbability)
        parcel.writeFloat(rightEyeOpenProbability)
    }

    companion object CREATOR : Parcelable.Creator<FaceModal> {
        override fun createFromParcel(parcel: Parcel): FaceModal {
            return FaceModal(parcel)
        }

        override fun newArray(size: Int): Array<FaceModal?> {
            return arrayOfNulls(size)
        }
    }
}