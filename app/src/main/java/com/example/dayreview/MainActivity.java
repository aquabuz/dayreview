package com.example.dayreview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
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
    private AlertDialog.Builder m_AlertDialog   = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_Dialog        = new Dialog(this);
        m_Dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        m_Dialog.setContentView (R.layout.custom_dialog);
        m_Dialog.getWindow().setBackgroundDrawableResource (android.R.color.transparent);
        m_Dialog.setCancelable(false);
        m_Dialog.show();

        webView = (WebView)findViewById(R.id.dayreivew);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClientClass());
        webView.setWebChromeClient(new WebViewChromeClientClass());


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("https://dayreview.kr/");
            }
        });

    }

    private class WebViewChromeClientClass extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.e("debug", "onJsAlert");

            final JsResult finalRes = result;

            m_AlertDialog = new AlertDialog.Builder(view.getContext(), R.style.MyDialogStyle);
            m_AlertDialog.setMessage(message + "                    ");
            m_AlertDialog.setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finalRes.confirm();
                                }
                            });
            m_AlertDialog.create();
            m_AlertDialog.setCancelable(false);
            m_AlertDialog.show();
            return true;
        }


        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            Log.e("debug", "onShowFileChooser");
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            Log.e("debug", "onJsConfirm");
            m_AlertDialog = new AlertDialog.Builder(view.getContext(), R.style.MyDialogStyle);
            m_AlertDialog.setMessage(message + "                    ");
            m_AlertDialog.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            });
            m_AlertDialog.setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            });
            m_AlertDialog.create();
            m_AlertDialog.setCancelable(false);
            m_AlertDialog.show();

            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            Log.e("debug", "onJsPrompt");
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }


    private class WebViewClientClass extends WebViewClient {
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
            Log.e("debug","shouldOverrideUrlLoading=" + uri.toString());
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
            Log.e("debug","onReceivedError");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(m_Dialog != null) {
                            m_Dialog.setCancelable(true);
                            m_Dialog.dismiss();
                        }
                    } catch (Throwable e) {Log.e("debug","onReceivedError::Throwable");}
                }
            });
            super.onReceivedError(view, request, error);
        }
    }


    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                m_Toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
                m_Toast.show();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                m_Toast.cancel();
                super.onBackPressed();
            }
        }
    }

}
