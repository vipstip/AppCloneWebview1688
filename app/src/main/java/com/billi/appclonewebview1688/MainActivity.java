package com.billi.appclonewebview1688;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    SearchView searchView;
    String url;
    private String originalText;
    private String translatedText;
    private boolean connected;
    Translate translate;
    ImageButton showBangSL;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webviewSearch);
        showBangSL = findViewById(R.id.showBangSL);
//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        webView  = new WebView(this);
        getTranslateService();
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
//        createBottomNavigation();
//        setContentView(webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("https://m.1688.com/offer/")){
                    webView.loadUrl (
                            "javascript: ( function() {var spanTag1 = document.createElement('span');" +
                                    "spanTag1.innerHTML = '<span style=\"font-size: 50pt; border: 0px; padding: 0px; margin: 0px;\">text</span>';" +
                                    "var element = document.getElementById(\"widget-wap-detail-common-price\");" +
                                    "element.appendChild(spanTag1); }) ()");

                    webView.loadUrl (
                            "javascript: ( function() { var showBangSL = document.getElementsByClassName(\"takla-wap-b2b-skuselector-component\")[0].style.display = \"block\";" +
                                    "var elements = document.getElementsByClassName(\"component-sku-selector-container\")[0].style.bottom=0; }) ()");
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url){

                return false;
            }
        });

        showBangSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl (
                        "javascript: ( function() { var showBangSL = document.getElementsByClassName(\"takla-wap-b2b-skuselector-component\")[0].style.display = \"block\";" +
                                "var elements = document.getElementsByClassName(\"component-sku-selector-container\")[0].style.bottom=0; }) ()");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Here...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                originalText = query;
                translate();
                String query1 = null;
                try {
                    query1 = URLEncoder.encode(translatedText, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url = "https://m.1688.com/offer_search/-6D7033.html?keywords="+query1;
//                url = "https://www.google.com/";

                Log.e("FinishTranslate",translatedText);
                webView.loadUrl(url);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }


    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate() {

        //Get input text to be translated:
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("zh-CN"), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
    }

    public void createBottomNavigation(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_recents) {
                    Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}
