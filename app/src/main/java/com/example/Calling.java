package com.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mycallingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Calling extends AppCompatActivity implements CallAdapter.CallClick {
    Button call;
    GoogleSignInAccount account;
    RecyclerView recyclerView;
    List<User> list;

    CallAdapter adapter;

    FCMService fcmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);


        recyclerView=findViewById(R.id.CallList);

        account = GoogleSignIn.getLastSignedInAccount(this);

        fcmService=Common.getFCMService();


        list=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                     User user=dataSnapshot.getValue(User.class);
                     if(!account.getId().equals(user.UID)){
                         list.add(user);
                     }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(Calling.this));
                adapter=new CallAdapter(Calling.this,list,Calling.this);
                recyclerView.setAdapter(adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void PersonToCall(int position) {
        Toast.makeText(this, list.get(position).emailId, Toast.LENGTH_SHORT).show();

        String myName=account.getDisplayName();
        String mTargetName=list.get(position).Name;
        String channelName;
        channelName = myName.compareTo(mTargetName) < 0 ? myName + mTargetName : mTargetName + myName;
        Intent intent = new Intent(Calling.this, VideoActivity.class);


      /*  System.out.println("UID"+account.getId());
        intent.putExtra("UID",String.valueOf(account.getId()));
        intent.putExtra("channelName",channelName);
        startActivity(intent);*/

        CallThePerson(list.get(position).UID);


    }

    private void CallThePerson(String id) {
        final String[] token = new String[1];
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference1.orderByChild("UID").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    token[0] =user.token;

                    System.out.println("User"+dataSnapshot.getValue());

                    Notification notification=new Notification("Incoming  Call",account.getDisplayName());

                    System.out.println("token"+user.token);


                    String myName=account.getDisplayName();
                    String mTargetName=user.Name;
                    String channelName;
                    channelName=account.getId();
                    Data data=new Data("Incoming call",account.getDisplayName(),channelName,"valid");

                    Sender content=new Sender(token[0],data);
                    fcmService.sendMessage(content)
                            .enqueue(new Callback<FCMResponse>() {
                                @Override
                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                    assert response.body() != null;

                                    if(response.body().success==1) {

                                       /* Intent intent=new Intent(Calling.this,VideoActivity.class);

                                        System.out.println(channelName);
                                        intent.putExtra("ChannelName",channelName);
                                        intent.putExtra("typeOfCall","OutgoingCall");
                                        intent.putExtra("Token",user.token);
                                        startActivity(intent);
                                        finish();*/

                                    }
                                        else{
                                            Toast.makeText(Calling.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                @Override
                                public void onFailure(Call<FCMResponse> call, Throwable t) {

                                    Toast.makeText(Calling.this, "User Offline", Toast.LENGTH_SHORT).show();

                                }
                            });




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}