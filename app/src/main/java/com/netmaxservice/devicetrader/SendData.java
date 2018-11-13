package com.netmaxservice.devicetrader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.netmaxservice.devicetrader.helpers.Fader;
import com.netmaxservice.devicetrader.helpers.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendData extends AppCompatActivity {

    private ProgressDialog progress;
    private static Context mContext;
    private static final int MSG_SHOW_TOAST = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    Button mFrontPicButton, mBackPicButton;
    boolean frontFileExist;
    boolean backFileExist;
    boolean nameEmpty;
    boolean mailEmpty;
    boolean zipEmpty;
    EditText mNameField;
    EditText mMailField;
    EditText mZipField;
    private AdView mAdView;

    @Override
    public void onResume() {
        super.onResume();

        // hide keyboard on resume (created to remove focus from edit text in this case)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        mContext = this;

        mNameField = findViewById(R.id.editTextName);
        mMailField = findViewById(R.id.editTextMail);
        mZipField = findViewById(R.id.editTextZip);

        // SCROLL VIEW HACK
        ScrollView view = findViewById(R.id.scrollView);
        view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                v.requestFocusFromTouch();
                return false;
            }
        });

        // data from WebInterface class
        Intent intent = getIntent();
        String stringFromWeb = intent.getStringExtra("device");

        // deviceInfo string to List
        List<String> list = new ArrayList<String>(Arrays.asList(stringFromWeb.split(",")));

          /*  for (int i = 0; i < list.size(); i++) {
                Log.v("list element: ", "" + list.get(i));
            } */

        // set model of a submitted device
        TextView mDevice = findViewById(R.id.textViewDevice);
        mDevice.setText("GET " + list.get(4) + " FOR YOUR:\n" + list.get(0));
        Fader.runAlphaAnimation(this, mDevice.getId());


        // attach front photo button
        mFrontPicButton = findViewById(R.id.buttonFrontPic);
        mFrontPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code for front pic
                clearFocus();

                saveFullImage("front.jpg");
            }
        });

        // attach back photo button
        mBackPicButton = findViewById(R.id.buttonBackPic);
        mBackPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code for back pic
                clearFocus();

                saveFullImage("back.jpg");
            }
        });

        // start over button
        final Button mStartOverButton = findViewById(R.id.buttonStartOver);
        mStartOverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus();

                Intent startOverIntent = new Intent(SendData.this, TradeWeb.class);
                startActivity(startOverIntent);
            }
        });

        // send request button
        final Button mSendRequestButton = findViewById(R.id.buttonSendRequest);
        mSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code for send request button
                clearFocus();

                if (mNameField.getText().toString().matches("")) {
                    nameEmpty = true;
                }

                if (mMailField.getText().toString().matches("")) {
                    mailEmpty = true;
                }

                if (mZipField.getText().toString().matches("")) {
                    zipEmpty = true;
                }

                File fileFrontPic = new File(Environment.getExternalStorageDirectory().getPath() + "/front.jpg");
                if (fileFrontPic.exists()) {
                    frontFileExist = true;
                }

                File fileBackPic = new File(Environment.getExternalStorageDirectory().getPath() + "/front.jpg");
                if (fileBackPic.exists()) {
                    backFileExist = true;
                }

                // if form is filled and photos attached, then process request
                if (frontFileExist && backFileExist && !nameEmpty && !mailEmpty && !zipEmpty) {
                    download();
                    new Post().execute(mNameField.getText().toString(), mMailField.getText().toString(),
                            mZipField.getText().toString(), imageToString("front.jpg"), imageToString("back.jpg"));
                } else {
                    Toast.makeText(SendData.this, R.string.fill_forms_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // admob
        mAdView = findViewById(R.id.adView);
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
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    // decode image to String
    public String imageToString(String inputFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/" + inputFile);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] image = stream.toByteArray();
        String imageString = Base64.encodeToString(image, 0);
        return imageString;
    }

    // handling toast after "send request" button
    private static Handler messageHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_SHOW_TOAST) {
                String message = (String) msg.obj;
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        }
    };

    // toast is called after form submission
    private static void displayMessage() {
        Message msg = new Message();
        msg.what = MSG_SHOW_TOAST;
        msg.obj = mContext.getString(R.string.thank_you_message);
        messageHandler.sendMessage(msg);
    }


    // can access only static variables/objects inside static method
    public static void dataSent() {
        displayMessage();
        Intent goHome = new Intent(mContext, MainActivity.class);
        mContext.startActivity(goHome);
    }


    // take photos
    private void saveFullImage(String type) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(getFilesDir().getPath(), type);
        Uri outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        if (type.equals("front.jpg")) {
            mFrontPicButton.setText(R.string.attached_text);
            //    mFrontPicButton.setEnabled(false);
        } else if (type.equals("back.jpg")) {
            mBackPicButton.setText(R.string.attached_text);
            //    mBackPicButton.setEnabled(false);
        }
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_IMAGE_CAPTURE) && (resultCode == Activity.RESULT_OK)) {
            // Check if the result includes a thumbnail Bitmap

        }
    }*/


    // dialog is fired up after send request is called
    public void download() {
        progress = new ProgressDialog(this);
        progress.setMessage("Sending your request...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        //   progress.setProgress(0);
        progress.show();

        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while (jumpTime < totalProgressTime) {
                    try {
                        sleep(300);
                        jumpTime += 5;
                        progress.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public void clearFocus() {
        mNameField.clearFocus();
        mMailField.clearFocus();
        mZipField.clearFocus();
    }


}

// calling post in separate thread
class Post extends AsyncTask<String, Integer, Long> {

    @Override
    protected Long doInBackground(String... arg0) {

        String nameField = arg0[0];
        String mailField = arg0[1];
        String zipField = arg0[2];
        String frontPicField = arg0[3];
        String backPicField = arg0[4];

        HttpRequest request = HttpRequest.post("http://netmaxservice.com/devicetrader/post.php");
        request.part("name", nameField);
        request.part("mail", mailField);
        request.part("zip", zipField);
        request.part("front", frontPicField);
        request.part("back", backPicField);
        if (request.ok())
            SendData.dataSent();
        return null;
    }

}