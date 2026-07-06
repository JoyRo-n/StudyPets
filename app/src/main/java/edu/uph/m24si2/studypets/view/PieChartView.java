package edu.uph.m24si2.studypets.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom Pie Chart — tidak perlu library eksternal.
 * Gunakan setData(easy, medium, hard) sebelum invalidate().
 */
public class PieChartView extends View {

    private int easy = 0, medium = 0, hard = 0;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();

    private static final int COLOR_EASY   = 0xFF4CAF50;
    private static final int COLOR_MEDIUM = 0xFFFF9800;
    private static final int COLOR_HARD   = 0xFFF44336;
    private static final int COLOR_EMPTY  = 0xFFE0E0E0;

    public PieChartView(Context ctx) { super(ctx); init(); }
    public PieChartView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public PieChartView(Context ctx, AttributeSet a, int s) { super(ctx, a, s); init(); }

    private void init() {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(int easy, int medium, int hard) {
        this.easy = easy; this.medium = medium; this.hard = hard;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        int cx = w / 2, cy = h / 2;
        int radius = Math.min(cx, cy) - 20;

        oval.set(cx - radius, cy - radius, cx + radius, cy + radius);

        int total = easy + medium + hard;

        if (total == 0) {
            paint.setColor(COLOR_EMPTY);
            canvas.drawOval(oval, paint);
            textPaint.setColor(Color.GRAY);
            canvas.drawText("Belum ada data", cx, cy + 12, textPaint);
            return;
        }

        float startAngle = -90f;
        int[][] slices = {{easy, COLOR_EASY}, {medium, COLOR_MEDIUM}, {hard, COLOR_HARD}};
        String[] labels = {"E", "M", "H"};

        for (int i = 0; i < slices.length; i++) {
            if (slices[i][0] == 0) continue;
            float sweep = 360f * slices[i][0] / total;
            paint.setColor(slices[i][1]);
            canvas.drawArc(oval, startAngle, sweep, true, paint);

            // Label di tengah slice
            float midAngle = (float) Math.toRadians(startAngle + sweep / 2);
            float lx = cx + (radius * 0.6f) * (float) Math.cos(midAngle);
            float ly = cy + (radius * 0.6f) * (float) Math.sin(midAngle);
            textPaint.setColor(Color.WHITE);
            canvas.drawText(labels[i] + ":" + slices[i][0], lx, ly + 12, textPaint);

            startAngle += sweep;
        }

        // Lubang tengah (donut effect)
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius * 0.45f, paint);

        // Total di tengah
        textPaint.setColor(Color.parseColor("#7C4DFF"));
        textPaint.setTextSize(40f);
        canvas.drawText(String.valueOf(total), cx, cy + 14, textPaint);
        textPaint.setTextSize(28f);
        textPaint.setColor(Color.GRAY);
        canvas.drawText("total", cx, cy + 40, textPaint);
    }
}
