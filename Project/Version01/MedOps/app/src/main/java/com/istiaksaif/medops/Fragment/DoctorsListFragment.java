package com.istiaksaif.medops.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.Adapter.DoctorListAdapter;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DoctorsListFragment extends Fragment {

    private RecyclerView doctorsRecycler;
    private DoctorListAdapter doctorListAdapter;
    private ArrayList<DoctorItem> doctorItemList;
    private DatabaseReference doctorsDatabaseRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doctorsDatabaseRef = FirebaseDatabase.getInstance().getReference();
        doctorItemList = new ArrayList<>();

        doctorsRecycler = view.findViewById(R.id.drRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        doctorsRecycler.setLayoutManager(layoutManager);
        doctorsRecycler.setHasFixedSize(true);
        GetDataFromFirebase();
    }
    private void GetDataFromFirebase() {
        Query query = doctorsDatabaseRef.child("users").orderByChild("isUser").equalTo("Doctor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        if (snapshot.child("verifyStatus").getValue().toString().equals("verified")){
                            DoctorItem doctorItem = new DoctorItem();
                            doctorItem.setDoctorName(snapshot.child("doctorName").getValue().toString());
                            doctorItem.setImage(snapshot.child("image").getValue().toString());
                            doctorItem.setDoctorId(snapshot.child("doctorId").getValue().toString());
                            doctorItem.setStatus(snapshot.child("status").getValue().toString());

                            doctorItemList.add(doctorItem);
                        }
                    } catch (Exception e) {

                    }
                }
                doctorListAdapter = new DoctorListAdapter(getContext(), doctorItemList);
                doctorsRecycler.setAdapter(doctorListAdapter);
                doctorListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ClearAll(){
        if (doctorItemList !=null){
            doctorItemList.clear();
            if (doctorListAdapter !=null){
                doctorListAdapter.notifyDataSetChanged();
            }
        }
        doctorItemList = new ArrayList<>();
    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctors, container, false);
        return view;
    }
}