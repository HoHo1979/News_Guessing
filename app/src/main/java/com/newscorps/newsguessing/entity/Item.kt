package com.newscorps.newsguessing.entity

data class Item(
    val correctAnswerIndex: Int,
    val headlines: List<String>,
    val imageUrl: String,
    val section: String,
    val standFirst: String,
    val storyUrl: String
){
    constructor():this(0, mutableListOf(),"","","","")
}
