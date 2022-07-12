package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.AgeCalculator;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AppointmentDoctorActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private TextView drName,drNickName,drStudies,workingIn,drDesignation,workingExperience,
            consultHourTo,consultHour,consulthourtostatus,consulthourstatus,consultDays,
            date,hour,min,available_status,sufficient_balance,consultFee,nameOfDay;
    private ImageView drImage;
    private int time,time1;
    private LinearLayout appoinButton,videoCallButton,confirmButton;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Intent intent;
    private String doctorId,dateStr,setTime,dayName;
    private AgeCalculator age = null;
    private NumberPicker hourPicker,minPicker;
    private LottieAnimationView cross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        age=new AgeCalculator();
        age.getCurrentDate();

        intent = getIntent();
        doctorId = intent.getStringExtra("userId");
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
        drStudies = findViewById(R.id.degrees);
        drDesignation = findViewById(R.id.designation);
        workingExperience = findViewById(R.id.workingExperience);
        consultHour = findViewById(R.id.ch);
        consultHourTo = findViewById(R.id.cht);
        consulthourstatus = findViewById(R.id.consulthourstatus);
        consulthourtostatus = findViewById(R.id.consulthourtostatus);
        workingIn = findViewById(R.id.workhospital);
        videoCallButton = findViewById(R.id.videocallbtn);
        sufficient_balance = findViewById(R.id.sufficient_balance);
        consultFee = findViewById(R.id.feeamount);
        consultDays = findViewById(R.id.consultDays);
        GetDataFromFirebase();

        appoinButton = findViewById(R.id.takeapponbtn);
        databaseReference.child("users").child(doctorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String k = dataSnapshot.child("key").getValue().toString();
                        databaseReference.child("usersData").child(k).child("appointment")
                                .orderByChild("userId").equalTo(uid).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                                    try {
                                        String s = snapshot.child("status").getValue().toString();
                                        String d = snapshot.child("date").getValue().toString();
                                        String t = snapshot.child("time").getValue().toString();

                                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                                        int ts1 = (int) (format.parse(d.trim() + " " + t.trim()).getTime() / 1000);

                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(new Date());
                                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                                        sdf.setCalendar(calendar);
                                        sdf.setTimeZone(TimeZone.getTimeZone("BST"));

                                        String serverTime = sdf.format(calendar.getTime());
                                        int ts = (int) (sdf.parse(serverTime.trim()).getTime()/ 1000);
                                        if(s.equals("confirm")){
                                            if(ts>ts1){
                                                appoinButton.setVisibility(View.GONE);
                                                videoCallButton.setVisibility(View.VISIBLE);
                                                sufficient_balance.setVisibility(View.GONE);
                                            }else if(ts<ts1){
                                                appoinButton.setVisibility(View.GONE);
                                                videoCallButton.setVisibility(View.VISIBLE);
                                                videoCallButton.setBackground(getResources().getDrawable(R.drawable.rectangle_confirm1));
                                                sufficient_balance.setVisibility(View.GONE);
                                            }
                                        }
                                    }catch (Exception e){

                                    }
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
        appoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AppointmentDoctorActivity.this);
                bottomSheetDialog.setContentView(R.layout.appoinment_popup);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                age=new AgeCalculator();
                date = bottomSheetDialog.findViewById(R.id.date);
                nameOfDay = bottomSheetDialog.findViewById(R.id.nameOfDay);
                confirmButton = bottomSheetDialog.findViewById(R.id.confirm_appoin_button);
                nameOfDay.setText(age.getNameOfDay());
                available_status = bottomSheetDialog.findViewById(R.id.available_status);
                hour = bottomSheetDialog.findViewById(R.id.hourstore);
                min = bottomSheetDialog.findViewById(R.id.minstore);
                hourPicker = bottomSheetDialog.findViewById(R.id.hourpicker);
                minPicker = bottomSheetDialog.findViewById(R.id.minpicker);
                date.setText(age.getCurrentDateOfMonthName()+"  ");
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int nYear = calendar.get(Calendar.YEAR);
                        int nMonth = calendar.get(Calendar.MONTH);
                        int nDay = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth, datepickerListener, nYear, nMonth, nDay);
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });

                hourPicker.setMinValue(time);
                hourPicker.setMaxValue(time1);
                minPicker.setMinValue(00);
                minPicker.setMaxValue(59);

                appointment();
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String q = databaseReference.push().getKey();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("time", setTime);
                        result.put("date", date.getText().toString());
                        result.put("appointmentId", q);
                        result.put("userId", uid);
                        result.put("status", "confirm");

                        databaseReference.child("users").child(uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String k = snapshot.child("key").getValue().toString();
                                        databaseReference.child("usersData").child(k).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        int balance = 0;
                                                        String s = snapshot.child("balanceTk").getValue(String.class);
                                                        balance = (Integer.parseInt(s.trim()))-(
                                                                Integer.parseInt(consultFee.getText().toString().trim()));

                                                        HashMap<String, Object> result1 = new HashMap<>();
                                                        result1.put("balanceTk", String.valueOf(balance));
                                                        databaseReference.child("usersData").
                                                                child(snapshot.getKey()).updateChildren(result1);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(AppointmentDoctorActivity.this,
                                                                "Error ", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        databaseReference.child("users").child(doctorId).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String k = snapshot.child("key").getValue().toString();
                                databaseReference.child("usersData").child(k).child("appointment")
                                        .child(q).updateChildren(result).addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        appoinButton.setVisibility(View.GONE);
                                        videoCallButton.setVisibility(View.VISIBLE);
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                                result.put("doctorId", doctorId);
                                databaseReference.child("appointment")
                                        .child(q).updateChildren(result);
                                databaseReference.child("usersData").child(k)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int balance2 = 0;
                                        String ss = snapshot.child("balanceTk").getValue(String.class);
                                        balance2 = (Integer.parseInt(ss.trim()))+(
                                                Integer.parseInt(consultFee.getText().toString().trim()));

                                        HashMap<String, Object> result2 = new HashMap<>();
                                        result2.put("balanceTk", String.valueOf(balance2));
                                        databaseReference.child("usersData").
                                                child(snapshot.getKey()).updateChildren(result2);
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
                });
                cross = (LottieAnimationView) bottomSheetDialog.findViewById(R.id.cross);
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
        videoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentDoctorActivity.this,OutGoingActivity.class);
                intent.putExtra("userId",doctorId);
                intent.putExtra("type","video");
                startActivity(intent);
            }
        });
    }

    private void appointment() {
        String splitTime[] = consultDays.getText().toString().split(", ");
        boolean found = false;
        for (int i = 0; i < splitTime.length; i++){
            if (splitTime[i].equals(nameOfDay.getText().toString())){
                Toast.makeText(getApplicationContext(), "now choose your desire time", Toast.LENGTH_SHORT).show();
                hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        hour.setText(String.valueOf(i1));
                    }
                });
                minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        min.setText(String.valueOf(i1));
                        setTime = (hour.getText().toString()+":"+min.getText().toString());
                        int minsToAdd = 15;

                        databaseReference.child("users").child(doctorId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String k = snapshot.child("key").getValue().toString();
                                databaseReference.child("usersData").child(k).child("appointment").
                                        addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                List<Integer> intArray = new ArrayList<>();
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    try {
                                                        String s = snapshot.child("time").getValue(String.class);//time=19:15
                                                        String d = snapshot.child("date").getValue(String.class);

                                                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                                                        long ts = format.parse(date.getText().toString()
                                                                + setTime).getTime() / 1000;
                                                        int dd = (int) ts;
                                                        long ts1 = format.parse(d.trim() + " " + s.trim()).getTime() / 1000;
                                                        intArray.add((int) ts1);
                                                        for (int k = 0; k < intArray.size(); k++) {
                                                            if ((dd - minsToAdd * 60) < intArray.get(k) &&
                                                                    (dd + minsToAdd * 60) > intArray.get(k)) {
                                                                available_status.setText(R.string.timeUnavailable);
                                                                available_status.setTextColor(getResources().getColor(R.color.pink));
                                                                confirmButton.setClickable(false);
                                                                confirmButton.setBackgroundDrawable(
                                                                        getResources().getDrawable(R.drawable.rectangle_confirm1));
                                                                break;
                                                            } else {
                                                                available_status.setText(R.string.idealTime);
                                                                confirmButton.setClickable(true);
                                                                available_status.setTextColor(getResources().getColor(R.color.dark));
                                                                confirmButton.setBackgroundDrawable(
                                                                        getResources().getDrawable(R.drawable.rectangle_confirm));
                                                            }
                                                        }
                                                    } catch (Exception e) {

                                                    }
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
                });
                found=true;
                break;
            }
        }
        if(!found){
            confirmButton.setClickable(false);
            confirmButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_confirm1));
            Toast.makeText(getApplicationContext(), "Doctor isn't available at "+nameOfDay.
                    getText().toString()+"", Toast.LENGTH_SHORT).show();
        }
        if(found==true && hour.getText().toString().equals(null) && min.getText().toString().equals(null)) {
            confirmButton.setClickable(true);
            confirmButton.setBackground(getResources().getDrawable(R.drawable.rectangle_confirm));
        }
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("users").child(doctorId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String k = dataSnapshot.child("key").getValue().toString();
                databaseReference.child("usersData").child(k).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    String fee = snapshot.child("consultFee").getValue().toString();
                                    consultFee.setText(fee);
                                    databaseReference.child("users").child(uid)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String key = dataSnapshot.child("key").getValue().toString();
                                                    databaseReference.child("usersData").child(key)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    try {
                                                                        String balanceTk = snapshot.child("balanceTk")
                                                                                .getValue().toString();
                                                                        if(Integer.parseInt(balanceTk)>=Integer.parseInt(fee)){
                                                                            sufficient_balance.setVisibility(View.GONE);
                                                                        }else {
                                                                            sufficient_balance.setVisibility(View.VISIBLE);
                                                                            appoinButton.setClickable(false);
                                                                            appoinButton.setBackground(
                                                                                    getResources()
                                                                                            .getDrawable(R.drawable.rectangle_33));
                                                                        }
                                                                    }catch (Exception e){

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
                                    String name = snapshot.child("name").getValue().toString();
                                    String splitName[] = name.split(" ");
                                    int i = splitName.length;
                                    drNickName.setText(splitName[i-1]);
                                    drName.setText(name.replace(splitName[i-1],""));
                                    workingIn.setText(snapshot.child("workingIn").getValue().toString());
                                    drStudies.setText(snapshot.child("degrees").getValue().toString());
                                    drDesignation.setText(snapshot.child("designation").getValue().toString());
                                    consultDays.setText(snapshot.child("consultDays").getValue().toString());
                                    String experience = snapshot.child("workingExperience").getValue().toString();
                                    try {
                                        String str1[] = experience.split("/");
                                        int dayOfMonth1 = Integer.parseInt(str1[0]);
                                        int month1 = Integer.parseInt(str1[1]);
                                        int year1 = Integer.parseInt(str1[2]);
                                        age.setDateOfBirth(year1, month1, dayOfMonth1);
                                        age.calcualteYear();
                                        age.calcualteMonth();
                                        age.calcualteDay();
                                        workingExperience.setText(age.getResult());
                                    }catch (Exception e){

                                    }
                                    String Image = snapshot.child("imageUrl").getValue().toString();
                                    try {
                                        Picasso.get().load(Image).resize(320,320).into(drImage);
                                    }catch (Exception e){
                                        Picasso.get().load(R.drawable.dropdown).into(drImage);
                                    }
                                    String timeS = snapshot.child("consultHour").getValue().toString();

                                    String splitTime[] = timeS.split(":");
                                    time  = Integer.parseInt(splitTime[0]);
                                    if (time>12){
                                        consultHour.setText(String.valueOf(time-12));
                                        consulthourstatus.setText("pm-");
                                    }else {
                                        consultHour.setText(String.valueOf(time));
                                        consulthourstatus.setText("am-");
                                    }
                                    String timeS1 = snapshot.child("consultHourTo").getValue().toString();
                                    String splittime[] = timeS1.split(":");
                                    time1 = Integer.parseInt(splittime[0]);
                                    if (time1>12){
                                        consultHourTo.setText(String.valueOf(time1-12));
                                        consulthourtostatus.setText("pm");
                                    }else {
                                        consultHourTo.setText(String.valueOf(time1));
                                        consulthourtostatus.setText("am");
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
            dateStr = monthname+"  "+dayOfMonth+", "+year;
            dayName=calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            date.setText(dateStr+"  ");
            nameOfDay.setText(dayName);

            appointment();
        }
    };
}