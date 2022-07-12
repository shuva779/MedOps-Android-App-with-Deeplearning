package com.istiaksaif.medops.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.Activity.AskQusActivity;
import com.istiaksaif.medops.Adapter.CommunityQAListAdapter;
import com.istiaksaif.medops.Adapter.DoctorListAdapter;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.Model.QAItem;
import com.istiaksaif.medops.R;

import java.util.ArrayList;

public class CommunityFragment extends Fragment {

    private RecyclerView qarecycler;
    private CommunityQAListAdapter communityQAListAdapter;
    private ArrayList<QAItem> qaItemArrayList;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ExtendedFloatingActionButton fab;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        qaItemArrayList = new ArrayList<>();

        qarecycler = view.findViewById(R.id.qnRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        qarecycler.setLayoutManager(layoutManager);
        qarecycler.setHasFixedSize(true);
        GetDataFromFirebase();

        fab = (ExtendedFloatingActionButton) view.findViewById(R.id.floatingButtonAdd);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AskQusActivity.class);
                startActivity(intent);
            }
        });
    }
    private void GetDataFromFirebase() {
        Query query = databaseReference.child("CommunityQA");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        QAItem item = new QAItem();
                        item.setQues(snapshot.child("ques").getValue().toString());
                        item.setQaimage(snapshot.child("qaimage").getValue().toString());
                        item.setQuesdes(snapshot.child("quesDescription").getValue().toString());
                        item.setQuesId(snapshot.child("quesId").getValue().toString());
                        item.setReply(Long.toString(snapshot.child("Comments").getChildrenCount()));
                        String userid = snapshot.child("userId").getValue().toString();
                        Query query = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                String k = snapshot1.child("key").getValue().toString();
                                databaseReference.child("usersData").child(k).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                        try {
                                            String uImg = dataSnapshot1.child("imageUrl").getValue().toString();
                                            String uName = dataSnapshot1.child("name").getValue().toString();
                                            item.setUserimage(uImg);
                                            item.setUserName(uName);
                                            qaItemArrayList.add(item);
                                        }catch (Exception e){

                                        }
                                        communityQAListAdapter = new CommunityQAListAdapter(getContext(), qaItemArrayList);
                                        qarecycler.setAdapter(communityQAListAdapter);
                                        communityQAListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(), "Some Thing Wrong", Toast.LENGTH_SHORT).show();
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
        if (qaItemArrayList !=null){
            qaItemArrayList.clear();
            if (communityQAListAdapter !=null){
                communityQAListAdapter.notifyDataSetChanged();
            }
        }
        qaItemArrayList = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        return view;
    }
}