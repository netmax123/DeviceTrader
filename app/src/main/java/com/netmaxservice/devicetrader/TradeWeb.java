package com.netmaxservice.devicetrader;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TradeWeb extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_web);

        WebView webTrade = (WebView) findViewById(R.id.webViewTrade);
        webTrade.clearCache(true);
        webTrade.setBackgroundColor(Color.TRANSPARENT);
        webTrade.loadUrl("http://www.netmaxservice.com/devicetrader/app.html");

        // enable JS in WebView
        WebSettings webSettings = webTrade.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webTrade.addJavascriptInterface(new WebInterface(this), "Android");

        // admob
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        mAdView.loadAd(request);

    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}

