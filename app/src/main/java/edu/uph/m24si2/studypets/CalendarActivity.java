package edu.uph.m24si2.studypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.uph.m24si2.studypets.adapter.QuestAdapter;
import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.Quest;
import edu.uph.m24si2.studypets.model.QuestData;

// CalendarActivity = halaman kalender quest
public class CalendarActivity extends AppCompatActivity {

    RoomHelper db;
    CalendarView calendarView;
    TextView tvTanggalDipilih;
    RecyclerView rvQuests;
    QuestAdapter adapter;
    String tanggalDipilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = RoomHelper.getOrInit(this);

        calendarView       = findViewById(R.id.calendar_view);
        tvTanggalDipilih   = findViewById(R.id.tv_selected_date);
        rvQuests           = findViewById(R.id.rv_calendar_quests);

        rvQuests.setLayoutManager(new LinearLayoutManager(this));
        // Mode read-only — null = tidak ada tombol complete/delete
        adapter = new QuestAdapter(this, new ArrayList<>(), null, null);
        rvQuests.setAdapter(adapter);

        // Default tampilkan quest hari ini
        tanggalDipilih = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadData();

        // Ketika user klik tanggal di kalender
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int tahun, int bulan, int hari) {
                tanggalDipilih = String.format(Locale.getDefault(),
                        "%04d-%02d-%02d", tahun, bulan + 1, hari);
                loadData();
            }
        });

        Button btnBack = findViewById(R.id.btn_back_calendar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    // loadData() — filter quest berdasarkan tanggal yang dipilih
    private void loadData() {
        String hariIni = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Calendar cal   = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        String besok   = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        String label;
        if (tanggalDipilih.equals(hariIni))              label = "🔴 Hari Ini";
        else if (tanggalDipilih.equals(besok))           label = "🟡 Besok";
        else if (tanggalDipilih.compareTo(hariIni) > 0) label = "🟢 Masih Lama";
        else                                             label = "⚪ Sudah Lewat";

        tvTanggalDipilih.setText("📋 Quest deadline " + tanggalDipilih + "  " + label);

        // Ambil semua quest, filter yang deadlinenya sesuai tanggal
        List<QuestData> semua = db.getAllQuests();
        List<Quest> hasil = new ArrayList<>();
        for (QuestData q : semua) {
            if (q.deadline != null && q.deadline.startsWith(tanggalDipilih)) {
                hasil.add(new Quest(q.id, q.title, q.description, q.subject,
                        q.difficulty, q.xpReward, q.coinReward, q.status, q.deadline, q.isFromAdmin, q.buktiPath));
            }
        }

        // setData() — pola dari materi dosen
        adapter.setData(hasil);
    }
}
