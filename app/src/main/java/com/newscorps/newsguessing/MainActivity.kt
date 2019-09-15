package com.newscorps.newsguessing

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newscorps.newsguessing.database.UserDatabase
import com.newscorps.newsguessing.entity.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.answer_layout.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

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

    //A currentUser just start the game
    var currentUser=User("James",0,0)

    lateinit var job:Job
    lateinit var job1:Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        //Retrieve user data from sqlite and update the score
        //This can be move to userRepository and compare withe network information.
        job=CoroutineScope(Dispatchers.IO).launch{

           var userDao= UserDatabase.getInstance(application)?.getUserDao()

            var users=userDao?.getUser()

            if(users==null || users.size==0){
                info("Game just started")
                userDao?.addUser(currentUser)
            }else {
                var myuser = userDao?.getUserByName(currentUser.name)
                withContext(Dispatchers.Main) {
                    myuser?.let {
                        QuestionCounter.counter = it.currentQuestionIndex
                        currentUser.score = it.score
                        //update the UI with information store on the sqlite
                        scoreTextView.text=currentUser.score.toString()
                        indexTextView.text=(QuestionCounter.counter+1).toString()
                    }
                }
            }
        }


        supportActionBar?.title="Welcome ${currentUser.name}"

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
        adapter =  AnswerAdapter(anwserLists,correctAnswerIndex,currentUser,questionItem,this)
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

            //Progress Bard, index and total questions update on the view
            progressBar.max = questionSize.toFloat()
            totalQuestiongsTextView.text=questionSize.toString()
            indexTextView.text = (QuestionCounter.counter + 1).toString()


            questionItem = questionList.get(QuestionCounter.counter)

            currentUser.currentQuestionIndex = QuestionCounter.counter

            QuestionCounter.counter += 1

            CoroutineScope(Dispatchers.Main).launch {
                progressBar.progress = QuestionCounter.counter.toFloat()
                progressBar.secondaryProgress = (QuestionCounter.counter + 2).toFloat()
            }

            scoreTextView.text = currentUser.score.toString()

            correctAnswerIndex = questionItem.correctAnswerIndex


            Glide.with(this)
                .load(questionItem.imageUrl)
      //          .apply(RequestOptions().override(150, 200))
                .into(newsImageView)


            anwserLists.clearThenAddList(anwserLists,questionItem.headlines)

            adapter.item = questionItem

            adapter.correctAnswerIndex = correctAnswerIndex

            adapter.notifyDataSetChanged()

            saveCurrentUserInfoToSqlite()

        }else{

            Toast.makeText(this,"Go to ScoreBoard",Toast.LENGTH_LONG).show()

        }


    }

    fun saveCurrentUserInfoToSqlite() {

        info("current User ${currentUser.name} ,${currentUser.score}, ${currentUser.currentQuestionIndex}")


        job1=CoroutineScope(Dispatchers.IO).launch {

            var userDao= UserDatabase.getInstance(application)?.getUserDao()

            if(userDao!=null){
                userDao.updateUser(currentUser)
            }

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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        job1.cancel()
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

            //If anwser's poistion is equal to correctAnswerIndex the currentUser is award 2 points,
            //incorrect answer will get minus 1 point.


            if(correctAnswerIndex==position){

                    //update user score and user question index and save into database.
                    user.score+=2

                    user.currentQuestionIndex+=1

                    activity.saveCurrentUserInfoToSqlite()

                    var intent= Intent(context,CorrectActivity::class.java)

                    intent.putExtra(MainActivity.QUESTION_ITEM,item)
                    intent.putExtra(MainActivity.USER,user)

                    context.startActivity(intent)


            }else{

                //Only deduct point when currentUser has score greater than 0
                if((user.score-1)>=0) {
                    user.score -= 1
                }


                //Change the color when the currentUser click
                var gd= it.background as GradientDrawable
                gd.setColor(context.resources.getColor(R.color.accent))
                it.answerTextView.setTextColor(context.resources.getColor(R.color.icons))


                //Wrong answer would display red box so the currentUser knew the answer was incorrect.
                GlobalScope.launch (Dispatchers.Main){

                    delay(500)

                    //Change back to orignal color
                    gd.setColor(context.resources.getColor(R.color.primary_light))
                    it.answerTextView.setTextColor(context.resources.getColor(R.color.primary_text))

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


