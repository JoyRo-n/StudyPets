package edu.uph.m24si2.studypets;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import edu.uph.m24si2.studypets.database.RoomHelper;

// Application class — dipanggil pertama kali sebelum Activity apapun dibuka
public class StudyPetsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inisialisasi RoomHelper sekali saja di sini
        RoomHelper.init(this);

        // Apply dark mode sesuai preferensi yang tersimpan
        SharedPreferences prefs = getSharedPreferences("studypets_settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
            isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
