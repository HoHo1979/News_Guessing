package com.newscorps.newsguessing.entity

data class NewsItems(
    val items: List<Item>,
    val product: String,
    val resultSize: Int,
    val version: Int
)


