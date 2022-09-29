package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

public class CustomPaintView extends View {
    private Paint mPaint;
    private Bitmap mDrawBit;
    private Paint mEraserPaint;

    private Canvas mPaintCanvas = null;

    private float last_x;
    private float last_y;
    private boolean isEraser;

    private int mColor;

    public CustomPaintView(Context context) {
        super(context);
        init(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawBit == null) {
            generatorBit();
        }
    }

    private void generatorBit() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        if (measuredHeight <= 0 || measuredWidth <= 0) {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            measuredWidth = getMeasuredWidth();
            measuredHeight = getMeasuredHeight();

            if (measuredWidth > 0 && measuredHeight > 0) {
                mDrawBit = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            }

        } else {
            mDrawBit = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        }

        if (mDrawBit != null) {
            mPaintCanvas = new Canvas(mDrawBit);
        }
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setStrokeWidth(40);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(mColor);
    }

    public void setWidth(float width) {
        this.mPaint.setStrokeWidth(width);
    }

    public void setStrokeAlpha(float alpha) {
        this.mPaint.setAlpha((int) alpha);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawBit != null) {
            canvas.drawBitmap(mDrawBit, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ret = true;
                last_x = x;
                last_y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                mPaintCanvas.drawLine(last_x, last_y, x, y, isEraser ? mEraserPaint : mPaint);
                last_x = x;
                last_y = y;
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                break;
        }
        return ret;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }
    }

    public void setEraser(boolean eraser) {
        this.isEraser = eraser;
        mPaint.setColor(eraser ? Color.TRANSPARENT : mColor);
    }

    public void setEraserStrokeWidth(float width) {
        mEraserPaint.setStrokeWidth(width);
    }

    public Bitmap getPaintBit() {
        return mDrawBit;
    }

    public void reset() {
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }

        generatorBit();
    }
}
