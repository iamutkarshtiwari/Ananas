package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TextStickerView extends RelativeLayout {
    private ImageView bitmapHolderImageView;

    public TextStickerView(Context context) {
        super(context);
        init(null);
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
    }

    public ImageView getBitmapHolderImageView() {
        return bitmapHolderImageView;
    }

    public void updateImageBitmap(Bitmap bitmap) {
        if (bitmapHolderImageView != null) {
            removeView(bitmapHolderImageView);
        }

        bitmapHolderImageView = new ImageView(getContext());

        //Setup image attributes
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        bitmapHolderImageView.setLayoutParams(imageViewParams);
        bitmapHolderImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bitmapHolderImageView.setAdjustViewBounds(true);

        bitmapHolderImageView.setDrawingCacheEnabled(true);
        bitmapHolderImageView.setImageBitmap(bitmap);
        addView(bitmapHolderImageView);
    }
}
