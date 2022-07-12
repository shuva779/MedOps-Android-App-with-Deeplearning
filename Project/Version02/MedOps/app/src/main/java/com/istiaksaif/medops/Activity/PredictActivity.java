package com.istiaksaif.medops.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.istiaksaif.medops.R;
import com.istiaksaif.medops.ml.MedOps;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;

public class PredictActivity extends AppCompatActivity {

    Toolbar toolbar;

    private TextView predictResult,predictResultPercent,confidence,confidencePercent,confidence1,confidencePercent1,
            confidence2,confidencePercent2;
    private Bitmap img;
    private ImageView predictImg, savedOnCloud;

    private static final float IMAGE_STD= 1.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Intent intent;
    private String intentImage;
    private LottieAnimationView loading;
    private LinearLayout l0,l1,l2;

    int delay = 1000;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);
        intent = getIntent();
        intentImage = intent.getStringExtra("image");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_transparent);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        predictResult = findViewById(R.id.predictResult);
        predictResultPercent = findViewById(R.id.predictResultPercent);
        predictImg = findViewById(R.id.predictimg);
        confidence = findViewById(R.id.confidence);
        confidencePercent = findViewById(R.id.percent);
        confidence1 = findViewById(R.id.confidence1);
        confidencePercent1 = findViewById(R.id.percent1);
        confidence2 = findViewById(R.id.confidence2);
        confidencePercent2 = findViewById(R.id.percent2);
        l0 = findViewById(R.id.l1);
        l1 = findViewById(R.id.l2);
        l2 = findViewById(R.id.l3);

        Uri image = Uri.parse(intentImage);
        predictImg.setImageURI(image);
        loading = (LottieAnimationView) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        loading.setAnimation(R.raw.loading);
        loading.loop(true);
        loading.playAnimation();

        databaseReference = FirebaseDatabase.getInstance().getReference("PredictionResult");
        storageReference = FirebaseStorage.getInstance().getReference();

        savedOnCloud =findViewById(R.id.savedOnCloud);
        savedOnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                predictSaveOnCloud();
                showProgress();

            }
        });
    }

    public void classifyImage(Bitmap image){
        try {
            MedOps model = MedOps.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[224 * 224];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < 224; i++){
                for(int j = 0; j < 224; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (IMAGE_STD / PROBABILITY_STD));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (IMAGE_STD / PROBABILITY_STD));
                    byteBuffer.putFloat((val & 0xFF) * (IMAGE_STD / PROBABILITY_STD));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            MedOps.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Covid 19", "Normal Case", "Pneumonia"};
            predictResult.setVisibility(View.VISIBLE);
            predictResult.setText(classes[maxPos]);
            predictResultPercent.setText(String.format("%.1f%%", confidences[maxPos] * 100));

            l0.setVisibility(View.VISIBLE);
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);

            String s = "l"+maxPos;
            if (s.equals("l0")) {
                l0.setBackground(getDrawable(R.color.pink));
                confidence.setTextColor(getResources().getColor(R.color.white));
                confidencePercent.setTextColor(getResources().getColor(R.color.white));
            }else if (s.equals("l1")) {
                l1.setBackground(getDrawable(R.color.green));
                confidence1.setTextColor(getResources().getColor(R.color.white));
                confidencePercent1.setTextColor(getResources().getColor(R.color.white));
            }else if (s.equals("l2")) {
                l2.setBackground(getDrawable(R.color.pink));
                confidence2.setTextColor(getResources().getColor(R.color.white));
                confidencePercent2.setTextColor(getResources().getColor(R.color.white));
            }

            confidence.setText(classes[0]);
            confidencePercent.setText(String.format("%.1f%%", confidences[0] * 100));
            confidence1.setText(classes[1]);
            confidencePercent1.setText(String.format("%.1f%%", confidences[1] * 100));
            confidence2.setText(classes[2]);
            confidencePercent2.setText(String.format("%.1f%%", confidences[2] * 100));
            loading.setVisibility(View.GONE);

            model.close();
        } catch (IOException e) {

        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Uri image = Uri.parse(intentImage);
                try {
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                    img = Bitmap.createScaledBitmap(img, 224, 224, false);
                    classifyImage(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, delay);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Uri image = Uri.parse(intentImage);
//                try {
//                    img = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
//                    img = Bitmap.createScaledBitmap(img, 224, 224, false);
//                    classifyImage(img);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, delay);
//    }

    private void predictSaveOnCloud(){
        Uri image = Uri.parse(intentImage);
        String predictId = databaseReference.child(uid).push().getKey();
        String PredictCase = predictResult.getText().toString().trim();
        String PredictPercent = predictResultPercent.getText().toString().trim();

        HashMap<String, Object> result = new HashMap<>();
        result.put("predictCase", PredictCase);
        result.put("predictPercent", PredictPercent);
        result.put("userId",uid);
        result.put("predictId",predictId);

        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + this.getContentResolver());
        databaseReference.child(predictId).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fileRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String, Object> resultimg = new HashMap<>();
                                resultimg.put("image", uri.toString());
                                databaseReference.child(predictId).updateChildren(resultimg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PredictActivity.this, "your prediction Successfully saved on cloud", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
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
                        Toast.makeText(PredictActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PredictActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showProgress(){
        progressDialog = new ProgressDialog(PredictActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void onBackPressed(){
        finish();
    }
}