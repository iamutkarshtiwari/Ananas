package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class TextStickerView extends View {
    private static int STATUS_IDLE = 0;
    private static int STATUS_MOVE = 1;
    private static int STATUS_DELETE = 2;
    private static int STATUS_ROTATE = 3;

    private int imageCount;
    private boolean moved;
    private int currentStatus;
    private TextStickerItem currentItem;
    private TextStickerItem previousItem;
    private float oldX, oldY;

    private LinkedHashMap<Integer, TextStickerItem> bank = new LinkedHashMap<>();
    private EditTextItemListener editTextListener;

    public TextStickerView(Context context) {
        super(context);
        init();
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        currentStatus = STATUS_IDLE;
    }

    public void setEditTextListener(EditTextItemListener listener) {
        editTextListener = listener;
    }

    public void addText(final String text, int color, Typeface font, int style) {
        TextStickerItem item = new TextStickerItem(this.getContext());
        item.init(text, color, font, style, this);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        bank.put(++imageCount, item);
        currentItem = item;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Integer id : bank.keySet()) {
            TextStickerItem item = bank.get(id);
            item.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                int deleteId = -1;

                // Items are drawn first in first out so the later items are on top. Therefore,
                // reverse the order so we select the items on top first
                ArrayList<Integer> keys = new ArrayList<>(bank.keySet());
                Collections.sort(keys, Collections.reverseOrder());

                for (Integer id : keys) {
                    TextStickerItem item = bank.get(id);

                    // Even if we find a match, we keep going as items are drawn first in first out
                    // so the last item is the one at the top
                    if (item.detectDeleteRect.contains(x, y)) {
                        // ret = true;
                        deleteId = id;
                        currentStatus = STATUS_DELETE;
                        break;
                    } else if (item.detectRotateRect.contains(x, y)) {
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        previousItem = currentItem;
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_ROTATE;
                        oldX = x;
                        oldY = y;
                        break;
                    } else if (item.detectRect.contains(x, y)) {
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        previousItem = currentItem;
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_MOVE;
                        oldX = x;
                        oldY = y;
                        break;
                    }
                }

                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {
                    currentItem.isDrawHelpTool = false;
                    currentItem = null;
                    invalidate();
                }

                if (deleteId > 0 && currentStatus == STATUS_DELETE) {
                    bank.remove(deleteId);
                    currentStatus = STATUS_IDLE;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                moved = true;
                ret = true;
                if (currentStatus == STATUS_MOVE) {
                    float dx = x - oldX;
                    float dy = y - oldY;
                    if (currentItem != null) {
                        currentItem.updatePos(dx, dy);
                        invalidate();
                    }
                    oldX = x;
                    oldY = y;
                } else if (currentStatus == STATUS_ROTATE) {
                    float dx = x - oldX;
                    float dy = y - oldY;
                    if (currentItem != null) {
                        currentItem.updateRotateAndScale(dx, dy);
                        invalidate();
                    }
                    oldX = x;
                    oldY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                ret = false;
                currentStatus = STATUS_IDLE;
                break;
            case MotionEvent.ACTION_UP:
                ret = false;
                currentStatus = STATUS_IDLE;

                if (!moved) {
                    if (previousItem != null && previousItem == currentItem) {
                        // When a user touches an unselected item, it selects it. When a user touches
                        // an already selected item without perfoming a move, scale or rotate, it
                        // indicates they want to edit
                        editTextListener.editTextItem(currentItem);
                    } else {
                        // invalidate to draw the help tool
                        invalidate();
                    }
                } else {
                    moved = false;
                }

                // clear previous item as this is no longer needed
                previousItem = null;

                break;
        }
        return ret;
    }

    public LinkedHashMap<Integer, TextStickerItem> getBank() {
        return bank;
    }

    public void clear() {
        bank.clear();
        this.invalidate();
    }

    public void hideHelper() {
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }

        this.invalidate();
    }

    public interface EditTextItemListener {
        void editTextItem(TextStickerItem item);
    }
}
