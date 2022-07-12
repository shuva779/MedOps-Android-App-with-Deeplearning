package com.istiaksaif.medops.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.medops.Activity.AppointmentDoctorActivity;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.R;

import java.util.ArrayList;
import java.util.HashMap;


public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DoctorItem> mdata;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    public DoctorListAdapter(Context context, ArrayList<DoctorItem> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.doctorcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String DocId = mdata.get(position).getUserId();
        holder.doctorname.setText(mdata.get(position).getName());
        holder.workHospital.setText(mdata.get(position).getWorkingIn());
        holder.doctorDegrees.setText(mdata.get(position).getDegrees());
        holder.doctorDesignation.setText(mdata.get(position).getDesignation());
        Glide.with(context).load(mdata.get(position).getImageUrl()).placeholder(R.drawable.dropdown).into(holder.DoctorImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AppointmentDoctorActivity.class);
                intent.putExtra("userId",DocId);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context,holder.DoctorImage,"proImg").toBundle();
                context.startActivity(intent,b);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView DoctorImage;
        TextView doctorname,appointmentButton,doctorDegrees,workHospital,doctorDesignation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DoctorImage = (ImageView) itemView.findViewById(R.id.drimage);
            doctorname = (TextView) itemView.findViewById(R.id.drname);
            doctorDegrees = (TextView) itemView.findViewById(R.id.dr);
            workHospital = (TextView) itemView.findViewById(R.id.workhospital);
            doctorDesignation = (TextView) itemView.findViewById(R.id.drposition);

            appointmentButton = (TextView) itemView.findViewById(R.id.appointmentbtn);
        }
    }
}
