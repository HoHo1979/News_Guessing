package com.newscorps.newsguessing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_article.*
import org.jetbrains.anko.startActivity

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        var url = intent.getStringExtra(CorrectActivity.STORY_URL)


        webView.let {
            it.settings.javaScriptEnabled = true
            it.loadUrl(url)
        }

    }

    override fun onBackPressed() {
       startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}
