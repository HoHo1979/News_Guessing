package com.newscorps.newsguessing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_article.*

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)


        var url = intent.getStringExtra(CorrectActivity.STORY_URL)

        webView?.let {
            it.settings.javaScriptEnabled = true
            it.loadUrl(url)
        }


    }
}
