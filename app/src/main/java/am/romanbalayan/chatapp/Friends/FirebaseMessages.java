package am.romanbalayan.chatapp.Friends;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import am.romanbalayan.chatapp.R;


public class FirebaseMessages extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "1";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String id = remoteMessage.getData().get("from_Id");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody());

        String clickAction = remoteMessage.getNotification().getClickAction();
        Intent intent = new Intent(clickAction);
        intent.putExtra("id", id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}
