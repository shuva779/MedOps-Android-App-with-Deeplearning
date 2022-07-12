package com.istiaksaif.medops.Utils;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.istiaksaif.medops.Activity.IncomingCallActivity;
import com.istiaksaif.medops.Activity.OutGoingActivity;
import com.istiaksaif.medops.R;

import java.util.HashMap;

import okhttp3.internal.Util;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
        if(type!=null){
            if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                Intent intent = new Intent(getApplicationContext(), IncomingCallActivity.class);
                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE,remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
//                intent.putExtra("first_name",remoteMessage.getData().get("first_name"));
//                intent.putExtra("last_name",remoteMessage.getData().get("last_name"));
//                intent.putExtra("email",remoteMessage.getData().get("email"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s){
        super.onNewToken(s);
    }

    private void updateToken(String token){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("token");
        HashMap<String,Object> map = new HashMap<>();
        map.put("token",token);
        databaseReference.updateChildren(map);
    }

    private void createNormalNotification(String title,String message,String userId,String userImage){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1000");
        builder.setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_launcher_foreground);

        Intent intent = new Intent(this, OutGoingActivity.class);
        intent.putExtra("userId",userId);
    }
}
