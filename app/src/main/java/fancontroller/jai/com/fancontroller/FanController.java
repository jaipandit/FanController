package fancontroller.jai.com.fancontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

/**
 * The fan controller custom view.
 * Created by Jai Pandit on 1/18/18.
 */

public class FanController extends View {

    private static final String TAG = FanController.class.getSimpleName();

    private float viewWidth;
    private float viewHeight;
    private float dialRadius;
    private float dialCenterXPosition;
    private float dialCenterYPosition;

    private Paint textPen;
    private Paint dialPen;
    private Paint stopPen;
    private Paint linePen;

    private DialStop[] stops;

    public FanController(Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public FanController(Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FanController(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FanController(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Create pen to draw the text.
        textPen = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPen.setStyle(Paint.Style.FILL_AND_STROKE);
        textPen.setColor(Color.BLACK);
        textPen.setTextSize(40f);
        textPen.setTextAlign(Paint.Align.CENTER);

        // Create pen to draw dial.
        dialPen = new Paint(Paint.ANTI_ALIAS_FLAG);
        dialPen.setStyle(Paint.Style.FILL);
        dialPen.setColor(Color.GREEN);

        //Create pen to draw stops.
        stopPen = new Paint(Paint.ANTI_ALIAS_FLAG);
        stopPen.setColor(Color.RED);
        stopPen.setStyle(Paint.Style.FILL);

        // Create pen for line
        linePen = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePen.setColor(Color.GRAY);
        linePen.setStyle(Paint.Style.STROKE);
        //linePen.setStrokeWidth(12);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(dialCenterXPosition, dialCenterYPosition, dialRadius, dialPen);
        for (DialStop stop : stops) {
            canvas.drawText(Integer.toString(stop.number), stop.xPos, stop.yPos, stop.pen);
        }

        // Draw margins for my help.
        canvas.drawLine(dialCenterXPosition, 0, dialCenterXPosition, viewHeight, linePen);
        canvas.drawLine(0, dialCenterYPosition, viewWidth, dialCenterYPosition, linePen);
    }

    @Override
    public void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        Log.i(TAG, "old Width: " + oldWidth + " old height: " + oldHeight + " newWidth: " + newWidth + " newHeight: " + newHeight);
        viewWidth = newWidth;
        viewHeight = newHeight;

        dialCenterXPosition = viewWidth / 2;
        dialCenterYPosition = viewHeight / 2;
        dialRadius = (float) (Math.min(viewWidth, viewHeight) / 2 * 0.8);

        stops = createDialStops(12);
    }

    private static class DialStop {
        private final float xPos;
        private final float yPos;
        private final float radius;
        private final int number;
        private final Paint pen;

        DialStop(int number, float xPos, float yPos, float radius, Paint pen) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.radius = radius;
            this.number = number;
            this.pen = pen;
        }
    }

    private DialStop[] createDialStops(int numberOfStops) {
        DialStop[] myStops = new DialStop[numberOfStops];

        // we will come on this when we will study about negative angles.
        int maxAngle = 360;
        int angleOffset = maxAngle / numberOfStops;
        int nextAngle = angleOffset;
        for (int i = 0; i < myStops.length; i++) {
            Pair<Double, Double> resultPair = getCoordinate(dialCenterXPosition, dialCenterYPosition, (double) dialRadius, nextAngle);
            DialStop stop = new DialStop(i, resultPair.first.floatValue(), resultPair.second.floatValue(), 10, textPen);
            myStops[i] = stop;

            // prepare for next angle.
            nextAngle = nextAngle + angleOffset;
        }

        return myStops;
    }

    private Pair<Double, Double> getCoordinate(double xPos, double yPos, double radius, double angle) {
        double resultxPos = 0f;
        double resultyPos = 0f;

        double hypoteneus = radius;
        resultxPos = xPos + hypoteneus * (Math.cos(Math.toRadians(angle)));
        resultyPos = yPos - hypoteneus * (Math.sin(Math.toRadians(angle)));

        return new Pair<>(resultxPos, resultyPos);
    }
}
