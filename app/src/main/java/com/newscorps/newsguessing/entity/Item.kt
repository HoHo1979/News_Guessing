package com.newscorps.newsguessing.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val correctAnswerIndex: Int,
    val headlines: List<String>,
    val imageUrl: String,
    val section: String,
    val standFirst: String,
    val storyUrl: String
):Parcelable{
    constructor():this(0, mutableListOf(),"","","","")
}
