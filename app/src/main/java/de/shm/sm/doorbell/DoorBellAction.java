package de.shm.sm.doorbell;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class DoorBellAction extends AppCompatActivity {

    NetworkImageView mImageView;
    private Ringtone currentRingtone;
    @Override
    protected void onPause() {
        super.onPause();
        currentRingtone.stop();
        //if(!isFinishing()) finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        currentRingtone.play();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_bell_action);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ActionBar actionBar  = getSupportActionBar();
        actionBar.hide();

        View dcoreView = getWindow().getDecorView();
        dcoreView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_IMMERSIVE
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();
        InetAddress doorBellAddress = (InetAddress) intent.getSerializableExtra(DoorBellListener.DOOR_BELL_IP);
        //Find the reference to the ImageView
        mImageView = (NetworkImageView) findViewById(R.id.capturedImage);

        ImageLoader mImageLoader = MySingleton.getInstance(this.getApplicationContext()).getImageLoader();
        mImageView.setImageUrl("http:/" + doorBellAddress.toString() + "/image.jpg", mImageLoader);
        Uri currentRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this.getApplicationContext(),RingtoneManager.TYPE_ALARM);
        currentRingtone = RingtoneManager.getRingtone(this,currentRingtoneUri);
        currentRingtone.play();
        final ImageButton btnCancel = (ImageButton) findViewById(R.id.btnDoorBellCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRingtone.stop();
            }
        });
        final ImageButton btnOk = (ImageButton) findViewById(R.id.btnDoorBellOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRingtone.stop();
                finish();
            }
        });
    }
}
