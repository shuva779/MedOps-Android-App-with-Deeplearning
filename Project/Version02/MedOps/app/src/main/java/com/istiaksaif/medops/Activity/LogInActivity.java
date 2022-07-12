package com.istiaksaif.medops.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.istiaksaif.medops.Model.User;
import com.istiaksaif.medops.R;

import java.util.HashMap;


public class LogInActivity extends AppCompatActivity {

    private TextInputEditText email,password,popup_email;
    private Button logInButton,forgotButton;
    private TextView signup;
    private MaterialTextView forgotpassword;
    private GoogleSignInClient googleSignInClient;
    private FloatingActionButton signgoogle;
    private static  int RC_SIGN_IN =2;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private static final String TAG = "Authentication";
    private FirebaseAuth.AuthStateListener authStateListener;
    float v=0;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private LottieAnimationView cross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //signin by email
        email = findViewById(R.id.emaillogin);
        password = findViewById(R.id.passlogin);
        logInButton = findViewById(R.id.login_btn);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();


                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(LogInActivity.this, "please enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(LogInActivity.this, "please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(
                        LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgress();
                            if (task.isSuccessful()) {
                                checkUserInfo();
                            } else {
                                Toast.makeText(LogInActivity.this, task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LogInActivity.this, LogInActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        }
                    });
                }
        });

        //signin by google part
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        signgoogle = findViewById(R.id.sign_google);
        signgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null){
                   pushData();
                }
                else {
                }
            }
        };


        //intent registration activity
        signup =(TextView) findViewById(R.id.registeractivity);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //forgot_password
        forgotpassword = findViewById(R.id.forgotpass);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createpopupDiaglog();
            }
        });


        //animation part
        signgoogle.setTranslationY(300);
        signgoogle.setAlpha(v);

        signgoogle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

    }

    private  void collectToken(){
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    return;
                }
                String token = task.getResult();
                databaseReference.child("users").child(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                databaseReference.child("users").child(snapshot.getKey())
                                        .child("token")
                                        .setValue(token);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }
    private void checkUserInfo() {
        showProgress();
        FirebaseUser user = mAuth.getCurrentUser();
        collectToken();
        Query query = databaseReference.child("users").child(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.child("key").getValue().toString();
                    databaseReference.child("usersData").child(key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child("nid").getValue(String.class).equals("")) {
                                    progressDialog.dismiss();
                                    updateUI(user);
                                    finish();
                                } else {
                                    if (snapshot.child("isUser").getValue(String.class).equals("User")) {
                                        Intent intent = new Intent(LogInActivity.this, UserHomeActivity.class);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                    if (snapshot.child("isUser").getValue(String.class).equals("Doctor")) {
                                        if (user.isEmailVerified()) {
                                            HashMap<String, Object> result = new HashMap<>();
                                            result.put("verifyStatus", "verified");
                                            databaseReference.child("usersData").child(snapshot.getKey()).updateChildren(result);
                                            Intent intent = new Intent(LogInActivity.this, DoctorHomeActivity.class);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                            finish();
                                        } else {
                                            user.sendEmailVerification();
                                            Toast.makeText(LogInActivity.this, "Check your email to " +
                                                    "verify your account", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }
                                    if (snapshot.child("isUser").getValue(String.class).equals("Nurse")) {
                                        Intent intent = new Intent(LogInActivity.this, UserHomeActivity.class);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                    if (snapshot.child("isUser").getValue(String.class).equals("Admin")) {
                                        Intent intent = new Intent(LogInActivity.this, AdminManagerHomeActivity.class);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    protected void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            checkUserInfo();
        }
    }

    private void signIn() {
        Intent signInintent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInintent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d("Sign in failed", e.toString());
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()) {
                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                pushData();
                            }else{
                                checkUserInfo();
                            }
                        } else {
                            Toast.makeText(LogInActivity.this,"Login Failed"
                                    +task.getException().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LogInActivity.this, LogInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void pushData() {
        showProgress();
        String key = databaseReference.push().getKey();
        FirebaseUser user = mAuth.getCurrentUser();
        String pname = user.getDisplayName();
        String pEmail = user.getEmail();
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", user.getUid());
        result.put("key", key);
        databaseReference.child("users").child(user.getUid()).setValue(result);
        User userhelp = new User(pname,pEmail,"","User","","",
                "","",user.getUid(),"","0","","",key);
        databaseReference.child("usersData").child(key).setValue(userhelp);
        collectToken();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user){
        Intent intent = new Intent(LogInActivity.this,checkActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    public void createpopupDiaglog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup,null);
        popup_email = (TextInputEditText)contactPopupView.findViewById(R.id.emailforgot);
        forgotButton = (Button) contactPopupView.findViewById(R.id.forgot_btn);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpassword();
            }
        });

        cross = (LottieAnimationView) contactPopupView.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void forgotpassword(){
        if(popup_email.getText().toString().equals("")){
            popup_email.setError("please fill");
        }
        else {
            showProgress();
            mAuth.sendPasswordResetEmail(popup_email.getText().toString()).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     progressDialog.dismiss();
                     Toast.makeText(LogInActivity.this, "please check your email and " +
                             "reset password", Toast.LENGTH_SHORT).show();
                     dialog.dismiss();
                 }
                 else{
                     progressDialog.dismiss();
                     Toast.makeText(LogInActivity.this, "Unsuccessful"+task.getException()
                             .toString(), Toast.LENGTH_SHORT).show();
                     dialog.dismiss();
                 }
              }
          });
        }
    }
    private void showProgress(){
        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}