package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.Constants;
import com.istiaksaif.medops.Utils.PreferenceManager;
import com.istiaksaif.medops.network.ApiClient;
import com.istiaksaif.medops.network.ApiService;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutGoingActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String receiver_token;
    private TextView Name;
    private ImageView image,layoutBgImg,endCall,micOnIcon,videoOnIcon;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Intent intent;
    private String doctorId,meetingType;
    private String inviterToken = null;
    private String meetingRoom = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_going);

        preferenceManager =new PreferenceManager(getApplicationContext());

        intent = getIntent();
        doctorId = intent.getStringExtra("userId");
        meetingType = intent.getStringExtra("type");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        layoutBgImg = findViewById(R.id.layoutBgImg);
        image = findViewById(R.id.profileimage);
        Name = findViewById(R.id.callernamed);
        endCall = findViewById(R.id.endCall);
        micOnIcon = findViewById(R.id.micOnIcon);
        videoOnIcon = findViewById(R.id.videoOnIcon);

        endCall.setOnClickListener(view -> {
            databaseReference.child("users").child(doctorId).child("token").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    receiver_token = snapshot.getValue(String.class);
                    cancelInvitation(receiver_token);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        GetDataFromFirebase();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    inviterToken = task.getResult();
                    sendCallInvitation();
                }
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken){
        try {
            JSONArray token = new JSONArray();
            token.put(receiverToken);
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);
            data.put("userId",uid);
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = Name.getText().toString();
            data.put(Constants.REMOTE_MSG_MEETING_ROOM,meetingRoom);
            jsonObject.put(Constants.REMOTE_MSG_DATA,data);
            jsonObject.put(Constants.REMOTE_MSG_REG_IDS,token);

            sendRemoteMessage(jsonObject.toString(),Constants.REMOTE_MSG_INVITATION);

        }catch (Exception e){

        }
    }
    private void sendRemoteMessage(String remoteMessageBody,String type){
        ApiClient.getRetrofit().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeader(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutGoingActivity.this,"Invitation Successfully",Toast.LENGTH_SHORT).show();
                    }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutGoingActivity.this,"Invitation Cancelled",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(OutGoingActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(OutGoingActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void sendCallInvitation() {
        databaseReference.child("users").child(doctorId).child("token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiver_token = snapshot.getValue(String.class);
                initiateMeeting(meetingType, receiver_token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cancelInvitation(String receiverToken){
        try {
            JSONArray token = new JSONArray();
            token.put(receiverToken);
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCEL);

            jsonObject.put(Constants.REMOTE_MSG_DATA,data);
            jsonObject.put(Constants.REMOTE_MSG_REG_IDS,token);

            sendRemoteMessage(jsonObject.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE);

        }catch (Exception e){

        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                    try {
                        URL serverUrl = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions conferenceOptions =
                                new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(serverUrl).setWelcomePageEnabled(false)
                                .setRoom(meetingRoom)
                                .build();
                        JitsiMeetActivity.launch(OutGoingActivity.this,conferenceOptions);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(OutGoingActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    Toast.makeText(context,"Invitation Reject",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
            broadcastReceiver,new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                broadcastReceiver);
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("users").child(doctorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String k = dataSnapshot.child("key").getValue().toString();
                databaseReference.child("usersData").child(k).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Name.setText(snapshot.child("name").getValue().toString());
                            String Image = snapshot.child("imageUrl").getValue().toString();
                            try {
                                Picasso.get().load(Image).into(image);
                                Picasso.get().load(Image).into(layoutBgImg);
                            }catch (Exception e){
                                Picasso.get().load(R.drawable.dropdown).into(image);
                                Picasso.get().load(R.color.green_white).into(layoutBgImg);
                            }
                            //doctorItem.setStatus(snapshot.child("status").getValue().toString());
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}