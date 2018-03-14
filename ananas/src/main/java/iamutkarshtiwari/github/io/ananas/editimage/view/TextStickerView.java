package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.utils.ListUtil;
import iamutkarshtiwari.github.io.ananas.editimage.utils.RectUtil;

import java.util.ArrayList;
import java.util.List;

public class TextStickerView extends View {
    public static final float TEXT_SIZE_DEFAULT = 80;
    public static final int PADDING = 32;
    public static final int TEXT_TOP_PADDING = 10;

    private TextPaint mPaint = new TextPaint();
    private Paint debugPaint = new Paint();
    private Paint mHelpPaint = new Paint();

    private Rect mTextRect = new Rect();
    private RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();
    private Rect mRotateRect = new Rect();

    private RectF mDeleteDstRect = new RectF();
    private RectF mRotateDstRect = new RectF();

    private Bitmap mDeleteBitmap;
    private Bitmap mRotateBitmap;

    private int mCurrentMode = IDLE_MODE;

    private static final int IDLE_MODE = 2;
    private static final int MOVE_MODE = 3;
    private static final int ROTATE_MODE = 4;
    private static final int DELETE_MODE = 5;

    private EditText mEditText;//输入控件

    public int layout_x = 0;
    public int layout_y = 0;

    private float last_x = 0;
    private float last_y = 0;

    public float mRotateAngle = 0;
    public float mScale = 1;
    private boolean isInitLayout = true;

    private boolean isShowHelpBox = true;

    private boolean mAutoNewLine = false;
    private List<String> mTextContents = new ArrayList<String>(2);
    private String mText;

    public TextStickerView(Context context) {
        super(context);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setEditText(EditText textView) {
        this.mEditText = textView;
    }

    private void initView(Context context) {
        debugPaint.setColor(Color.parseColor("#66ff0000"));

        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sticker_delete);
        mRotateBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sticker_rotate);

        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mRotateBitmap.getHeight());

        mDeleteDstRect = new RectF(0, 0, Constants.STICKER_BTN_HALF_SIZE << 1, Constants.STICKER_BTN_HALF_SIZE << 1);
        mRotateDstRect = new RectF(0, 0, Constants.STICKER_BTN_HALF_SIZE << 1, Constants.STICKER_BTN_HALF_SIZE << 1);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TEXT_SIZE_DEFAULT);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mHelpPaint.setColor(Color.BLACK);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setAntiAlias(true);
        mHelpPaint.setStrokeWidth(4);
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public void setTextColor(int newColor) {
        mPaint.setColor(newColor);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInitLayout) {
            isInitLayout = false;
            resetView();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText))
            return;

        parseText();
        drawContent(canvas);
    }

    protected void parseText() {
        if (TextUtils.isEmpty(mText))
            return;

        mTextContents.clear();

        String[] splits = mText.split("\n");
        for (String text : splits) {
            mTextContents.add(text);
        }
    }

    private void drawContent(Canvas canvas) {
        drawText(canvas);

        //draw x and rotate button
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;
        mDeleteDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        mRotateDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);

        RectUtil.rotateRect(mDeleteDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(mRotateDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);

        if (!isShowHelpBox) {
            return;
        }

        canvas.save();
        canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
        canvas.restore();


        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
        canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);
    }

    private void drawText(Canvas canvas) {
        drawText(canvas, layout_x, layout_y, mScale, mRotateAngle);
    }

    public void drawText(Canvas canvas, int _x, int _y, float scale, float rotate) {
        if (ListUtil.isEmpty(mTextContents))
            return;

        int x = _x;
        int y = _y;
        int text_height = 0;

        mTextRect.set(0, 0, 0, 0);//clear
        Rect tempRect = new Rect();
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int charMinHeight = Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom);//字体高度
        text_height = charMinHeight;
        for (int i = 0; i < mTextContents.size(); i++) {
            String text = mTextContents.get(i);
            mPaint.getTextBounds(text, 0, text.length(), tempRect);
            if (tempRect.height() <= 0) {
                tempRect.set(0, 0, 0, text_height);
            }

            RectUtil.rectAddV(mTextRect, tempRect, 0, charMinHeight);
        }

        mTextRect.offset(x, y);

        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
        RectUtil.scaleRect(mHelpBoxRect, scale);

        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());

        int draw_text_y = y + (text_height >> 1) + PADDING;
        for (int i = 0; i < mTextContents.size(); i++) {
            canvas.drawText(mTextContents.get(i), x, draw_text_y, mPaint);
            draw_text_y += text_height;
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = ROTATE_MODE;
                    last_x = mRotateDstRect.centerX();
                    last_y = mRotateDstRect.centerY();
                    ret = true;
                } else if (mHelpBoxRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;
                } else {
                    isShowHelpBox = false;
                    invalidate();
                }

                if (mCurrentMode == DELETE_MODE) {
                    mCurrentMode = IDLE_MODE;
                    clearTextContent();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    layout_x += dx;
                    layout_y += dy;

                    invalidate();

                    last_x = x;
                    last_y = y;
                } else if (mCurrentMode == ROTATE_MODE) {
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    updateRotateAndScale(dx, dy);

                    invalidate();
                    last_x = x;
                    last_y = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                mCurrentMode = IDLE_MODE;
                break;
        }

        return ret;
    }

    public void clearTextContent() {
        if (mEditText != null) {
            mEditText.setText(null);
        }
    }

    public void updateRotateAndScale(final float dx, final float dy) {
        float c_x = mHelpBoxRect.centerX();
        float c_y = mHelpBoxRect.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;

        mScale *= scale;
        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        mRotateAngle += angle;
    }

    public void resetView() {
        layout_x = getMeasuredWidth() / 2;
        layout_y = getMeasuredHeight() / 2;
        mRotateAngle = 0;
        mScale = 1;
        mTextContents.clear();
    }

    public float getScale() {
        return mScale;
    }

    public float getRotateAngle() {
        return mRotateAngle;
    }

    public boolean isAutoNewLine() {
        return mAutoNewLine;
    }

    public void setAutoNewline(boolean isAuto) {
        if (mAutoNewLine != isAuto) {
            mAutoNewLine = isAuto;
            postInvalidate();
        }
    }
}
