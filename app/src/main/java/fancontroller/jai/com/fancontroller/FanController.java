package fancontroller.jai.com.fancontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
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
    private static final int START_ROTATION = 1;
    private static final int STOP_ROTATION = 2;
    private static final long INTERVAL = 30;

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
    private Handler uiHandler;


    private boolean isRotationStarted = false;

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

        uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case START_ROTATION:
                        isRotationStarted = true;
                        Log.i(TAG, "Rotation Started");
                        rotateArrayIndexes();

                        // redraw.
                        FanController.this.invalidate();

                        Log.i(TAG, "Message processed.");
                        uiHandler.sendMessageDelayed(uiHandler.obtainMessage(START_ROTATION), INTERVAL);
                        return true;
                    case STOP_ROTATION:
                        isRotationStarted = false;
                        Log.i(TAG, "Rotation stopped");
                        uiHandler.removeMessages(START_ROTATION);
                        return true;
                    default:
                        return false;
                }
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRotationStarted) {
                    uiHandler.sendMessageAtFrontOfQueue(uiHandler.obtainMessage(STOP_ROTATION));
                } else {
                    uiHandler.sendEmptyMessage(START_ROTATION);
                }
                return false;
            }
        });
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, "Redrawing.");
        /*if (dialPen.getColor() == Color.GREEN) {
            dialPen.setColor(Color.RED);
        } else {
            dialPen.setColor(Color.GREEN);
        }*/
        canvas.drawCircle(dialCenterXPosition, dialCenterYPosition, dialRadius, dialPen);

        for (int i = 0; i < stops.length; i++) {
            DialStop stop = stops[i];
            //Log.i(TAG, "Drawing number")
            canvas.drawCircle(stop.xPos, stop.yPos, stop.radius, stop.pen);
        }

        // Draw margins for my help.
        //canvas.drawLine(dialCenterXPosition, 0, dialCenterXPosition, viewHeight, linePen);
        //canvas.drawLine(0, dialCenterYPosition, viewWidth, dialCenterYPosition, linePen);

    }

    @Override
    public void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        Log.i(TAG, "old Width: " + oldWidth + " old height: " + oldHeight + " newWidth: " + newWidth + " newHeight: " + newHeight);
        viewWidth = newWidth;
        viewHeight = newHeight;

        dialCenterXPosition = viewWidth / 2;
        dialCenterYPosition = viewHeight / 2;
        dialRadius = (float) (Math.min(viewWidth, viewHeight) / 2 * 0.8);

        createDialStops(12);
    }

    private static class DialStop {
        private final float xPos;
        private final float yPos;
        private float radius;
        private final float baseRadius;
        private int number;
        private Paint pen;

        DialStop(int number, float xPos, float yPos, float radius) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.radius = radius;
            this.baseRadius = radius;

            //Create pen to draw stops.
            pen = new Paint(Paint.ANTI_ALIAS_FLAG);
            pen.setColor(Color.RED);
            pen.setStyle(Paint.Style.FILL);

            setNumber(number);
        }

        @Override
        public String toString() {
            return Integer.toString(number);
        }

        public void setNumber(int color) {
            this.number = color;
            if (color == Color.RED) {
                radius = radius + 20;
            } else {
                radius = baseRadius;
            }
            pen.setColor(number);
        }
    }

    private void createDialStops(int numberOfStops) {
        stops = new DialStop[numberOfStops];

        int maxAngle = 360;
        int angleOffset = maxAngle / numberOfStops;
        int nextAngle = 0;


        for (int i = 0; i < numberOfStops; i++) {
            Pair<Double, Double> resultPair = getCoordinate(dialCenterXPosition, dialCenterYPosition, (double) dialRadius, nextAngle);
            DialStop stop = new DialStop(i == 0 ? Color.RED : Color.GRAY,
                    resultPair.first.floatValue(), resultPair.second.floatValue(), 30);

            stops[i] = stop;
            // prepare for next angle.
            nextAngle = nextAngle + angleOffset;
        }
    }

    private Pair<Double, Double> getCoordinate(double xPos, double yPos, double radius, double angle) {
        double resultxPos = 0f;
        double resultyPos = 0f;

        double hypoteneus = radius;
        resultxPos = xPos + hypoteneus * (Math.cos(Math.toRadians(angle)));
        resultyPos = yPos - hypoteneus * (Math.sin(Math.toRadians(angle)));

        return new Pair<>(resultxPos, resultyPos);
    }

    private void rotateArrayIndexes() {
        int nextEntry = -1;
        for (int i = 0; i < stops.length - 1; i++) {
            if (nextEntry != -1) {
                int temp = stops[i + 1].number;
                stops[i + 1].setNumber(nextEntry);
                nextEntry = temp;
            } else {
                nextEntry = stops[i + 1].number;
                stops[i + 1].setNumber(stops[i].number);
            }
        }
        stops[0].setNumber(nextEntry);
        printArray();
    }

    private void printArray() {
        StringBuilder sb = new StringBuilder("Data: \n");
        for (int i = 0; i < stops.length; i++) {
            sb.append("stop[" + i + "] ->" + stops[i].toString() + "\n");
        }
        sb.append("*****");

        Log.i(TAG, sb.toString());
    }

}
