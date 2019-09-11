package com.newscorps.newsguessing

import android.content.Context
import android.os.Bundle
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newscorps.newsguessing.entity.Item
import com.newscorps.newsguessing.entity.ItemViewModel
import com.newscorps.newsguessing.entity.NewsItems

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.answer_layout.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
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

    lateinit var itemViewModel:ItemViewModel
    var anwserLists = mutableListOf<String>()
    var questionIndex=0
    lateinit var questionItem:Item
    lateinit var adapter:AnswerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        itemViewModel = ViewModelProvider.
            AndroidViewModelFactory.getInstance(this.application).create(ItemViewModel::class.java)

        //The json feed can be update once new information comes in.
        itemViewModel.getNewItems().observe(this, Observer {

            questionItem = it.get(questionIndex)
            anwserLists.clear()
            anwserLists.addAll(questionItem.headlines)
            adapter.notifyDataSetChanged()

        })

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }



        var anwserRecycler = answerRecycler
        anwserRecycler.setHasFixedSize(true)
        anwserRecycler.layoutManager= LinearLayoutManager(this)
        adapter =  AnswerAdapter(anwserLists)
        anwserRecycler.adapter = adapter

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


class AnswerAdapter(var answerList:List<String>): RecyclerView.Adapter<AnswerAdapter.AnswerHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder {
        context=parent.context
        return AnswerHolder(LayoutInflater.from(parent.context).inflate(R.layout.answer_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        holder.bind(answerList,position)
    }


    inner class AnswerHolder(itemView: View):RecyclerView.ViewHolder(itemView){



        fun bind(answerList: List<String>, position: Int) {

            itemView.answerTextView.text= answerList.get(position)

        }

    }

}


