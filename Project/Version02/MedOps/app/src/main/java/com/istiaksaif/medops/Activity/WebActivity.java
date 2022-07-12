package com.istiaksaif.medops.Activity;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.istiaksaif.medops.R;
//
public class WebActivity extends AppCompatActivity {
//    private DatabaseReference databaseReference;
//
//    public WebView googleForm;
//    private Intent intent;
//    public String path;
//    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//    public void onCreate(Bundle savedInstanceState) {
//        WebActivity.super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_web);
//        Intent intent2 = getIntent();
//        this.intent = intent2;
//        this.path = intent2.getStringExtra("path");
//        this.databaseReference = FirebaseDatabase.getInstance().getReference();
//        WebView webView = (WebView) findViewById(R.C0338id.google_form);
//        this.googleForm = webView;
//        webView.setWebViewClient(new WebViewClient());
//        this.databaseReference.child("url").addListenerForSingleValueEvent(new ValueEventListener() {
//            public void onDataChange(DataSnapshot snapshot) {
//                WebActivity.this.googleForm.loadUrl(snapshot.child(WebActivity.this.path).getValue().toString());
//            }
//
//            public void onCancelled(DatabaseError error) {
//            }
//        });
//        this.googleForm.getSettings().setJavaScriptEnabled(true);
    }
//
//    public class webClient extends WebViewClient {
//        public webClient() {
//        }
//
//        public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
//            super.onPageStarted(webView, url, bitmap);
//        }
//
//        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
//            webView.loadUrl(url);
//            return true;
//        }
//    }
//
//    public void onBackPressed() {
//        if (this.googleForm.canGoBack()) {
//            this.googleForm.goBack();
//        } else {
//            WebActivity.super.onBackPressed();
//        }
//    }
//}