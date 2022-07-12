package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.medops.R;

import java.util.HashMap;

public class AddMoneyActivity extends AppCompatActivity {

    private TextInputEditText amount,number,trxId;
    private MaterialAutoCompleteTextView accountType;
    private Button verifyButton;
    private Toolbar toolBar;


    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ProgressDialog progressDialog;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitleTextColor(R.color.dark);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("My Wallet");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("verifyPayment");

        amount = findViewById(R.id.tk);
        number = findViewById(R.id.number);
        trxId = findViewById(R.id.trxId);
        accountType = findViewById(R.id.accountType);
        TextInputLayout textInputLayoutAccount = findViewById(R.id.accountTypeLayout);
        String []optionUniName = {"Bkash","Nagad"};
        ArrayAdapter<String> arrayAdapterUni = new ArrayAdapter<>(this,R.layout.usertype_item,optionUniName);
        ((MaterialAutoCompleteTextView) textInputLayoutAccount.getEditText()).setAdapter(arrayAdapterUni);

        verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                HashMap<String, Object> result = new HashMap<>();
                result.put("amountTk", amount.getText().toString());
                result.put("accountNumber", number.getText().toString());
                result.put("trxId", trxId.getText().toString());
                result.put("accountType", accountType.getText().toString());
                result.put("userId", uid);

                String q = databaseReference.push().getKey();
                databaseReference.child(q).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something Wrong Try Again", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }
}