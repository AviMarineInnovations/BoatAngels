package in.avimarine.boatangels.general;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.activities.MainActivity;
import java.util.Random;

/**
 * Created by Motim on 3/12/2018.
 */

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

  private static final String TAG = "MessagingService";
  public static final int ID_SMALL_NOTIFICATION = 235;

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    Log.d(TAG, "From: " + remoteMessage.getFrom());
    Log.d(TAG, "Token: " + FirebaseInstanceId.getInstance().getToken());

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data payload: " + remoteMessage.getData());
      String msg = remoteMessage.getData().get("msg");
      String title = remoteMessage.getData().get("title");
      String InspectUuid = remoteMessage.getData().get("InspectionUid");
      Log.d(TAG,"InspecUidTest: "+ InspectUuid);
      sendNotification(msg, title);
    }

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG,"Message Notification Title" + remoteMessage.getNotification().getTitle());
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
  }

  private void sendNotification(String messageBody, String title) {
    Random rand = new Random();
    int n = rand.nextInt(10000);

    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, ID_SMALL_NOTIFICATION+n, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    String channelId = "fcm_default_channel";
    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (notificationManager != null) {
      notificationManager.notify(ID_SMALL_NOTIFICATION+n, notificationBuilder.build());
    }

  }

}