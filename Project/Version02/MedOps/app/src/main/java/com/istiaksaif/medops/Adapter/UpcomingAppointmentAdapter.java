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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.medops.Activity.AppointmentDoctorActivity;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingAppointmentAdapter extends RecyclerView.Adapter<UpcomingAppointmentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DoctorItem> mdata;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    public UpcomingAppointmentAdapter(Context context, ArrayList<DoctorItem> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.upcoming_visit,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String DocId = mdata.get(position).getUserId();
        holder.doctorname.setText(mdata.get(position).getName());
        holder.doctorDesignation.setText(mdata.get(position).getDesignation());
        Glide.with(context).load(mdata.get(position).getImageUrl()).placeholder(R.drawable.dropdown).into(holder.DoctorImage);
        String timeS = mdata.get(position).getConsultHourTo();

        String splitTime[] = timeS.split(":");
        int time  = Integer.parseInt(splitTime[0]);
        if (time>12){
            holder.time.setText(String.valueOf(time-12)+":"+splitTime[1]+" pm");
        }else {
            holder.time.setText(String.valueOf(timeS)+" am");
        }
        holder.status.setText(mdata.get(position).getDegrees());
        final String OLD_FORMAT = "MMM dd, yyyy";
        final String NEW_FORMAT = "dd/MM/yyyy";

        String oldDateString = mdata.get(position).getDob();
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        holder.date.setText(newDateString);

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
        TextView doctorname,doctorDesignation,date,time,status;
        LinearLayout cancel,reschedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DoctorImage = (ImageView) itemView.findViewById(R.id.doctorimage);
            doctorname = (TextView) itemView.findViewById(R.id.doctor_name);
            doctorDesignation = (TextView) itemView.findViewById(R.id.designation);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            status = (TextView) itemView.findViewById(R.id.status);
            cancel = (LinearLayout) itemView.findViewById(R.id.cancel);
            reschedule = (LinearLayout) itemView.findViewById(R.id.reschedule);
        }
    }
}
