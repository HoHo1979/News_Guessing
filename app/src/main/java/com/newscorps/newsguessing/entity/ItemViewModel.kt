package com.newscorps.newsguessing.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class ItemViewModel:ViewModel(){

    var newsLiveData = NewsLiveData()

    fun getNewsItem(): LiveData<List<Item>> {

        return newsLiveData
    }

}


