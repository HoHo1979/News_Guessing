package com.newscorps.newsguessing.entity

import android.util.Log

inline fun <reified T>List<T>.clearThenAddList(list:MutableList<T>, newList:List<T>){

    list.clear()
    list.addAll(newList)


}
