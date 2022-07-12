package com.istiaksaif.medops.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.ImageGetHelper;

import static android.app.Activity.RESULT_OK;

public class UserHomeFragment extends Fragment {

    private ImageGetHelper getImageFunction;
    private LinearLayout takeImageCard;
    private TextView takeImageButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getImageFunction = new ImageGetHelper(this,null);

        takeImageCard = view.findViewById(R.id.takeimgcard);
        takeImageButton = view.findViewById(R.id.takeimgbutton);

        takeImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFunction.showImagePicDialog();
            }
        });
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        return view;
    }
}