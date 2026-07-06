package edu.uph.m24si2.studypets;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

// BroadcastReceiver — dipanggil oleh AlarmManager saat deadline tiba
public class DeadlineReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID   = "quest_deadline_channel";
    public static final String EXTRA_JUDUL  = "quest_judul";
    public static final String EXTRA_ID     = "quest_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        String judulQuest = intent.getStringExtra(EXTRA_JUDUL);
        int    questId    = intent.getIntExtra(EXTRA_ID, 0);

        if (judulQuest == null) judulQuest = "Quest";

        // Buat notification channel (wajib untuk Android 8+)
        buatChannel(context);

        // Intent untuk buka QuestActivity saat notifikasi diklik
        Intent bukaApp = new Intent(context, QuestActivity.class);
        bukaApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, questId, bukaApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Bangun notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("⏰ Deadline Quest Hari Ini!")
                .setContentText("\"" + judulQuest + "\" harus diselesaikan hari ini!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Quest \"" + judulQuest + "\" deadline hari ini!\nJangan lupa selesaikan ya! 🎯"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(questId, builder.build());
        }
    }

    // Buat notification channel — wajib Android Oreo (API 26) ke atas
    private void buatChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Quest Deadline",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi pengingat deadline quest");
            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
