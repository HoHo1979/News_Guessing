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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.newscorps.newsguessing.entity.Item
import com.newscorps.newsguessing.entity.ItemViewModel
import com.newscorps.newsguessing.entity.User

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.answer_layout.view.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger {

    lateinit var itemViewModel:ItemViewModel
    var anwserLists = mutableListOf<String>()
    var questionTotalSize=0
    var questionItem=Item()
    var questionList= mutableListOf<Item>()
    lateinit var adapter:AnswerAdapter
    var correctAnswerIndex=0
    //A user just start the game
    var user=User("James",0,0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        itemViewModel = ViewModelProvider.
            AndroidViewModelFactory.getInstance(this.application).create(ItemViewModel::class.java)

        //The json feed can be update once new information comes in.
        itemViewModel.getNewItems().observe(this, Observer {

            questionList.clear()
            questionList.addAll(it)

            questionItem = questionList.get(user.currentQuestionIndex)

            questionTotalSize = it.size
            //info("Total size of questions:${questionTotalSize}")


            showItemOnView()

        })

        skipButton.setOnClickListener {

            user.currentQuestionIndex+=1
            questionItem = questionList.get(user.currentQuestionIndex)
            showItemOnView()
        }

        //Display which question index
        indexTextView.text=(user.currentQuestionIndex+1).toString()


        //Recycle view for anwser
        var anwserRecycler = answerRecycler
        anwserRecycler.setHasFixedSize(true)
        anwserRecycler.layoutManager= LinearLayoutManager(this)
        adapter =  AnswerAdapter(anwserLists,correctAnswerIndex,user)
        anwserRecycler.adapter = adapter


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        

    }

    private fun showItemOnView() {

        correctAnswerIndex = questionItem.correctAnswerIndex

        Glide.with(this)
            .load(questionItem.imageUrl)
            .apply(RequestOptions().override(120, 200))
            .into(newsImageView)

        anwserLists.clear()
        anwserLists.addAll(questionItem.headlines)
        adapter.notifyDataSetChanged()
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

//RecycleView Adapter to display answers
class AnswerAdapter(var anwserList:List<String>,var correctAnswerIndex:Int,var user:User): RecyclerView.Adapter<AnswerAdapter.AnswerHolder>(),AnkoLogger {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder {
        context=parent.context
        return AnswerHolder(LayoutInflater.from(parent.context).inflate(R.layout.answer_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return anwserList.size
    }

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        holder.bind(anwserList,position)
        info("currect Index $correctAnswerIndex")
        holder.itemView.setOnClickListener{

            //If anwser's poistion is equal to correctAnswerIndex the user is award 2 point,
            //incorrect answer will get minus 1 point.
            if(correctAnswerIndex==position){
                info("Your score +2")
                user.score+=2
            }else{
                info("your score -1")
                user.score-=1
            }

        }
    }


    inner class AnswerHolder(itemView: View):RecyclerView.ViewHolder(itemView){



        fun bind(answerList: List<String>, position: Int) {
            info(answerList.size)
            itemView.answerTextView.text= (position+1).toString()+": "+answerList.get(position)
        }

    }

}


