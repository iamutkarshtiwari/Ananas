package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.utils.RectUtil;

public class TextStickerItem {
    private static final float MIN_SCALE = 0.15f;
    private static final int HELP_BOX_PAD = 25;
    private static final int BORDER_STROKE_WIDTH = 8;

    private static final int BUTTON_WIDTH = Constants.STICKER_BTN_HALF_SIZE;

    private String text;
    private Typeface font;
    private int style;

    private final TextPaint fontPaint;

    private RectF textBaseline;
    private RectF helpBox;
    private RectF deleteRect;
    private RectF rotateRect;

    private float rotateAngle = 0;
    boolean isDrawHelpTool = false;
    private Paint helpBoxPaint = new Paint();

    private float initWidth;

    private static Bitmap deleteBit;
    private static Bitmap rotateBit;

    public RectF detectRect;
    public RectF detectRotateRect;
    public RectF detectDeleteRect;

    TextStickerItem(Context context) {
        fontPaint = new TextPaint();
        fontPaint.setAntiAlias(true);
        fontPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_40sp));

        helpBoxPaint.setColor(Color.WHITE);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(BORDER_STROKE_WIDTH);

        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_close);
        }
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_resize);
        }
    }

    public String getText() { return text; }
    public int getColor() { return fontPaint.getColor(); }
    public Typeface getFont() { return font; }
    public int getStyle() { return style; }

    public void init(String text, int color, Typeface font, int style, View parentView) {
        this.text = text;
        this.font = font;
        this.style = style;

        fontPaint.setColor(color);
        fontPaint.setTypeface(Typeface.create(font, style));

        Rect bounds = new Rect();
        fontPaint.getTextBounds(text, 0, text.length(), bounds);

        int textWidth = bounds.width();
        int textHeight = bounds.height();
        int left = (parentView.getWidth() >> 1) - (textWidth >> 1) - bounds.left;
        int top = (parentView.getHeight() >> 1) - (textHeight >> 1);

        textBaseline = new RectF(left, top, left + textWidth, top);
        initWidth = textBaseline.width();
        isDrawHelpTool = true;

        helpBox = new RectF(
                textBaseline.left,
                textBaseline.top + bounds.top,
                textBaseline.right + (bounds.right - textWidth),
                textBaseline.top + bounds.bottom);

        updateHelpBoxRect();

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        detectRect = new RectF(helpBox);
        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }

    public void update(String text, int color, Typeface font, int style) {
        this.text = text;
        this.font = font;
        this.style = style;

        fontPaint.setColor(color);
        fontPaint.setTypeface(Typeface.create(font, style));

        Rect bounds = new Rect();
        fontPaint.getTextBounds(text, 0, text.length(), bounds);

        textBaseline.right = textBaseline.left + bounds.width();

        helpBox.set(textBaseline.left,
                textBaseline.top + bounds.top,
                textBaseline.right + bounds.left + (bounds.right - bounds.width()),
                textBaseline.top + bounds.bottom);

        updateHelpBoxRect();

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);

        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        Matrix m = new Matrix();
        m.setRotate(rotateAngle, textBaseline.centerX(), textBaseline.centerY());
        m.mapRect(detectRect, helpBox);
        m.mapRect(detectDeleteRect, deleteRect);
        m.mapRect(detectRotateRect, rotateRect);
    }

    private void updateHelpBoxRect() {
        this.helpBox.left -= HELP_BOX_PAD;
        this.helpBox.right += HELP_BOX_PAD;
        this.helpBox.top -= HELP_BOX_PAD;
        this.helpBox.bottom += HELP_BOX_PAD;
    }

    public void updatePos(final float dx, final float dy) {
        textBaseline.offset(dx, dy);

        helpBox.offset(dx, dy);
        deleteRect.offset(dx, dy);
        rotateRect.offset(dx, dy);

        detectRect.offset(dx, dy);
        detectRotateRect.offset(dx, dy);
        detectDeleteRect.offset(dx, dy);
    }

    public void updateRotateAndScale(final float dx, final float dy) {
        float c_x = textBaseline.centerX();
        float c_y = textBaseline.centerY();

        float x = detectRotateRect.centerX();
        float y = detectRotateRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;

        float newWidth = textBaseline.width() * scale;
        if (newWidth / initWidth < MIN_SCALE) {
            return;
        }

        RectUtil.scaleRect(textBaseline, scale);

        Rect bounds = new Rect();
        fontPaint.getTextBounds(text, 0, text.length(), bounds);

        helpBox.set(textBaseline.left,
                textBaseline.top + bounds.top,
                textBaseline.right + (bounds.right - bounds.width()),
                textBaseline.top + bounds.bottom);

        updateHelpBoxRect();

        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);

        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;

        float angle = (float) Math.toDegrees(Math.acos(cos));

        float calMatrix = xa * yb - xb * ya;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        rotateAngle += angle;

        Matrix m = new Matrix();
        m.setRotate(rotateAngle, textBaseline.centerX(), textBaseline.centerY());
        m.mapRect(detectRect, helpBox);
        m.mapRect(detectDeleteRect, deleteRect);
        m.mapRect(detectRotateRect, rotateRect);
    }

    public void updateForCanvas(Canvas newCanvas, View oldCanvas) {
        float newHeightRatio = (float) newCanvas.getHeight() / newCanvas.getWidth();
        int oldUsableHeight = Math.round(oldCanvas.getWidth() * newHeightRatio);
        int heightPadding = Math.round((float) (oldCanvas.getHeight() - oldUsableHeight) / 2);

        textBaseline.left = Math.round((textBaseline.left / oldCanvas.getWidth()) * newCanvas.getWidth());
        textBaseline.top = Math.round(((textBaseline.top - heightPadding) / oldUsableHeight) * newCanvas.getHeight());
        textBaseline.right = Math.round((textBaseline.right / oldCanvas.getWidth()) * newCanvas.getWidth());
        textBaseline.bottom = Math.round(((textBaseline.bottom - heightPadding) / oldUsableHeight) * newCanvas.getHeight());
    }

    public void draw(Canvas canvas) {
        fontPaint.setTextSize(getTextSizeForWidth(fontPaint, textBaseline.width(), text));

        canvas.save();
        canvas.rotate(rotateAngle, textBaseline.centerX(), textBaseline.centerY());

        canvas.drawText(text, textBaseline.left, textBaseline.top, fontPaint);

        if (this.isDrawHelpTool) {
            canvas.drawRoundRect(helpBox, 10, 10, helpBoxPaint);
            canvas.drawBitmap(deleteBit, null, deleteRect, null);
            canvas.drawBitmap(rotateBit, null, rotateRect, null);
        }

        canvas.restore();
    }

    private int getTextSizeForWidth(Paint paint, float desiredWidth, String text) {
        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        return (int) (testTextSize * desiredWidth / bounds.width());
    }
}
