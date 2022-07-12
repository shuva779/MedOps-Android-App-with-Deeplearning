package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.Adapter.CommnetListAdapter;
import com.istiaksaif.medops.Adapter.DoctorListAdapter;
import com.istiaksaif.medops.Model.Chat;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.Model.User;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.UsersListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AppointmentDoctorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView drName,drNickName,drStudies,drWork,drExperience;
    private ImageView drImage;
    private TextView appoinButton,confirmButton,date,hour,min;
    private LinearLayout videoCallButton;
    RelativeLayout check;
    private UsersListener usersListener;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Intent intent;
    private String doctorId,dateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        intent = getIntent();
        doctorId = intent.getStringExtra("doctorId");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.drtoolbar);
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

        drImage = findViewById(R.id.drimage);
        drName = findViewById(R.id.name);
        drNickName = findViewById(R.id.nickname);
        drStudies = findViewById(R.id.studies);
        drExperience = findViewById(R.id.post);
        drWork = findViewById(R.id.workhospital);
        appoinButton = findViewById(R.id.takeapponbtn);
        appoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AppointmentDoctorActivity.this);
                bottomSheetDialog.setContentView(R.layout.appoinment_popup);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                date = bottomSheetDialog.findViewById(R.id.date);
                date.setOnClickListener(new View.OnClickListener() {
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

                bottomSheetDialog.show();
            }
        });
        GetDataFromFirebase();

        videoCallButton = findViewById(R.id.videocallbtn);
        check = findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentDoctorActivity.this,OutGoingActivity.class);
                intent.putExtra("doctorId",doctorId);
                intent.putExtra("type","video");
                startActivity(intent);
            }
        });
    }
    private void GetDataFromFirebase() {
        Query query = databaseReference.child("users").orderByChild("doctorId").equalTo(doctorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        drName.setText(snapshot.child("doctorName").getValue().toString());
                        String Image = snapshot.child("image").getValue().toString();
                        try {
                            Picasso.get().load(Image).resize(320,320).into(drImage);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.dropdown).into(drImage);
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

    private DatePickerDialog.OnDateSetListener datepickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            String monthname;
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            monthname = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            month = month+1;
            dateStr = monthname+"  "+dayOfMonth;
            date.setText(dateStr);
        }
    };
}