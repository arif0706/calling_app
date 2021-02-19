package com.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mycallingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.nio.channels.Channel;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    RtcEngine rtcEngine;
    String APP_ID="08ad7e4349e44b1791d9815fe4d45701";
    IRtcEngineEventHandler handler;

    GoogleSignInAccount account;
    String ChannelName;

    FrameLayout localContainer;
    SurfaceView localView;

    RelativeLayout remoteContainer;
    SurfaceView remoteView;

    boolean joined=true;

    String token;


    String typeOfCall;

    private User user;

    ImageView callBtn,muteBtn,switchCameraBtn;


    FCMService fcmService;
    double uid;

    boolean CallEnd,mute;


    private static final String[] REQUESTED_PERMISSIONS={
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        account= GoogleSignIn.getLastSignedInAccount(this);
        Intent intent=getIntent();

        typeOfCall=intent.getStringExtra("typeOfCall");
        ChannelName = intent.getStringExtra("ChannelName");

        token=intent.getStringExtra("Token");

        System.out.println("TOKEN"+token);

        System.out.println("ChannelName in VideoActivity"+ChannelName);

        fcmService=Common.getFCMService();


        localContainer=findViewById(R.id.local_video_view_container);
        remoteContainer=findViewById(R.id.remote_video_view_container);

        callBtn=findViewById(R.id.btn_call);
        muteBtn=findViewById(R.id.btn_mute);
        switchCameraBtn=findViewById(R.id.btn_switch_camera);


        if(checkSelfPermission(REQUESTED_PERMISSIONS[0],22)&&checkSelfPermission(REQUESTED_PERMISSIONS[1],22)
        && checkSelfPermission(REQUESTED_PERMISSIONS[2],22)){

            initEngineAndJoinChannel();

        }



        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CallEnd){
                    startCall();
                    CallEnd=false;
                }
                else{
                    endCall();
                    CallEnd=true;
                }

            }
        });
        muteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute=!mute;
                rtcEngine.muteLocalAudioStream(mute);


            }
        });
        switchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        rtcEngine.switchCamera();
            }
        });




        handler=new IRtcEngineEventHandler() {
            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                super.onJoinChannelSuccess(channel, uid, elapsed);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("agora  Joined successfully"+(uid&0xFFFFFFFFL));

                        setUpRemoteVideo(uid);

                        joined=false;
                    }
                });
            }

            @Override
            public void onUserOffline(int uid, int reason) {
                super.onUserOffline(uid, reason);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        endCall();
                    }
                });
            }
            @Override
            public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("agora","First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                        setUpRemoteVideo(uid);
                    }
                });
            }

            @Override
            public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
                super.onRemoteVideoStateChanged(uid, state, reason, elapsed);

                System.out.println("state"+state);
                if(state== Constants.REMOTE_VIDEO_STATE_STARTING){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("agora remote video started");
                            setUpRemoteVideo(uid);
                        }
                    });
                }
            }
        };


    }

    private void initEngineAndJoinChannel() {

        System.out.println("initEngineJOinChannel");
        InitializeEngine();

        setUpVideoConfig();

        setUpLocalVideo();

        joinChannel();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 22) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            initEngineAndJoinChannel();
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,REQUESTED_PERMISSIONS,requestCode);
            return false;
        }

        return true;
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();


                Data data=new Data("","","","invalid");
                Sender sender=new Sender(token,data);
                fcmService.sendMessage(sender)
                        .enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                Intent intent = new Intent(VideoActivity.this, Calling.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });


            }



    void removeLocalVideo(){
        if(localView!=null){
            localContainer.removeView(localView);
        }
        localView=null;
    }
    private void leaveChannel(){
        rtcEngine.leaveChannel();

    }

    private void startCall() {
        setUpLocalVideo();
        joinChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!CallEnd){
            leaveChannel();
        }
        RtcEngine.destroy();
    }

    private void removeRemoteVideo() {

        if(remoteView!=null){
            remoteContainer.removeView(remoteView);
        }
        remoteView=null;

    }

    private void setUpRemoteVideo(int uid) {
        remoteView=RtcEngine.CreateRendererView(getBaseContext());
        remoteContainer.addView(remoteView);
        rtcEngine.enableVideo();
        rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView,VideoCanvas.RENDER_MODE_HIDDEN,uid));
        remoteView.setTag(uid);



    }

    void InitializeEngine()
    {
        try {
            System.out.println("Initialize engine");
            rtcEngine=RtcEngine.create(getBaseContext(),APP_ID,handler);
        } catch (Exception e) {
            System.out.println("InitializeEngine"+e.getMessage());
        }
    }
    private void setUpVideoConfig(){

        System.out.println("setUpVideoConfig");
        rtcEngine.enableVideo();

        rtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                        VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                        VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                ));
    }

    private void setUpLocalVideo(){
        System.out.println("setUpLocalVideo");
        rtcEngine.enableVideo();
        localView=RtcEngine.CreateRendererView(getBaseContext());
        localView.setZOrderMediaOverlay(true);
        localContainer.addView(localView);

        VideoCanvas videoCanvas=new VideoCanvas(localView,VideoCanvas.RENDER_MODE_HIDDEN,0);
        rtcEngine.setupLocalVideo(videoCanvas);
    }

    private void joinChannel(){
        System.out.println("JOin channel");
        rtcEngine.joinChannel(null,ChannelName,"",0);
    }
}