package com.example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mycallingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID="MyCallingApp";
    public static final String CHANNEL_NAME="MyCallingApp";
    public static final String CHANNEL_DESC="MyCallingApp notification";

    Button signIn;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    GoogleSignInAccount account;
    private RtmClient rtmClient;
    private List<RtmClientListener> mListenerList = new ArrayList<>();

    private String APP_ID="08ad7e4349e44b1791d9815fe4d45701";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signIn=findViewById(R.id.signIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1034409066057-sgq4oua7q9jajro5t8iv4915p3jccut1.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[0]);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
       // mAuth = FirebaseAuth.getInstance();


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);


            }
        });
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

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);




            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final User[] user = new User[1];
            if(completedTask.isSuccessful()) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        String token=task.getResult().getToken();
                       user[0] =new User(account.getDisplayName(),account.getEmail(),account.getId(),token);
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(account.getId()).setValue(user[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });



              rtmClient.login(null,account.getId(),new io.agora.rtm.ResultCallback<Void>(){

                  @Override
                  public void onSuccess(Void aVoid) {
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Intent intent = new Intent(MainActivity.this, Calling.class);
                              startActivity(intent);
                              finish();
                          }
                      });


                  }

                  @Override
                  public void onFailure(ErrorInfo errorInfo) {

                      System.out.println("Error info"+errorInfo);

                  }
              });


            }


        } catch (ApiException e) {

            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser user=mAuth.getCurrentUser();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            rtmClient.login(null,account.getId(),new io.agora.rtm.ResultCallback<Void>(){


                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(MainActivity.this, Calling.class);
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });

        }
    }
}