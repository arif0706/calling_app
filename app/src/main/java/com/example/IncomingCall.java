package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycallingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

public class IncomingCall extends AppCompatActivity {


    MediaPlayer mediaPlayer;
    TextView IncomingName;
    Button accept;
    RtcEngine rtcEngine;
    private RtmClient rtmClient;
    private List<RtmClientListener> mListenerList = new ArrayList<>();

    private String APP_ID="08ad7e4349e44b1791d9815fe4d45701";

    FrameLayout cameraPreview;
    SurfaceView localView;
    IRtcEngineEventHandler handler;

    GoogleSignInAccount account;

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);




        cameraPreview=findViewById(R.id.LocalVideoViewContainer);
        Intent intent=getIntent();
        String Name=intent.getStringExtra("Name");
        String ChannelName=intent.getStringExtra("ChannelName");
        String valid=intent.getStringExtra("valid");
        account = GoogleSignIn.getLastSignedInAccount(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.cradles_medium);
        mediaPlayer.setLooping(true);
        accept = findViewById(R.id.accept);
        IncomingName = findViewById(R.id.IncomingName);

        if(valid.equals("invalid")){

            System.out.println("Call disconnected!");
            Intent intent1=new Intent(this,Calling.class);
            startActivity(intent1);
            finish();
        }
        else {


            Log.d("No of cameras", Camera.getNumberOfCameras() + "");
            for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
                Camera.CameraInfo camInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(camNo, camInfo);

                if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                    mCamera = Camera.open(camNo);
                }
            }
            if (mCamera == null) {
                // no front-facing camera, use the first back-facing camera instead.
                // you may instead wish to inform the user of an error here...
                mCamera = Camera.open();
            }
            mCamera.setDisplayOrientation(90);
            mPreview = new CameraPreview(this, mCamera);
            cameraPreview.addView(mPreview);

            Toast.makeText(this, ChannelName, Toast.LENGTH_SHORT).show();
            IncomingName.setText(Name);
            mediaPlayer.start();
        }




        try {
            rtmClient=RtmClient.createInstance(this, APP_ID, new RtmClientListener() {
                @Override
                public void onConnectionStateChanged(int state, int reason) {
                    for (RtmClientListener listener : mListenerList) {
                        listener.onConnectionStateChanged(state, reason);
                    }

                }

                @Override
                public void onMessageReceived(RtmMessage rtmMessage, String s) {
                }



                @Override
                public void onTokenExpired() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        rtmClient.login(null,account.getId(),new io.agora.rtm.ResultCallback<Void>(){

            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent intent1=new Intent(IncomingCall.this,VideoActivity.class);
                intent1.putExtra("typeOfCall","IncomingCall");
                intent1.putExtra("ChannelName",ChannelName);
                startActivity(intent1);
                finish();



            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        finish();
    }
}