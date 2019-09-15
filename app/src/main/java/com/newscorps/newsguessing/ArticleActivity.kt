package com.newscorps.newsguessing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)


        var url = intent.getStringExtra(CorrectActivity.STORY_URL)



        webView.let {
                it.settings.javaScriptEnabled = true
                it.webViewClient = WebViewClient()
                it.loadUrl(url)
        }




    }

    override fun onBackPressed() {
       startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}
