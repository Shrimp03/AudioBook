package com.example.audiobook.message;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.audiobook.activities.MainActivity;
import com.example.audiobook.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "AudioBook_Notifications";
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Log để debug
        Log.d("FCM", "Message received at: " + System.currentTimeMillis());

        // Kiểm tra nếu thông báo bị tắt trong cài đặt
        if (!isNotificationsEnabled()) {
            Log.d("FCM", "Notifications are disabled in settings. Ignoring message.");
            return; // Không hiển thị thông báo nếu người dùng đã tắt
        }

        // Xử lý data payload
        Map<String, String> data = remoteMessage.getData();
        if (!data.isEmpty()) {
            Log.d("FCM", "Message data: " + data);

            // Kiểm tra thời gian gửi
            long sentTime = Long.parseLong(data.get("timestamp"));
            long delay = System.currentTimeMillis() - sentTime;
            Log.d("FCM", "Message delay: " + delay + "ms");
        }

        // Xử lý notification
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Hiển thị notification ngay lập tức
            showNotificationImmediately(title, body, data);
        }
    }

    /**
     * Kiểm tra xem người dùng đã bật thông báo hay chưa
     * @return true nếu thông báo được bật, false nếu đã tắt
     */
    private boolean isNotificationsEnabled() {
        SharedPreferences prefs = getSharedPreferences(
                "notifications_prefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("notification_enabled", true);
    }

    private void showNotificationImmediately(String title, String body, Map<String, String> data) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo notification channel với HIGH importance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Urgent Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Immediate notifications from AudioBook");
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Build notification với HIGH priority
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Thêm action khi click
        if (data.containsKey("click_action")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("notification_data", new HashMap<>(data));

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );
            builder.setContentIntent(pendingIntent);
        }

        notificationManager.notify(0, builder.build());
    }
}
