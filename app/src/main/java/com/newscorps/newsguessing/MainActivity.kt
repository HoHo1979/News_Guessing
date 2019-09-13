package com.newscorps.newsguessing

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.newscorps.newsguessing.entity.Item
import com.newscorps.newsguessing.entity.ItemViewModel
import com.newscorps.newsguessing.entity.User
import com.newscorps.newsguessing.entity.clearThenAddList

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.answer_layout.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*
import java.util.stream.Collector
import java.util.stream.Collectors

class MainActivity : AppCompatActivity(), AnkoLogger {

    companion object{
        var QUESTION_ITEM="QUESTION_ITEM"
        var USER="USER"
    }

    lateinit var itemViewModel:ItemViewModel
    var anwserLists = mutableListOf<String>()
    var questionItem=Item()
    var questionList= mutableListOf<Item>()
    lateinit var adapter:AnswerAdapter
    var correctAnswerIndex=0
    var questionSize=0
    //A user just start the game
    var user=User("James",0,0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.title="Welcome ${user.name}"
        toolbar.subtitle="Guess This HeadLine"

        itemViewModel = ViewModelProvider.
            AndroidViewModelFactory.getInstance(this.application).create(ItemViewModel::class.java)

        //The json feed can be updated once new information come in.
        itemViewModel.getNewsItem().observe(this, Observer {



            //Test the progress bar when the questionList only has 10 items: pass
            //questionList.clearThenAddList(questionList,it.stream().limit(10).collect(Collectors.toList()))
            //questionSize=10

            questionList.clearThenAddList(questionList,it)

            questionSize=it.size

            progressBar.max = questionSize.toFloat()

            showNextItemOnView()

        })



        skipButton.setOnClickListener {

            showNextItemOnView()
        }

        //Display which question index
        indexTextView.text=(QuestionCounter.counter+1).toString()

        progressBar.apply {
            isReverse=false
        }


        //Recycle view for anwsers
        var anwserRecycler = answerRecycler
        anwserRecycler.setHasFixedSize(true)
        anwserRecycler.layoutManager= LinearLayoutManager(this)
        adapter =  AnswerAdapter(anwserLists,correctAnswerIndex,user,questionItem,this)
        anwserRecycler.adapter = adapter


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


    }


    override fun onResume() {
        super.onResume()


    }

    //Load next item on the View.
    fun showNextItemOnView() {

        if(QuestionCounter.counter<questionSize) {

            questionItem = questionList.get(QuestionCounter.counter)

            indexTextView.text = (QuestionCounter.counter + 1).toString()

            QuestionCounter.counter += 1

            CoroutineScope(Dispatchers.Main).launch {
                progressBar.progress = QuestionCounter.counter.toFloat()
                progressBar.secondaryProgress = (QuestionCounter.counter + 2).toFloat()
            }

            scoreTextView.text = "Your Score:" + user.score.toString()

            correctAnswerIndex = questionItem.correctAnswerIndex


            Glide.with(this)
                .load(questionItem.imageUrl)
      //          .apply(RequestOptions().override(150, 200))
                .into(newsImageView)


            anwserLists.clearThenAddList(anwserLists,questionItem.headlines)

            adapter.item = questionItem

            adapter.correctAnswerIndex = correctAnswerIndex

            adapter.notifyDataSetChanged()

        }else{

            Toast.makeText(this,"Go to ScoreBoard",Toast.LENGTH_LONG).show()

        }


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
class AnswerAdapter(var anwserList:List<String>,var correctAnswerIndex:Int,var user:User,var item:Item,var activity: MainActivity): RecyclerView.Adapter<AnswerAdapter.AnswerHolder>(),AnkoLogger {
    lateinit var context: Context
    lateinit var view:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder {
        context=parent.context
        view=parent
        return AnswerHolder(LayoutInflater.from(parent.context).inflate(R.layout.answer_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return anwserList.size
    }

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        holder.bind(anwserList,position)

        holder.itemView.setOnClickListener{

            //If anwser's poistion is equal to correctAnswerIndex the user is award 2 points,
            //incorrect answer will get minus 1 point.


            if(correctAnswerIndex==position){

                    user.score+=2

                    var intent= Intent(context,CorrectActivity::class.java)

                    intent.putExtra(MainActivity.QUESTION_ITEM,item)
                    intent.putExtra(MainActivity.USER,user)

                    context.startActivity(intent)


            }else{

                //Only deduct point when user has score greater than 0
                if((user.score-1)>0) {
                    user.score -= 1
                }

                it.setBackgroundColor(Color.parseColor("#FF3D00"))

                //Wrong answer would display red box so the user knew the answer was incorrect.
                GlobalScope.launch (Dispatchers.Main){

                    delay(500)

                    it.setBackgroundColor(Color.WHITE)

                    activity.showNextItemOnView()
                }

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


