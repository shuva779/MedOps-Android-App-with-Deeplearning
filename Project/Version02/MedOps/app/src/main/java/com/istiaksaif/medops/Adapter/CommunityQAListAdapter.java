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
import com.istiaksaif.medops.Model.QAItem;
import com.istiaksaif.medops.R;

import java.util.ArrayList;


public class CommunityQAListAdapter extends RecyclerView.Adapter<CommunityQAListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<QAItem> mdata;

    public CommunityQAListAdapter(Context context, ArrayList<QAItem> mdata) {
        this.context = context;
        this.mdata = mdata;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.comunityqncard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String quesId = mdata.get(position).getQuesId();
        Glide.with(context).load(mdata.get(position).getQaimage()).placeholder(R.drawable.dropdown).into(holder.Image);
        Glide.with(context).load(mdata.get(position).getUserimage()).placeholder(R.drawable.dropdown).into(holder.userImg);

        holder.userName.setText(mdata.get(position).getUserName());
        holder.ques.setText(mdata.get(position).getQues());
        holder.quesDes.setText(mdata.get(position).getQuesdes());
        holder.reply.setText(mdata.get(position).getReply()+" answers");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QAActivity.class);
                intent.putExtra("quesId",quesId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image,userImg;
        TextView ques,quesDes,userName,reply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.qusimage);
            userImg = (ImageView) itemView.findViewById(R.id.userimage);
            ques = (TextView) itemView.findViewById(R.id.ques);
            quesDes = (TextView) itemView.findViewById(R.id.qusdes);
            userName = (TextView) itemView.findViewById(R.id.username);
            reply = (TextView) itemView.findViewById(R.id.reply);

        }
    }
}
