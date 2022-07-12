package com.istiaksaif.medops.Adapter;

import android.content.Context;
import android.content.Intent;
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
        String DocId = mdata.get(position).getDoctorId();
        holder.doctorname.setText(mdata.get(position).getDoctorName());
        holder.workHospital.setText(mdata.get(position).getHospital());
        Glide.with(context).load(mdata.get(position).getImage()).placeholder(R.drawable.dropdown).into(holder.DoctorImage);

        if(mdata.get(position).getStatus().equals("")){
            holder.appointmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    bottomSheetDialog.setContentView(R.layout.appoinment_popup);
                    bottomSheetDialog.setCanceledOnTouchOutside(false);

                    bottomSheetDialog.show();
//                    HashMap<String, Object> result = new HashMap<>();
//                    result.put("userId", uid);
//                    result.put("status", "pending");
//                    databaseReference.child("Doctors").child(DocId).child("patientsList").child(uid).updateChildren(result);
//                    holder.appointmentButton.setText("pending");
                }
            });
        }else if(mdata.get(position).getStatus().equals("confirm")){
            holder.appointmentButton.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AppointmentDoctorActivity.class);
                intent.putExtra("doctorId",DocId);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView DoctorImage;
        TextView doctorname,appointmentButton,doctorlevel,workHospital,doctorPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DoctorImage = (ImageView) itemView.findViewById(R.id.drimage);
            doctorname = (TextView) itemView.findViewById(R.id.drname);
            doctorlevel = (TextView) itemView.findViewById(R.id.dr);
            workHospital = (TextView) itemView.findViewById(R.id.workhospital);
            doctorPosition = (TextView) itemView.findViewById(R.id.drposition);

            appointmentButton = (TextView) itemView.findViewById(R.id.appointmentbtn);
        }
    }
}
