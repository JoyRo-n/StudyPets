package edu.uph.m24si2.studypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.UserStats;
import edu.uph.m24si2.studypets.view.BarChartView;
import edu.uph.m24si2.studypets.view.PieChartView;

// StatsActivity = halaman statistik belajar
public class StatsActivity extends AppCompatActivity {

    RoomHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        db = RoomHelper.getOrInit(this);

        loadData();

        Button btnBack = findViewById(R.id.btn_back_stats);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    // loadData() — ambil data statistik dari db dan tampilkan
    private void loadData() {
        // Pie chart — quest per kesulitan
        int easy   = db.getQuestCountByDifficulty("Easy");
        int medium = db.getQuestCountByDifficulty("Medium");
        int hard   = db.getQuestCountByDifficulty("Hard");

        PieChartView pieChart = findViewById(R.id.pie_chart);
        pieChart.setData(easy, medium, hard);

        ((TextView) findViewById(R.id.tv_easy_count)).setText("Easy: " + easy);
        ((TextView) findViewById(R.id.tv_medium_count)).setText("Medium: " + medium);
        ((TextView) findViewById(R.id.tv_hard_count)).setText("Hard: " + hard);

        // Bar chart — quest per hari (7 hari terakhir)
        int[] dataPerHari = db.getQuestPerWeek();
        String[] namaHari  = {"Min","Sen","Sel","Rab","Kam","Jum","Sab"};
        String[] labelHari = new String[7];
        Calendar kalender  = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            Calendar d = (Calendar) kalender.clone();
            d.add(Calendar.DATE, -(6 - i));
            labelHari[i] = namaHari[d.get(Calendar.DAY_OF_WEEK) - 1];
        }

        BarChartView barChart = findViewById(R.id.bar_chart);
        barChart.setData(dataPerHari, labelHari);

        // Ringkasan XP dan total quest
        ((TextView) findViewById(R.id.tv_total_xp))
                .setText(String.valueOf(db.getCurrentTotalXP()));

        UserStats u = db.getUserStats();
        if (u != null) {
            ((TextView) findViewById(R.id.tv_total_quests))
                    .setText(String.valueOf(u.totalQuestsCompleted));
        }
    }
}
