package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.gesture.MultiTouchListener;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnGestureControl;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnMainBitmapChangeListener;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnMultiTouchListener;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnPhotoEditorListener;
import iamutkarshtiwari.github.io.ananas.editimage.layout.ZoomLayout;
import iamutkarshtiwari.github.io.ananas.editimage.view.TextStickerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AddTextFragment extends BaseEditFragment implements OnPhotoEditorListener, View.OnClickListener, OnMainBitmapChangeListener, OnMultiTouchListener {

    public static final int INDEX = ModuleConfig.INDEX_ADDTEXT;
    public static final String TAG = AddTextFragment.class.getName();

    private View mainView;
    private TextStickerView textStickersParentView;
    private ZoomLayout zoomLayout;

    private InputMethodManager inputMethodManager;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<View> addedViews;

    public static AddTextFragment newInstance() {
        return new AddTextFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_add_text, container, false);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditImageActivity editImageActivity = ensureEditActivity();

        inputMethodManager = (InputMethodManager) editImageActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        textStickersParentView = editImageActivity.findViewById(R.id.text_sticker_panel);
        textStickersParentView.setDrawingCacheEnabled(true);
        addedViews = new ArrayList<>();

        zoomLayout = editImageActivity.findViewById(R.id.text_sticker_panel_frame);

        View backToMenu = mainView.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        LinearLayout addTextButton = mainView.findViewById(R.id.add_text_btn);
        addTextButton.setOnClickListener(this);
    }

    private void showTextEditDialog(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(activity, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode1) -> editText(rootView, inputText, colorCode1));
    }

    @Override
    public void onAddViewListener(int numberOfAddedViews) {
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
    }

    @Override
    public void onStartViewChangeListener() {
    }

    @Override
    public void onStopViewChangeListener() {
    }

    @Override
    public void onRemoveViewListener(View removedView) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_text_btn) {
            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(activity);
            textEditorDialogFragment.setOnTextEditorListener(this::addText);
        }
    }

    public void hideInput() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null && isInputMethodShow()) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isInputMethodShow() {
        return inputMethodManager.isActive();
    }

    @Override
    public void onMainBitmapChange() {
        textStickersParentView.updateImageBitmap(activity.getMainBit());
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void backToMain() {
        hideInput();
        clearAllStickers();
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        textStickersParentView.setVisibility(View.GONE);
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TEXT;
        activity.mainImage.setVisibility(View.GONE);
        textStickersParentView.updateImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
        textStickersParentView.setVisibility(View.VISIBLE);

        autoScaleImageToFitBounds();
    }

    private void autoScaleImageToFitBounds() {
        textStickersParentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textStickersParentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scaleImage();
            }
        });
    }

    private void scaleImage() {
        final float zoomLayoutWidth = zoomLayout.getWidth();
        final float zoomLayoutHeight = zoomLayout.getHeight();

        final float imageViewWidth = textStickersParentView.getWidth();
        final float imageViewHeight = textStickersParentView.getHeight();

        // To avoid divideByZero exception
        if (imageViewHeight != 0 && imageViewWidth != 0 && zoomLayoutHeight != 0 && zoomLayoutWidth != 0) {
            final float offsetFactorX = zoomLayoutWidth / imageViewWidth;
            final float offsetFactorY = zoomLayoutHeight / imageViewHeight;

            float scaleFactor = Math.min(offsetFactorX, offsetFactorY);
            zoomLayout.setChildScale(scaleFactor);
        }
    }

    public void applyTextImage() {
        // Hide borders of all stickers before save
        updateViewsBordersVisibilityExcept(null);
        compositeDisposable.clear();
        Disposable applyTextDisposable = Observable.fromCallable(() -> getFinalBitmapFromView(textStickersParentView))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> {
                            if (addedViews.size() > 0) {
                                activity.changeMainBitmap(bitmap, true);
                            }
                            backToMain();
                        },
                        e -> {
                            e.printStackTrace();
                            backToMain();
                            Toast.makeText(getContext(), getString(R.string.iamutkarshtiwari_github_io_ananas_save_error), Toast.LENGTH_SHORT).show();
                        });
        compositeDisposable.add(applyTextDisposable);
    }

    private void clearAllStickers() {
        textStickersParentView.removeAllViews();
    }

    private Bitmap getFinalBitmapFromView(View view) {
        Bitmap finalBitmap = view.getDrawingCache();
        Bitmap resultBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        int textStickerHeightCenterY = textStickersParentView.getHeight() / 2;
        int textStickerWidthCenterX = textStickersParentView.getWidth() / 2;

        int imageViewHeight = textStickersParentView.getBitmapHolderImageView().getHeight();
        int imageViewWidth = textStickersParentView.getBitmapHolderImageView().getWidth();

        // Crop actual image from textStickerView
        return Bitmap.createBitmap(resultBitmap, textStickerWidthCenterX - (imageViewWidth / 2), textStickerHeightCenterY - (imageViewHeight / 2), imageViewWidth, imageViewHeight);
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addText(String text, final int colorCodeTextView) {
        final View textStickerView = getTextStickerLayout();
        final TextView textInputTv = textStickerView.findViewById(R.id.text_sticker_tv);
        final ImageView imgClose = textStickerView.findViewById(R.id.sticker_delete_btn);
        final FrameLayout frameBorder = textStickerView.findViewById(R.id.sticker_border);

        textInputTv.setText(text);
        textInputTv.setTextColor(colorCodeTextView);
        textInputTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                getResources().getDimension(R.dimen.text_sticker_size));

        MultiTouchListener multiTouchListener = new MultiTouchListener(
                imgClose,
                this.textStickersParentView,
                activity.mainImage,
                this, getContext());
        multiTouchListener.setOnGestureControl(new OnGestureControl() {

            boolean isDownAlready = false;

            @Override
            public void onClick() {
                boolean isBackgroundVisible = frameBorder.getTag() != null && (boolean) frameBorder.getTag();
                if (isBackgroundVisible && !isDownAlready) {
                    String textInput = textInputTv.getText().toString();
                    int currentTextColor = textInputTv.getCurrentTextColor();
                    showTextEditDialog(textStickerView, textInput, currentTextColor);
                }
            }

            @Override
            public void onDown() {
                boolean isBackgroundVisible = frameBorder.getTag() != null && (boolean) frameBorder.getTag();
                if (!isBackgroundVisible) {
                    frameBorder.setBackgroundResource(R.drawable.background_border);
                    imgClose.setVisibility(View.VISIBLE);
                    frameBorder.setTag(true);
                    updateViewsBordersVisibilityExcept(textStickerView);
                    isDownAlready = true;
                } else {
                    isDownAlready = false;
                }
            }

            @Override
            public void onLongClick() {
            }
        });

        textStickerView.setOnTouchListener(multiTouchListener);
        addViewToParent(textStickerView);
    }

    private View getTextStickerLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rootView = layoutInflater.inflate(R.layout.view_text_sticker_item, null);
        TextView txtText = rootView.findViewById(R.id.text_sticker_tv);
        if (txtText != null) {
            txtText.setGravity(Gravity.CENTER);
            ImageView imgClose = rootView.findViewById(R.id.sticker_delete_btn);
            if (imgClose != null) {
                imgClose.setOnClickListener(view -> deleteViewFromParent(rootView));
            }
        }
        return rootView;
    }

    private void updateViewsBordersVisibilityExcept(@Nullable View keepView) {
        for (View view : addedViews) {
            if (view != keepView) {
                FrameLayout border = view.findViewById(R.id.sticker_border);
                border.setBackgroundResource(0);
                ImageView closeBtn = view.findViewById(R.id.sticker_delete_btn);
                closeBtn.setVisibility(View.GONE);
                border.setTag(false);
            }
        }
    }

    private void editText(View view, String inputText, int colorCode) {
        TextView inputTextView = view.findViewById(R.id.text_sticker_tv);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            inputTextView.setTextColor(colorCode);
            textStickersParentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    private void addViewToParent(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textStickersParentView.addView(view, params);
        addedViews.add(view);
        updateViewsBordersVisibilityExcept(view);
    }

    private void deleteViewFromParent(View view) {
        textStickersParentView.removeView(view);
        addedViews.remove(view);
        textStickersParentView.invalidate();
        updateViewsBordersVisibilityExcept(null);
    }
}
