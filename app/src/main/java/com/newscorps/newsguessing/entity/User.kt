package com.newscorps.newsguessing.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class User(@PrimaryKey var name:String, var score:Int, var currentQuestionIndex:Int): Parcelable {

    constructor():this("",0,0)

}
