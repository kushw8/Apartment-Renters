package com.example.rentalapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.rentalapp.ChatroomActivity;
import com.example.rentalapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

import static com.example.rentalapp.ChatroomActivity.isOpen;


public class CustomMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(!isOpen){
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> response = remoteMessage.getData();
        String user = response.get("user");
        String user_id = response.get("user_id");
        String message = response.get("message");
        String sender_fcm = response.get("sender_fcm");

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(getApplicationContext(), ChatroomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("id",user_id);
        intent.putExtra("fcm",sender_fcm);
        intent.putExtra("login",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),12,intent,0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"123")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(user)
                .setSound(defaultSound)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1,notificationBuilder.build());



    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.i("TAG","Token:"+s);
        SharedPreferences.Editor editor = getSharedPreferences("TOKEN",MODE_PRIVATE).edit();
        editor.putString("key",s);
        editor.apply();
    }
}
