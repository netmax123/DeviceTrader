package com.netmaxservice.devicetrader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.netmaxservice.devicetrader.helpers.Fader;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button to start activity with WebView
        Button tradeButton = findViewById(R.id.buttonTrade);
        tradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetIsAvailable()) {
                    Intent goToTrade = new Intent(MainActivity.this, TradeWeb.class);
                    startActivity(goToTrade);
                } else {
                    Toast.makeText(MainActivity.this,
                            R.string.internet_down_message,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // button for rate us
        Button rateButton = findViewById(R.id.buttonRate);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRateDialog(MainActivity.this);
            }
        });

        // button for about us
        Button aboutButton = findViewById(R.id.buttonAbout);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderAboutDialog = new AlertDialog.Builder(MainActivity.this);
                builderAboutDialog.setTitle("ABOUT");
                builderAboutDialog.setMessage("Device Trader\nVersion " + BuildConfig.VERSION_NAME +getString(R.string.credits_message));
                builderAboutDialog.setCancelable(false);
                builderAboutDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog aboutAlert = builderAboutDialog.create();
                aboutAlert.show();


                TextView textView = (aboutAlert.findViewById(android.R.id.message));
                textView.setTextColor(Color.GRAY);
                textView.setTextSize(14);
            }
        });

        TextView deviceText = findViewById(R.id.deviceInfo);
        deviceText.setText(getString(R.string.current_device) + getDeviceName());
        Fader.runAlphaAnimation(this, deviceText.getId());

        // admob
        mAdView = findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        mAdView.loadAd(request);

    }

    // Called when leaving the activity
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    // Called when returning to the activity
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    // Called before the activity is destroyed
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    // todo: do this in background

    // check if internet is available
    public Boolean internetIsAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.netmaxservice.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            if (reachable) {
                System.out.println("Internet access");
                return reachable;
            } else {
                System.out.println("No Internet access");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    // dialog for rate us
    public static void showRateDialog(final Context context) {
        AlertDialog.Builder builderAboutDialog = new AlertDialog.Builder(context)
                .setTitle("RATE US!")
                .setMessage("Please rate Device Trader on Google Play!")
                .setPositiveButton("RATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context != null) {
                            String link = "market://details?id=";
                            try {
                                // play market available
                                context.getPackageManager()
                                        .getPackageInfo("com.android.vending", 0);
                                // not available
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                                // should use browser
                                link = "https://play.google.com/store/apps/details?id=";
                            }
                            // starts external action
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(link + context.getPackageName())));
                        }
                    }
                })
                .setNegativeButton("CANCEL", null);
        builderAboutDialog.show();
    }


    // get device name
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    // capitalize
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}