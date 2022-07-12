package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.Utils.ImageGetHelper;

import java.util.HashMap;

public class AskQusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView quesImg;
    private TextInputEditText ques,quesDes;

    private Button submitButton;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private ImageGetHelper getImageFunction;
    private ProgressDialog pro;
    private Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_qus);
        getImageFunction = new ImageGetHelper(null,this);
        pro = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("CommunityQA");
        storageReference = FirebaseStorage.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ques = findViewById(R.id.qus);
        quesDes = findViewById(R.id.qusdes);
        quesImg = findViewById(R.id.qusimage);
        quesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFunction.pickFromGallery();
            }
        });
        submitButton = findViewById(R.id.qusbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase(image);
                pro.show();
                pro.setContentView(R.layout.progress_dialog);
                pro.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
    }
    private void uploadToFirebase(Uri uri) {
        String quesId = databaseReference.child(uid).push().getKey();
        String QUES = ques.getText().toString().trim();
        String QUESDES = quesDes.getText().toString().trim();

        HashMap<String, Object> result = new HashMap<>();
        result.put("ques", QUES);
        result.put("quesDescription", QUESDES);
        result.put("userId", uid);
        result.put("quesId",quesId);
        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getContentResolver());
        databaseReference.child(quesId).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(uri == null){
                    Uri imguri = Uri.parse("");
                    HashMap<String, Object> resultimg = new HashMap<>();
                    resultimg.put("qaimage", imguri.toString());
                    databaseReference.child(quesId).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AskQusActivity.this, "Question Successful Submitted", Toast.LENGTH_SHORT).show();
                            pro.dismiss();
                            finish();
                        }
                    });
                }else
                if(uri != null){
                    fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> resultimg = new HashMap<>();
                                    resultimg.put("qaimage", uri.toString());
                                    databaseReference.child(quesId).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AskQusActivity.this, "Question Successful Submitted", Toast.LENGTH_SHORT).show();
                                            pro.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AskQusActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AskQusActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getImageFunction.IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            image = data.getData();
            quesImg.setImageURI(image);
            quesImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}