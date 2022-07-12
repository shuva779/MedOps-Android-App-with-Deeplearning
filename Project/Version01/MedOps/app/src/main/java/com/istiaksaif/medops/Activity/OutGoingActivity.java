package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import com.istiaksaif.medops.Utils.FcmNotifySender;
import com.istiaksaif.medops.Utils.PreferenceManager;
import com.istiaksaif.medops.network.ApiClient;
import com.istiaksaif.medops.network.ApiService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutGoingActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String receiver_token;
    private TextView Name;
    private ImageView image,layoutBgImg,endCall,micOnIcon,videoOnIcon;

    private DatabaseReference databaseReference,databaseReference_repo;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Intent intent;
    private String doctorId,meetingType;
    private String inviterToken = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_going);

        preferenceManager =new PreferenceManager(getApplicationContext());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful() && task.getResult() !=null){
                    inviterToken = task.getResult();
                }
            }
        });
        intent = getIntent();
        doctorId = intent.getStringExtra("doctorId");
        meetingType = intent.getStringExtra("type");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        layoutBgImg = findViewById(R.id.layoutBgImg);
        image = findViewById(R.id.profileimage);
        Name = findViewById(R.id.callernamed);
        endCall = findViewById(R.id.endCall);
        micOnIcon = findViewById(R.id.micOnIcon);
        videoOnIcon = findViewById(R.id.videoOnIcon);

        endCall.setOnClickListener(view -> onBackPressed());
        GetDataFromFirebase();
        sendCallInvitation();
    }

    private void initiateMeeting(String meetingType, String receiverToken){
        try {
            JSONArray token = new JSONArray();
            token.put(receiverToken);
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);
            data.put("first_name",preferenceManager.getString("first_name"));
            data.put("last_name",preferenceManager.getString("last_name"));
            data.put("email",preferenceManager.getString("email"));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

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

    private void checkResponse() {
        databaseReference_repo = FirebaseDatabase.getInstance().getReference("vcref").child(uid).child(doctorId);
        databaseReference_repo.child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String key = snapshot.child("key").getValue().toString();
                    String response = snapshot.child("response").getValue().toString();
                    if(response.equals("yes")){
                        joinmeeting(key);
                        Toast.makeText(OutGoingActivity.this,"Call Accepted",Toast.LENGTH_SHORT).show();

                    }else if(response.equals("no")){
                        Toast.makeText(OutGoingActivity.this,"busy...",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OutGoingActivity.this,AppointmentDoctorActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Toast.makeText(OutGoingActivity.this,"not responding",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void joinmeeting(String key) {
        try {
//            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
//                    .setServerURL(new URL("https://meet.jit.si"))
//                    .setRoom(key).setWelcomePageEnabled(false).build();
//            JitsiMeetActivity.launch(OutGoingActivity.this,options);
            finish();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCallInvitation() {
        databaseReference.child(doctorId).child("token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiver_token = snapshot.getValue(String.class);
                initiateMeeting(meetingType, receiver_token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                FcmNotifySender fcmNotifySender = new FcmNotifySender(receiver_token,"MedOps",uid,
//                        getApplicationContext(),OutGoingActivity.this);
//                fcmNotifySender.SendNotify();
//            }
//        }, 1000);
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("users").orderByChild("doctorId").equalTo(doctorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Name.setText(snapshot.child("doctorName").getValue().toString());
                        String Image = snapshot.child("image").getValue().toString();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}