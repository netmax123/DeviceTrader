package com.netmaxservice.devicetrader;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebInterface {
    Context mContext;

    // Instantiate the interface and set the context
    WebInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void sendInfo(String deviceInfo) {
        // send device info from web to activity
        Intent sendDataPage = new Intent(mContext, SendData.class);
        sendDataPage.putExtra("device", deviceInfo);
        mContext.startActivity(sendDataPage);
    }

    @JavascriptInterface
    public void formToast() {
        Toast.makeText(mContext, "Please complete the form!", Toast.LENGTH_SHORT).show();
    }
}