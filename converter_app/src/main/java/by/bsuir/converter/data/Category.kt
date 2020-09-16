package by.bsuir.converter.data

import android.os.Parcelable
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int,
    @ArrayRes val unitsId: Int,
    val type: CategoryType
) : Parcelable