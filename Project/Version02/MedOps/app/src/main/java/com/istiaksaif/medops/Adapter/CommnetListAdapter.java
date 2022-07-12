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
import com.istiaksaif.medops.Activity.QAActivity;
import com.istiaksaif.medops.Model.Chat;
import com.istiaksaif.medops.Model.QAItem;
import com.istiaksaif.medops.R;

import java.util.ArrayList;


public class CommnetListAdapter extends RecyclerView.Adapter<CommnetListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Chat> mdata;

    public CommnetListAdapter(Context context, ArrayList<Chat> mdata) {
        this.context = context;
        this.mdata = mdata;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.commentcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(mdata.get(position).getUserImage()).placeholder(R.drawable.dropdown).into(holder.userImg);

        holder.userName.setText(mdata.get(position).getUserName());
        holder.comment.setText(mdata.get(position).getMessage());
        holder.time.setText(mdata.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImg;
        TextView comment,time,userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = (ImageView) itemView.findViewById(R.id.userimage);
            comment = (TextView) itemView.findViewById(R.id.comment);
            time = (TextView) itemView.findViewById(R.id.time);
            userName = (TextView) itemView.findViewById(R.id.username);
        }
    }
}
