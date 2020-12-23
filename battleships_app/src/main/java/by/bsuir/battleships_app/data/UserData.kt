package by.bsuir.battleships_app.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    val id: String = "",
    val nickname: String = "",
    val avatarUrl: String = ""
) : Parcelable
