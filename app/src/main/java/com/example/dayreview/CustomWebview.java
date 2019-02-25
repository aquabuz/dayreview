package com.example.dayreview;

import android.content.Context;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CustomWebview extends WebView {


    public CustomWebview(Context context) {
        super(context);
    }

    /*
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.e("debug", "onJsAlert");
        return super.onJsAlert(view, url, message, result);
    }
    */


}
