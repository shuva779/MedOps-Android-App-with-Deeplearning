package com.istiaksaif.medops.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.medops.Activity.PredictActivity;
import com.istiaksaif.medops.Adapter.DoctorListAdapter;
import com.istiaksaif.medops.Adapter.UpcomingAppointmentAdapter;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.ImageGetHelper;
import com.istiaksaif.medops.ml.MedOps;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserHomeFragment extends Fragment {

    private ImageGetHelper getImageFunction;
    private LinearLayout takeImageCard;

    private RecyclerView appoinRecycler;
    private UpcomingAppointmentAdapter upcomingAppointmentAdapter;
    private ArrayList<DoctorItem> ItemList;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private TextView visitText;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getImageFunction = new ImageGetHelper(this,null);

        takeImageCard = view.findViewById(R.id.takeimgcard);
        takeImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFunction.pickFromGallery();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        ItemList = new ArrayList<>();

        visitText = view.findViewById(R.id.visitText);
        visitText.setVisibility(View.GONE);
        appoinRecycler = view.findViewById(R.id.appoinRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        appoinRecycler.setLayoutManager(layoutManager);
        appoinRecycler.setHasFixedSize(true);
        GetDataFromFirebase();
    }

    private void GetDataFromFirebase() {
        Query query = databaseReference.child("appointment").orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DoctorItem doctorItem = new DoctorItem();
                    try {
                        String Status = snapshot.child("status").getValue().toString();
                        if (Status.equals("confirm")){
                            visitText.setVisibility(View.VISIBLE);
                            doctorItem.setDegrees(snapshot.child("status").getValue().toString());
                            doctorItem.setConsultHourTo(snapshot.child("time").getValue().toString());
                            doctorItem.setDob(snapshot.child("date").getValue().toString());

                            String doctorId = snapshot.child("doctorId").getValue().toString();
                            Query query1 = databaseReference.child("users").child(doctorId);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String k = snapshot.child("key").getValue().toString();
                                    databaseReference.child("usersData").child(k)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    doctorItem.setName(snapshot.child("name").getValue().toString());
                                                    doctorItem.setImageUrl(snapshot.child("imageUrl").getValue().toString());
                                                    doctorItem.setDesignation(snapshot.child("designation").getValue().toString());

                                                    ItemList.add(doctorItem);

                                                    upcomingAppointmentAdapter = new UpcomingAppointmentAdapter(getContext(), ItemList);
                                                    appoinRecycler.setAdapter(upcomingAppointmentAdapter);
                                                    upcomingAppointmentAdapter.notifyDataSetChanged();
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
        if (ItemList !=null){
            ItemList.clear();
            if (upcomingAppointmentAdapter !=null){
                upcomingAppointmentAdapter.notifyDataSetChanged();
            }
        }
        ItemList = new ArrayList<>();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == getImageFunction.IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri image = data.getData();
            Intent intent = new Intent(getActivity(), PredictActivity.class);
            intent.putExtra("image",image.toString());
            getActivity().startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        return view;
    }
}