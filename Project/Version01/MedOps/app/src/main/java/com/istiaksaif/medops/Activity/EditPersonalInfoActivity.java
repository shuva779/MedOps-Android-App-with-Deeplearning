package com.istiaksaif.medops.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.R;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Istiak Saif on 10/08/21.
 */

public class EditPersonalInfoActivity extends AppCompatActivity {

    private TextInputEditText fullName,dateOfBirth,nid,height,weight;
    private MaterialAutoCompleteTextView bloodGroup;
    private Button nextButton;
    private String date;
    private Toolbar toolBar;


    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        fullName = findViewById(R.id.name);
        dateOfBirth = findViewById(R.id.dateofbirth);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int nYear = calendar.get(Calendar.YEAR);
                int nMonth = calendar.get(Calendar.MONTH);
                int nDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, datepickerListener, nYear, nMonth, nDay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        nid = findViewById(R.id.nid);
        height = findViewById(R.id.heightinput);
        weight = findViewById(R.id.weightinut);
        bloodGroup = findViewById(R.id.bloodgroup);
        TextInputLayout textInputLayoutblood = findViewById(R.id.bloodgrouplayout);
        String []optionUniName = {"A+","A-","B+","B-","AB+"
                ,"AB-","O+","O-"};
        ArrayAdapter<String> arrayAdapterUni = new ArrayAdapter<>(this,R.layout.usertype_item,optionUniName);
        ((MaterialAutoCompleteTextView) textInputLayoutblood.getEditText()).setAdapter(arrayAdapterUni);

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info();
            }
        });


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String name = ""+dataSnapshot.child("name").getValue();
                    String dob = ""+dataSnapshot.child("dob").getValue();
                    String blood = ""+dataSnapshot.child("bloodgroup").getValue();
                    String receivenid = ""+dataSnapshot.child("nid").getValue();
                    String Height = ""+dataSnapshot.child("height").getValue();
                    String Weight = ""+dataSnapshot.child("weight").getValue();

//                    lname = findViewById(R.id.layoutname);
//                    lblood = findViewById(R.id.bloodgrouplayout);
//                    lnid = findViewById(R.id.layoutnid);
//                    lheight = findViewById(R.id.layoutheight);
//                    lweight = findViewById(R.id.layoutweight);
                    fullName.setText(name);
                    dateOfBirth.setText(dob);
                    bloodGroup.setText(blood);
                    nid.setText(receivenid);
                    height.setText(Height);
                    weight.setText(Weight);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditPersonalInfoActivity.this,"Some Thing Wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void Info() {
        String FullName = fullName.getText().toString();
        String DateOfBirth = dateOfBirth.getText().toString();
        String NID = nid.getText().toString();
        String BloodGroup = bloodGroup.getText().toString();
        String Height = height.getText().toString();
        String Weight = weight.getText().toString();
//        String Age = age.getText().toString();

        if (TextUtils.isEmpty(FullName)){
            Toast.makeText(EditPersonalInfoActivity.this, "please enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(DateOfBirth)){
            Toast.makeText(EditPersonalInfoActivity.this, "please enter DateOfBirth", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(NID)){
            Toast.makeText(EditPersonalInfoActivity.this, "please enter NID", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (NID.length()<13 ){
            Toast.makeText(EditPersonalInfoActivity.this, "Nid number minimum 13 and max 18 numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(BloodGroup)){
            Toast.makeText(EditPersonalInfoActivity.this, "please enter bloodGroup", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", FullName);
        result.put("nid", NID);
        result.put("bloodgroup", BloodGroup);
        result.put("dob", DateOfBirth);
//        result.put("age", Age);
        result.put("height", Height);
        result.put("weight", Weight);


        databaseReference.child(uid).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditPersonalInfoActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    private DatePickerDialog.OnDateSetListener datepickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            month = month+1;
            date = dayOfMonth+"/"+month+"/"+year;
            dateOfBirth.setText(date);
        }
    };
}