package com.newscorps.newsguessing.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var name:String, var score:Int,var currentQuestionIndex:Int): Parcelable {

    constructor():this("",0,0)

}
