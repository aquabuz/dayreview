package com.example.dayreview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView webView;

    private Dialog      m_Dialog                = null;
    private long        backKeyPressedTime      = 0;
    private Toast       m_Toast;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_Dialog        = new Dialog(this);
        m_Dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        m_Dialog.setContentView (R.layout.custom_dialog);
        m_Dialog.getWindow().setBackgroundDrawableResource (android.R.color.transparent);

        webView = (WebView)findViewById(R.id.dayreivew);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                view.loadUrl(uri.toString());
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                view.loadUrl(uri.toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(m_Dialog != null) {
                            m_Dialog.show();
                        }
                    }
                });

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageCommitVisible(WebView view, final String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(m_Dialog != null) {
                                m_Dialog.setCancelable(true);
                                m_Dialog.dismiss();
                            }
                        } catch (Throwable e) {}
                    }
                });

                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(m_Dialog != null) {
                                m_Dialog.setCancelable(true);
                                m_Dialog.dismiss();
                            }
                        } catch (Throwable e) {}
                    }
                });
                super.onReceivedError(view, request, error);
            }
        });


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("https://dayreview.kr/");
            }
        });

    }


    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                m_Toast.cancel();
                super.onBackPressed();
            }
        }
    }


    public void showGuide() {
        m_Toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        m_Toast.show();
    }


}
