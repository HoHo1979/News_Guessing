package com.newscorps.newsguessing

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.newscorps.newsguessing.entity.Item
import com.newscorps.newsguessing.entity.User

import kotlinx.android.synthetic.main.activity_correct.*
import kotlinx.android.synthetic.main.content_correct.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CorrectActivity : AppCompatActivity(),AnkoLogger {

    companion object{
        var STORY_URL="SOTRY_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct)
        setSupportActionBar(toolbar)

        var questionItem=intent.getParcelableExtra<Item>(MainActivity.QUESTION_ITEM)
        var user = intent.getParcelableExtra<User>(MainActivity.USER)

        scoreTV.text="You score: "+user.score.toString()

        var articleUrl =""

        if (questionItem != null) {


            Glide.with(this)
                .load(questionItem.imageUrl)
                .apply(RequestOptions().override(120, 200))
                .into(itemImageView)


            standFirstTextView.text = questionItem.standFirst

            articleUrl=questionItem.storyUrl

        }


        //Link to the Article
        articleButton.setOnClickListener {

            var intent=Intent(this,ArticleActivity::class.java)
            intent.putExtra(CorrectActivity.STORY_URL,articleUrl)
            startActivity(intent)

        }

        nextButton.setOnClickListener {

          //  user.currentQuestionIndex+=1

            QuestionCounter.counter+=1
            finish()


        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}
