package tech.honeysharma.techbmechat.Blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import tech.honeysharma.techbmechat.R;

public class DetailedNewsActivity extends AppCompatActivity {

   WebView webView;
    Intent intent;

    String  newsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_news);


        webView = findViewById(R.id.webView);

        intent = getIntent();


        newsUrl = intent.getStringExtra("newsUrl");

        webView.loadUrl(newsUrl);


    }
}
