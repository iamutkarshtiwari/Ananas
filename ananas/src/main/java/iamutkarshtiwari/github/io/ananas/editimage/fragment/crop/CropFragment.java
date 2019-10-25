package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnLoadingDialogListener;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class CropFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_CROP;
    public static final String TAG = CropFragment.class.getName();

    private static int SELECTED_COLOR = R.color.white;
    private static int UNSELECTED_COLOR = R.color.text_color_gray_3;

    private View mainView;
    private LinearLayout ratioList;
    private CropImageView cropPanel;
    private OnLoadingDialogListener loadingDialogListener;

    private CropRationClick cropRatioClick = new CropRationClick();
    private TextView selectedTextView;

    private CompositeDisposable disposables = new CompositeDisposable();

    public static CropFragment newInstance() {
        return new CropFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
        return mainView;
    }

    private void setUpRatioList() {
        ratioList.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER;
        params.leftMargin = 20;
        params.rightMargin = 20;

        RatioText[] ratioTextList = RatioText.values();
        for (int i = 0; i < ratioTextList.length; i++) {
            TextView text = new TextView(activity);
            toggleButtonStatus(text, false);
            text.setTextSize(15);
            text.setAllCaps(true);
            text.setTypeface(text.getTypeface(), Typeface.BOLD);
            text.setText(getResources().getText(ratioTextList[i].getRatioTextId()));
            ratioList.addView(text, params);

            if (i == 0) {
                selectedTextView = text;
            }

            text.setTag(ratioTextList[i]);
            text.setOnClickListener(cropRatioClick);
        }
        toggleButtonStatus(selectedTextView, true);
    }


    private final class CropRationClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            toggleButtonStatus(selectedTextView, false);

            TextView currentTextView = (TextView) view;
            toggleButtonStatus(currentTextView, true);

            selectedTextView = currentTextView;

            RatioText ratioText = (RatioText) currentTextView.getTag();
            if (ratioText == RatioText.FREE) {
                cropPanel.setFixedAspectRatio(false);
            } else if (ratioText == RatioText.FIT_IMAGE) {
                Bitmap currentBmp = ensureEditActivity().getMainBit();
                cropPanel.setAspectRatio(currentBmp.getWidth(), currentBmp.getHeight());
            } else {
                AspectRatio aspectRatio = ratioText.getAspectRatio();
                cropPanel.setAspectRatio(aspectRatio.getAspectX(), aspectRatio.getAspectY());
            }
        }
    }

    private void toggleButtonStatus(TextView view, boolean isActive) {
        view.setTextColor(getColorFromRes((isActive) ? SELECTED_COLOR : UNSELECTED_COLOR));
        view.setTypeface((isActive) ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(activity, resId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadingDialogListener = ensureEditActivity();

        View backToMenu = mainView.findViewById(R.id.back_to_main);
        ratioList = mainView.findViewById(R.id.ratio_list_group);
        setUpRatioList();
        this.cropPanel = ensureEditActivity().cropPanel;
        backToMenu.setOnClickListener(new BackToMenuClick());
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_CROP;

        activity.mainImage.setVisibility(View.GONE);
        cropPanel.setVisibility(View.VISIBLE);
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);

        activity.bannerFlipper.showNext();
        cropPanel.setImageBitmap(activity.getMainBit());
        cropPanel.setFixedAspectRatio(false);
    }


    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }


    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        cropPanel.setVisibility(View.GONE);
        activity.mainImage.setVisibility(View.VISIBLE);

        activity.mainImage.setScaleEnabled(true);
        activity.bottomGallery.setCurrentItem(0);

        if (selectedTextView != null) {
            selectedTextView.setTextColor(getColorFromRes(UNSELECTED_COLOR));
        }

        activity.bannerFlipper.showPrevious();
    }


    public void applyCropImage() {
        disposables.add(getCroppedBitmap()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialogListener.showLoadingDialog())
                .doFinally(() -> loadingDialogListener.dismissLoadingDialog())
                .subscribe(bitmap -> {
                    activity.changeMainBitmap(bitmap, true);
                    backToMain();
                }, e -> {
                    e.printStackTrace();
                    backToMain();
                    Toast.makeText(getContext(), "Error while saving image", Toast.LENGTH_SHORT).show();
                }));
    }

    private Single<Bitmap> getCroppedBitmap() {
        return Single.fromCallable(() -> cropPanel.getCroppedImage());
    }

    @Override
    public void onStop() {
        disposables.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }
}
