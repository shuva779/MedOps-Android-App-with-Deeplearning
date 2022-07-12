package com.istiaksaif.medops.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.medops.Model.DoctorItem;
import com.istiaksaif.medops.Model.User;
import com.istiaksaif.medops.R;

import java.util.HashMap;


public class AdminHomeFragment extends Fragment {

    private TextInputEditText fullName,nid,email,bmdcId;
    private MaterialAutoCompleteTextView userType;
    private Button Invite;

    private DatabaseReference doctorDatabaseRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseAuth firebaseAuth;


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doctorDatabaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        fullName = view.findViewById(R.id.name);
        nid = view.findViewById(R.id.nid);
        email = view.findViewById(R.id.email);
        bmdcId = view.findViewById(R.id.bmdcid);
        userType = view.findViewById(R.id.type);
        TextInputLayout textInputLayoutuserType = view.findViewById(R.id.userType);
        String []optionUniName = {"Doctor","Nurse"};
        ArrayAdapter<String> arrayAdapterUni = new ArrayAdapter<>(getActivity(),R.layout.usertype_item,optionUniName);
        ((MaterialAutoCompleteTextView) textInputLayoutuserType.getEditText()).setAdapter(arrayAdapterUni);

        Invite = view.findViewById(R.id.next_button);
        Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info();
            }
        });
    }

    private void Info() {
        String FullName = fullName.getText().toString();
        String NID = nid.getText().toString();
        String USERTYPE = userType.getText().toString();
        String Email = email.getText().toString();
        String BMDCID = bmdcId.getText().toString();
        String passWord = "Doctor123";

        if (TextUtils.isEmpty(FullName)){
            Toast.makeText(getActivity(), "please enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(USERTYPE)){
            Toast.makeText(getActivity(), "please Select User", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(NID)){
            Toast.makeText(getActivity(), "please enter NID", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (NID.length()<13 ){
            Toast.makeText(getActivity(), "Nid number minimum 13 and max 18 numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Email)){
            Toast.makeText(getActivity(), "please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(BMDCID)){
            Toast.makeText(getActivity(), "please enter BMDCID", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(Email,passWord).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    u.sendEmailVerification();
                    String doctorId = u.getUid();
                    String imageUrl="https://firebasestorage.googleapis.com/v0/b/medops-covid19-detection.appspot.com/o/doctor.jpg?alt=media&token=8d6bbcc9-6afe-418f-8758-e3cfe2278437";
                    String key = doctorDatabaseRef.push().getKey();

                    DoctorItem doctorHelp = new DoctorItem(FullName,Email,"","","",
                            USERTYPE,imageUrl,NID,doctorId,"0","unverified"
                            ,BMDCID,"","","","", ""
                            ,"","","");
                    doctorDatabaseRef.child("usersData").child(key).setValue(doctorHelp);
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("userId", doctorId);
                    result.put("key", key);

                    doctorDatabaseRef.child("users").child(doctorId).setValue(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fullName.setText("");
                                    nid.setText("");
                                    userType.setText("");
                                    email.setText("");
                                    bmdcId.setText("");
                                    Toast.makeText(getActivity(),"Invitation Done",Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    getActivity().finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error ", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Authentication Failed "+task.getException()
                            .toString(), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        return view;
    }

}