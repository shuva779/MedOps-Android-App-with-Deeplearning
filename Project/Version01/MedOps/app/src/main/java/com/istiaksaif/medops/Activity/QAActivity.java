package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.istiaksaif.medops.Adapter.CommnetListAdapter;
import com.istiaksaif.medops.Adapter.CommunityQAListAdapter;
import com.istiaksaif.medops.Model.Chat;
import com.istiaksaif.medops.Model.QAItem;
import com.istiaksaif.medops.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class QAActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView ques,quesdes,userName,answer;
    private ImageView qaimage,userImage,sendbutton;
    private EditText comment;

    private RecyclerView commentRecycler;
    private CommnetListAdapter commnetListAdapter;
    private ArrayList<Chat> chatArrayList;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Intent intent;
    private String quesId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_a);

        intent = getIntent();
        quesId = intent.getStringExtra("quesId");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.qatoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userName = findViewById(R.id.username);
        ques = findViewById(R.id.ques);
        quesdes = findViewById(R.id.qusdes);
        answer = findViewById(R.id.reply);
        qaimage = findViewById(R.id.qaimage);
        userImage = findViewById(R.id.userimage);
        comment = findViewById(R.id.sendmessage);
        sendbutton = findViewById(R.id.sendicon);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = null;
                try {
                    msg = comment.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!msg.equals("")) {
                    sendMessage(uid, msg);//changes
                } else {
                    Toast.makeText(QAActivity.this, "empty message", Toast.LENGTH_SHORT).show();
                }
                comment.setText("");
            }
        });
        GetDataFromFirebase();

        chatArrayList = new ArrayList<>();

        commentRecycler = findViewById(R.id.commentRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecycler.setLayoutManager(layoutManager);
        commentRecycler.setHasFixedSize(true);
        readMessage();
    }
    private void sendMessage(String sender, String message) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());

        HashMap<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("message", message);
        result.put("time", currentTime);
        result.put("date", currentDate);
        databaseReference.child("CommunityQA").child(quesId).child("Comments").push().setValue(result);
    }
    private void readMessage() {
        Query query = databaseReference.child("CommunityQA").child(quesId).child("Comments");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Chat chat = new Chat();
                        chat.setMessage(snapshot.child("message").getValue().toString());
                        chat.setTime(snapshot.child("time").getValue().toString());
                        String userid = snapshot.child("sender").getValue().toString();
                        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("userId").equalTo(userid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                for (DataSnapshot dataSnapshot1:snapshot1.getChildren()){
                                    try {
                                        chat.setUserName(dataSnapshot1.child("name").getValue().toString());
                                        chat.setUserImage(dataSnapshot1.child("imageUrl").getValue().toString());

                                        chatArrayList.add(chat);
                                    }catch (Exception e){

                                    }
                                }
                                commnetListAdapter = new CommnetListAdapter(QAActivity.this, chatArrayList);
                                commentRecycler.setAdapter(commnetListAdapter);
                                commnetListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(QAActivity.this, "Some Thing Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetDataFromFirebase() {
        Query query = databaseReference.child("CommunityQA").orderByChild("quesId").equalTo(quesId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ques.setText(snapshot.child("ques").getValue().toString());
                        answer.setText(Long.toString(snapshot.child("Comments").getChildrenCount())+" answers");
                        String QUESImage = snapshot.child("qaimage").getValue().toString();
                        quesdes.setText(snapshot.child("quesDescription").getValue().toString());
                        try {
                            Picasso.get().load(QUESImage).resize(320,320).into(qaimage);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.dropdown).into(qaimage);
                        }
                        String userid = snapshot.child("userId").getValue().toString();
                        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("userId").equalTo(userid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                for (DataSnapshot dataSnapshot1:snapshot1.getChildren()){
                                    try {
                                        userName.setText(dataSnapshot1.child("name").getValue().toString());
                                        String uImage = dataSnapshot1.child("imageUrl").getValue().toString();
                                        try {
                                            Picasso.get().load(uImage).resize(320,320).into(userImage);
                                        }catch (Exception e){
                                            Picasso.get().load(R.drawable.dropdown).into(userImage);
                                        }
                                    }catch (Exception e){

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(QAActivity.this, "Some Thing Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ClearAll(){
        if (chatArrayList !=null){
            chatArrayList.clear();
            if (commnetListAdapter !=null){
                commnetListAdapter.notifyDataSetChanged();
            }
        }
        chatArrayList = new ArrayList<>();
    }
}