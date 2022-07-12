package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.Constants;
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

public class IncomingCallActivity extends AppCompatActivity {

    private TextView Name;
    private ImageView image,layoutBgImg,endCall,answerCall;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Intent intent;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        intent = getIntent();
        userId = intent.getStringExtra("userId");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        layoutBgImg = findViewById(R.id.layoutBgImg);
        image = findViewById(R.id.profileimage);
        Name = findViewById(R.id.callernamed);
        endCall = findViewById(R.id.rejectCall);
        answerCall = findViewById(R.id.answerCall);

        GetDataFromFirebase();
        answerCall.setOnClickListener(view -> sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)));
        endCall.setOnClickListener(view -> sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)));
    }

    private void sendInvitationResponse(String type,String receiverToken){
        try {
            JSONArray token = new JSONArray();
            token.put(receiverToken);
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type);

            jsonObject.put(Constants.REMOTE_MSG_DATA,data);
            jsonObject.put(Constants.REMOTE_MSG_REG_IDS,token);

            sendRemoteMessage(jsonObject.toString(),type);

        }catch (Exception e){

        }
    }

    private void sendRemoteMessage(String remoteMessageBody,String type){
        ApiClient.getRetrofit().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeader(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                        try {
                            URL serverUrl = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions conferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverUrl).setWelcomePageEnabled(false)
                                    .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                                    .build();
                            JitsiMeetActivity.launch(IncomingCallActivity.this,conferenceOptions);
                            finish();
                        }catch (Exception e){
                            Toast.makeText(IncomingCallActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else{
                        Toast.makeText(IncomingCallActivity.this,"Invitation Rejected",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(IncomingCallActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                    finish();
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(IncomingCallActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCEL)){
                    Toast.makeText(context,"Invitation Cancelled",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

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
                broadcastReceiver
        );
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("users").child(userId);
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
                                Picasso.get().load(Image).resize(320,320).into(image);
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