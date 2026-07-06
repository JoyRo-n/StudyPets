package edu.uph.m24si2.studypets.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom Bar Chart — 7 bar untuk 7 hari terakhir.
 */
public class BarChartView extends View {

    private int[] data = new int[7];
    private String[] labels = {"Sen","Sel","Rab","Kam","Jum","Sab","Min"};

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BarChartView(Context ctx) { super(ctx); init(); }
    public BarChartView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public BarChartView(Context ctx, AttributeSet a, int s) { super(ctx, a, s); init(); }

    private void init() {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(28f);
        textPaint.setColor(Color.GRAY);
        axisPaint.setColor(Color.LTGRAY);
        axisPaint.setStrokeWidth(2f);
    }

    /** data[0] = 6 hari lalu ... data[6] = hari ini */
    public void setData(int[] data, String[] labels) {
        this.data   = data;
        this.labels = labels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        float padL = 20f, padR = 20f, padT = 20f, padB = 40f;
        float chartH = h - padT - padB;
        float chartW = w - padL - padR;

        // Garis sumbu X
        canvas.drawLine(padL, h - padB, w - padR, h - padB, axisPaint);

        // Max value
        int max = 1;
        for (int v : data) if (v > max) max = v;

        int n = data.length;
        float barW = chartW / (n * 2f);

        for (int i = 0; i < n; i++) {
            float x = padL + (i * 2 + 1) * barW;
            float barH = data[i] == 0 ? 0 : (float) data[i] / max * chartH * 0.85f;
            float top  = h - padB - barH;

            // Warna gradasi dari ungu ke pink
            barPaint.setColor(i == n - 1 ? 0xFF7C4DFF : 0xFFAA80FF);
            RectF rect = new RectF(x - barW * 0.7f, top, x + barW * 0.7f, h - padB);
            canvas.drawRoundRect(rect, 8f, 8f, barPaint);

            // Nilai di atas bar
            if (data[i] > 0) {
                textPaint.setColor(Color.parseColor("#7C4DFF"));
                textPaint.setTextSize(26f);
                canvas.drawText(String.valueOf(data[i]), x, top - 6, textPaint);
            }

            // Label hari
            textPaint.setColor(Color.GRAY);
            textPaint.setTextSize(24f);
            canvas.drawText(labels[i], x, h - padB + 30f, textPaint);
        }
    }
}
