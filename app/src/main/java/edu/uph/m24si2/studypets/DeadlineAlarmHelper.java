package edu.uph.m24si2.studypets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Helper untuk set dan cancel alarm deadline quest
public class DeadlineAlarmHelper {

    // Set alarm untuk quest — akan bunyi jam 8 pagi di tanggal deadline
    // deadline format: "yyyy-MM-dd"
    public static void setAlarm(Context context, int questId, String judulQuest, String deadline) {
        try {
            // Parse tanggal deadline
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date tanggal = sdf.parse(deadline);
            if (tanggal == null) return;

            // Set jam 8 pagi di tanggal deadline
            Calendar cal = Calendar.getInstance();
            cal.setTime(tanggal);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // Kalau waktunya sudah lewat hari ini, skip
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) return;

            // Buat intent ke DeadlineReceiver
            Intent intent = new Intent(context, DeadlineReceiver.class);
            intent.putExtra(DeadlineReceiver.EXTRA_JUDUL, judulQuest);
            intent.putExtra(DeadlineReceiver.EXTRA_ID, questId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    questId, // requestCode unik per quest
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            // Set alarm — gunakan setExactAndAllowWhileIdle agar akurat meski Doze mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        pendingIntent
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cancel alarm ketika quest dihapus
    public static void cancelAlarm(Context context, int questId) {
        Intent intent = new Intent(context, DeadlineReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                questId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
