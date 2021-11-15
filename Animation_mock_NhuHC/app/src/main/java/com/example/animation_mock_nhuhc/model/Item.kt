package com.example.animation_mock_nhuhc.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item (var name:String, var cash:Int, var path:Int, var color:Int): Parcelable