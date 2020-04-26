package com.example.socialminibtd.View.Activity.NewsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.socialminibtd.Adapter.NewsDayAdapter;
import com.example.socialminibtd.Model.ModelNews;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.RecyclerLongPressClickListener;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView recyc_news_day;
    private ArrayList<ModelNews> newsArrayList;
    private NewsDayAdapter dayAdapter;
    private WebView webview_newsday;
    private ImageView img_back_newsday;
    private EditText search_url_newsday;
    private ProgressBar progress_newsday;
    private String Url_default = "https://www.google.com.vn/?hl=vi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);
        setContentView(R.layout.activity_news);

        recyc_news_day = findViewById(R.id.recyc_news_day);
        webview_newsday = findViewById(R.id.webview_newsday);
        search_url_newsday = findViewById(R.id.search_url_newsday);
        progress_newsday = findViewById(R.id.progress_newsday);
        img_back_newsday = findViewById(R.id.img_back_newsday);


        img_back_newsday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        onShowListNewsDay();

        onSettingWebView();

        if (TextUtils.isEmpty(search_url_newsday.getText().toString().trim())) {

            webview_newsday.loadUrl(Url_default);

        } else {

            webview_newsday.loadUrl(search_url_newsday.getText().toString().trim());

        }


        onLoadWebView();


    }

    private void onSettingWebView() {

        WebSettings webSettings = webview_newsday.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

    }


    private void onShowListNewsDay() {

        recyc_news_day.setHasFixedSize(true);

//        LinearLayoutManager layoutManager =
//                new GridLayoutManager(NewsActivity.this, 2, GridLayoutManager.HORIZONTAL, false);


        LinearLayoutManager layoutManager =
                new GridLayoutManager(NewsActivity.this, 1, GridLayoutManager.HORIZONTAL, false);

        recyc_news_day.setLayoutManager(layoutManager);

        newsArrayList = new ArrayList<>();

        String[] NameNews = {"Evolutions Corona", "Dan Tri", "VNExpress", "Kenh 14", "Giai Tri"};

        String[] Url_News = {"https://corona.kompa.ai/", "https://dantri.com.vn/", "https://vnexpress.net/", "https://kenh14.vn/"
                , "https://news.zing.vn/giai-tri.html"};

        int[] Image_News = {R.drawable.ic_newday, R.drawable.ic_newday
                , R.drawable.ic_newday, R.drawable.ic_newday, R.drawable.ic_newday};


        for (int i = 0; i < NameNews.length; i++) {

            newsArrayList.add(new ModelNews(NameNews[i], Url_News[i], Image_News[i]));

        }

        dayAdapter = new NewsDayAdapter(newsArrayList, NewsActivity.this);

        recyc_news_day.setAdapter(dayAdapter);

    }


    private void onLoadWebView() {

        if (newsArrayList != null) {

            recyc_news_day.addOnItemTouchListener(new RecyclerLongPressClickListener(NewsActivity.this, recyc_news_day, new RecyclerLongPressClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    webview_newsday.loadUrl(newsArrayList.get(position).getUrl_news());
                    webview_newsday.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);

                            Log.d(Const.LOG_DAT, "WebView NewsDay onPageStarted:" + url);
                            progress_newsday.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);

                            Log.d(Const.LOG_DAT, "WebView NewsDay onPageFinished:" + url);
                            progress_newsday.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            super.onReceivedError(view, request, error);

                            Log.d(Const.LOG_DAT, "WebView NewsDay onReceivedError:" + error.toString());
                            progress_newsday.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                            super.onReceivedHttpError(view, request, errorResponse);

                            Log.d(Const.LOG_DAT, "WebView NewsDay onReceivedHttpError:" + errorResponse.toString());
                            progress_newsday.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                            super.onReceivedSslError(view, handler, error);

                            Log.d(Const.LOG_DAT, "WebView NewsDay onReceivedSslError:" + error.toString());
                            progress_newsday.setVisibility(View.INVISIBLE);

                        }
                    });

                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));

        }

    }

    @Override
    public void onBackPressed() {

        if (webview_newsday.canGoBack()) {

            webview_newsday.goBack();

            Log.d(Const.LOG_DAT, "WebView NewsDay" + webview_newsday.getUrl());

        } else {

            super.onBackPressed();

        }
    }
}
