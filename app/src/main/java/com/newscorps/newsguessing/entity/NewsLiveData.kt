package com.newscorps.newsguessing.entity

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//Create a New Live Data Object to seperate the datasource from the data provider,
// later if datasource is changed, this is the only file need to be modified.
class NewsLiveData: LiveData<List<Item>>(),AnkoLogger {

    var url= "https://firebasestorage.googleapis.com/v0/b/nca-dna-apps-dev.appspot.com/o/"

    var items= mutableListOf<Item>()

    lateinit var job:Job

    var isNewsUpdated=true

    override fun onActive() {

        if(isNewsUpdated) {

            job = CoroutineScope(Dispatchers.Main).launch {

                //Running on IO Thread
                withContext(Dispatchers.IO) {

                    var response = getFromRetrofit()


                    var news = response.awaitResponse().body()


                    if (news != null) {

                        items.clearThenAddList(items,news.items)

                    }

                }

                //Switch back on main thread and update the value
                value = items
                isNewsUpdated=false
            }
        }else{
            value=items
        }

    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }

    private fun getFromRetrofit(): Call<NewsItems> {

        var retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var newsItems =
            retrofit.create(NewService::class.java).getAllNewsGame()

        return newsItems;
    }
}

interface NewService{

    @GET("game.json?alt=media&token=e36c1a14-25d9-4467-8383-a53f57ba6bfe")
    fun getAllNewsGame(): Call<NewsItems>

}