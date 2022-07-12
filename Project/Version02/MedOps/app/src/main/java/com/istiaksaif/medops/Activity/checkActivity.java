package com.istiaksaif.medops.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.R;

import java.util.HashMap;

public class checkActivity extends AppCompatActivity {

    private Button submit;
    private TextInputEditText Nid;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    /**
     * Created by Istiak Saif on 02/07/21.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        popupDialogUserTypeDefine();

    }
    public void popupDialogUserTypeDefine(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.usernid,null);

        Nid = (TextInputEditText) contactPopupView.findViewById(R.id.nid);

        dialogBuilder.setView(contactPopupView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.show();


        submit = (Button) contactPopupView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NID = Nid.getText().toString();
                if (NID.length()<13){
                    Toast.makeText(checkActivity.this, "Nid number minimum 13 and max 18 numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!TextUtils.isEmpty(NID)){
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("nid", NID);

                    databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String key = snapshot.child("key").getValue().toString();
                                    databaseReference.child("usersData").child(key)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("usersData").child(snapshot.getKey()).updateChildren(result);
                                                    Intent intent = new Intent(checkActivity.this, UserHomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(),"Error ", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(getApplicationContext(),"Please Enter ", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    private void checkUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        Query query = databaseReference.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("isUser").getValue(String.class).equals("User")) {
                    Intent intent = new Intent(checkActivity.this, UserHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (snapshot.child("isUser").getValue(String.class).equals("Doctor")) {
                    Intent intent = new Intent(checkActivity.this, UserHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (snapshot.child("isUser").getValue(String.class).equals("Nurse")) {
                    Intent intent = new Intent(checkActivity.this, UserHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (snapshot.child("isUser").getValue(String.class).equals("Admin")) {
                    Intent intent = new Intent(checkActivity.this, AdminManagerHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}