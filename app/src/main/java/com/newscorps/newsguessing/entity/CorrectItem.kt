package com.newscorps.newsguessing.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CorrectItem(
                        val imageUrl: String,
                        val standFirst: String,
                        val storyUrl: String):Parcelable{
    constructor():this("","","")
}