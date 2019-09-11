package com.newscorps.newsguessing

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.newscorps.newsguessing.entity.Item
import com.newscorps.newsguessing.entity.NewsItems

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URL

class MainActivity : AppCompatActivity(), AnkoLogger {

    var url= "https://firebasestorage.googleapis.com/v0/b/nca-dna-apps-dev.appspot.com/o/"

    var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        CoroutineScope(Dispatchers.Main).launch{

            withContext(Dispatchers.IO){

                var response=getFromRetrofit()

                var news = response.awaitResponse().body()

                if(news!=null){

                    for(item in news.items){

                       // info("${item.correctAnswerIndex} ${item.imageUrl}")
                        items.add(item)
                    }
                }

            }

        }


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}


interface NewService{

    @GET("game.json?alt=media&token=e36c1a14-25d9-4467-8383-a53f57ba6bfe")
    fun getAllNewsGame(): Call<NewsItems>

}