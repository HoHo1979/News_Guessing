package com.newscorps.newsguessing.entity

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//Create a New Live Data Object to seperate the datasource from the data provider,
// later if datasource is changed the only this file need to be modified.
class NewsLiveData: LiveData<List<Item>>() {

    var url= "https://firebasestorage.googleapis.com/v0/b/nca-dna-apps-dev.appspot.com/o/"

    var items= mutableListOf<Item>()

    override fun onActive() {

        CoroutineScope(Dispatchers.Main).launch{

            //Running on other Thread
            withContext(Dispatchers.IO){

                var response=getFromRetrofit()

                var news = response.awaitResponse().body()

                if(news!=null){

                    for(item in news.items){


                        items.add(item)
                    }

                }

            }

            //Switch back on main thread and update the value
            value=items

        }

    }

    private fun getFromRetrofit(): Call<NewsItems> {

        var retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var newService =
            retrofit.create(NewService::class.java).getAllNewsGame()

        return newService;
    }
}

interface NewService{

    @GET("game.json?alt=media&token=e36c1a14-25d9-4467-8383-a53f57ba6bfe")
    fun getAllNewsGame(): Call<NewsItems>

}