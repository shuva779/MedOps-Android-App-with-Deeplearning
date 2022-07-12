package com.istiaksaif.medops.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.istiaksaif.medops.Model.User;
import com.istiaksaif.medops.R;

public class RegistrationActivity extends AppCompatActivity {

    private TextView signin;
    private TextInputEditText fullName,email,nid,password,passwordRepeat;
    private Button registrationButton;
    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        fullName = findViewById(R.id.name);
        email = findViewById(R.id.eamil);
        nid = findViewById(R.id.nid);
        password = findViewById(R.id.pass);
        passwordRepeat = findViewById(R.id.passretype);
        registrationButton = findViewById(R.id.reg_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
                }
        });

        //intent Login page
        signin = findViewById(R.id.signinactivity);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void Register() {
        String FullName = fullName.getText().toString();
        String Email = email.getText().toString();
        String NID = nid.getText().toString();
        String Password = password.getText().toString();
        String Password_re = passwordRepeat.getText().toString();

        if (TextUtils.isEmpty(FullName)){
            Toast.makeText(RegistrationActivity.this, "please enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Email)){
            Toast.makeText(RegistrationActivity.this, "please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(NID)){
            Toast.makeText(RegistrationActivity.this, "please enter NID", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (NID.length()<13){
            Toast.makeText(RegistrationActivity.this, "Nid number minimum 13 and max 18 numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password)){
            Toast.makeText(RegistrationActivity.this, "please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password_re)){
            Toast.makeText(RegistrationActivity.this, "please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!Password.equals(Password_re)){
            passwordRepeat.setError("password not match");
            return;
        }
        else if (Password.length()<8){
            Toast.makeText(RegistrationActivity.this, "password week & password length at least 8 character", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!isValidEmail(Email)){
            email.setError("Invalid email");
            return;
        }
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            User userhelp = new User(FullName,Email,null,"User","","","","",NID,uid,"offline");
                            databaseReference.child(uid).setValue(userhelp);
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, UserHomeActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Authentication Failed "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}