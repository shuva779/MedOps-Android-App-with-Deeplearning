package com.istiaksaif.medops.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.istiaksaif.medops.Adapter.TabViewPagerAdapter;
import com.istiaksaif.medops.Fragment.CommunityFragment;
import com.istiaksaif.medops.Fragment.DoctorsListFragment;
import com.istiaksaif.medops.Fragment.ProfileFragment;
import com.istiaksaif.medops.Fragment.UserHomeFragment;
import com.istiaksaif.medops.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Istiak Saif on 14/07/21.
 */
public class UserHomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager tabviewPager;
    private Toolbar toolbar;

    private long backPressedTime;
    private DrawerLayout drawerLayout;
    private LottieAnimationView cross;
    private CardView cardView;
    private LinearLayout logoutButton;

    private TextView appVersion;

    private GoogleSignInClient googleSignInClient;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private LottieAnimationView coinDrop,rewardOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        tabLayout = (TabLayout)findViewById(R.id.tab);
        tabviewPager = (ViewPager)findViewById(R.id.tabviewpager);
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabViewPagerAdapter.AddFragment(new ProfileFragment(),null);
        tabViewPagerAdapter.AddFragment(new CommunityFragment(),null);
        tabViewPagerAdapter.AddFragment(new DoctorsListFragment(),null);
        tabViewPagerAdapter.AddFragment(new UserHomeFragment(),null);
        tabviewPager.setAdapter(tabViewPagerAdapter);
        tabviewPager.setCurrentItem(3);
        tabLayout.setupWithViewPager(tabviewPager);


        tabLayout.getTabAt(0).setIcon(R.drawable.profile);
        tabLayout.getTabAt(3).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.community_chat);
        tabLayout.getTabAt(2).setIcon(R.drawable.doctor);

        checkReward();
        //appDrawer

        drawerLayout = findViewById(R.id.drawer_layout);
        cardView = findViewById(R.id.drawercard);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.menu);

        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                    cross = (LottieAnimationView)findViewById(R.id.cross);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawerLayout.closeDrawer(GravityCompat.END);
                        }
                    });
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        //drawerMenus

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.logout:
                        signOut();
                        break;
                }
            }
        });

        //version on menu
        appVersion = findViewById(R.id.app_version);
        PackageManager manager = getApplication().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getApplication().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        appVersion.setText("Version "+version);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("offline");
    }

    private void checkStatus(String status){
        HashMap<String, Object> result = new HashMap<>();
        result.put("status",status);

        databaseReference.child("users").child(uid).updateChildren(result);
    }

    private void giveReward(String token){
        try {
            HashMap<String, Object> result = new HashMap<>();
            result.put("userId",uid);
            result.put("phoneToken",token);

            databaseReference.child("Reward").child(token).updateChildren(result);
            databaseReference.child("Reward").child(uid).updateChildren(result);
            databaseReference.child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String k = snapshot.child("key").getValue().toString();
                            databaseReference.child("usersData").child(k).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                int balance = 0;
                                                String s = snapshot.child("balanceTk").getValue(String.class);
                                                balance = (Integer.parseInt(s.trim()))+(1000);

                                                HashMap<String, Object> result1 = new HashMap<>();
                                                result1.put("balanceTk", String.valueOf(balance));
                                                databaseReference.child("usersData").
                                                        child(snapshot.getKey()).updateChildren(result1);

                                            }catch (Exception e){

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(UserHomeActivity.this,
                                                    "Something Wrong ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 5000);
        }catch (Exception e){

        }
    }

    private void checkReward(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();
                databaseReference.child("Reward").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child(token).exists()){
                            if(!dataSnapshot.child(uid).exists()){
                                try {
                                    dialogBuilder = new AlertDialog.Builder(UserHomeActivity.this);
                                    final View contactPopupView = getLayoutInflater().inflate(R.layout.reward,null);
                                    coinDrop = (LottieAnimationView)contactPopupView. findViewById(R.id.coindrop);

                                    coinDrop.setVisibility(View.VISIBLE);
                                    coinDrop.setAnimation(R.raw.coinsdrop);
                                    coinDrop.loop(false);
                                    coinDrop.playAnimation();

                                    dialogBuilder.setView(contactPopupView);
                                    dialog = dialogBuilder.create();
                                    dialog.show();
                                    giveReward(token);
                                }catch (Exception e){

                                }
                            }else if(dataSnapshot.child(uid).exists()){
                                try {
                                    HashMap<String, Object> result = new HashMap<>();
                                    result.put("userId",uid);
                                    result.put("phoneToken",token);
                                    databaseReference.child("Reward").child(token).updateChildren(result);
                                }catch (Exception e){

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }return true;
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
        Intent intent1 = new Intent(this, LogInActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }

    public void onBackPressed(){
        if(backPressedTime + 2000>System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(getBaseContext(),"Press Back Again to Exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}