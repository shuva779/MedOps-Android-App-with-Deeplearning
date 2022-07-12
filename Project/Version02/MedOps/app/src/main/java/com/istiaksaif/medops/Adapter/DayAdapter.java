package com.istiaksaif.medops.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.istiaksaif.medops.Model.DaySelectModel;
import com.istiaksaif.medops.R;

import java.util.ArrayList;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.MyViewHolder> {

    private ArrayList<DaySelectModel> mModelList;
    private Context context;

    public DayAdapter(Context context, ArrayList<DaySelectModel> mModelList) {
        this.context = context;
        this.mModelList = mModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timecard, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.timeList.setText(mModelList.get(position).getText());
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        holder.timeList.setTextColor(context.getResources().getColor(R.color.dark));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mModelList.get(position).setSelected(!mModelList.get(position).isSelected());
                if(mModelList.get(position).isSelected()) {
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pink));
                    holder.timeList.setTextColor(context.getResources().getColor(R.color.white));
                }else {
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.timeList.setTextColor(context.getResources().getColor(R.color.dark));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView timeList;
        private CardView cardView;

        private MyViewHolder(View itemView) {
            super(itemView);
            timeList = itemView.findViewById(R.id.timetext);
            cardView = itemView.findViewById(R.id.timeCardView);
        }
    }

    public ArrayList<DaySelectModel> getSelected() {
        ArrayList<DaySelectModel> selected = new ArrayList<>();
        for (int i = 0; i < mModelList.size(); i++) {
            if (mModelList.get(i).isSelected()) {
                selected.add(mModelList.get(i));
            }
        }
        return selected;
    }
}
