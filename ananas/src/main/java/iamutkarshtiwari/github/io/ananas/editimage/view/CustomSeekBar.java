package iamutkarshtiwari.github.io.ananas.editimage.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class CustomSeekBar extends SeekBar {

    private Rect rect;
    private Paint paint;
    private int seekbar_height;
    private float max;
    private float mid;

    public CustomSeekBar(Context context) {
        super(context);

    }

    public CustomSeekBar(Context context, AttributeSet attrs) {

        super(context, attrs);
        rect = new Rect();
        paint = new Paint();
        seekbar_height = 6;
        this.max = getMax();
        this.mid = max / 2f;
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        rect.set(getThumbOffset(),
                (getHeight() / 2) - (seekbar_height / 2),
                getWidth() - getThumbOffset(),
                (getHeight() / 2) + (seekbar_height / 2));

        paint.setColor(Color.GRAY);

        canvas.drawRect(rect, paint);

        // TODO: Fix the slider indicator

//        if (this.getProgress() > mid) {
//
//
//            rect.set(getWidth() / 2,
//                    (getHeight() / 2) - (seekbar_height / 2),
//                    (int) ((getWidth() / 2) + ((getWidth() / max) * (getProgress() - mid))),
//                    getHeight() / 2 + (seekbar_height / 2));
//
//            paint.setColor(Color.CYAN);
//            canvas.drawRect(rect, paint);
//
//        }
//
//        if (this.getProgress() < mid) {
//
//            rect.set((int) ((getWidth() / 2) - ((getWidth() / max) * (mid - getProgress()))),
//                    (getHeight() / 2) - (seekbar_height / 2),
//                    getWidth() / 2,
//                    getHeight() / 2 + (seekbar_height / 2));
//
//            paint.setColor(Color.CYAN);
//            canvas.drawRect(rect, paint);
//
//        }

        super.onDraw(canvas);
    }
}
