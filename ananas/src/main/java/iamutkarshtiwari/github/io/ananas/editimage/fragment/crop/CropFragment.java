package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop;

import android.graphics.Bitmap;
import android.graphics.RectF;
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

import java.util.Arrays;
import java.util.List;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnLoadingDialogListener;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;


public class CropFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_CROP;
    public static final String TAG = CropFragment.class.getName();
    public static final float NO_ASPECT_RATIO_LIMIT = -1;

    private static int SELECTED_COLOR = R.color.white;
    private static int UNSELECTED_COLOR = R.color.text_color_gray_3;

    private View mainView;
    private LinearLayout ratioList;
    private CropImageView cropPanel;
    private OnLoadingDialogListener loadingDialogListener;

    private List<AspectRatio> aspectRatios;
    private float aspectRatioMin;
    private float aspectRatioMax;
    private String aspectRatioMinMsg;
    private String aspectRatioMaxMsg;

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

        for (int i = 0; i < aspectRatios.size(); i++) {
            AspectRatio aspectRatio = aspectRatios.get(i);

            TextView text = new TextView(activity);
            toggleButtonStatus(text, false);
            text.setTextSize(15);
            text.setAllCaps(true);
            text.setTypeface(text.getTypeface(), Typeface.BOLD);
            text.setText(aspectRatios.get(i).getName());
            ratioList.addView(text, params);

            if (i == 0) {
                selectedTextView = text;
            }

            text.setTag(aspectRatio);
            text.setOnClickListener(cropRatioClick);
        }
        toggleButtonStatus(selectedTextView, true);
    }

    public List<AspectRatio> getDefaultAspectRatios() {
        return Arrays.asList(
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_free_size)),
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_fit_image), -1, -1),
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_square), 1, 1),
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_ratio3_4), 3, 4),
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_ratio4_3), 4, 3),
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_ratio9_16), 9, 16),
                new AspectRatio(getString(R.string.iamutkarshtiwari_github_io_ananas_ratio16_9), 16, 9)
        );
    }

    public void setAspectRatios(List<AspectRatio> aspectRatios) {
        this.aspectRatios = aspectRatios;
    }

    public void setMinimumAspectRatio(float minAspectRatio, String validationMessage) {
        this.aspectRatioMin = minAspectRatio;
        this.aspectRatioMinMsg = validationMessage;
    }

    public void setMaximumAspectRatio(float maxAspectRatio, String validationMessage) {
        this.aspectRatioMax = maxAspectRatio;
        this.aspectRatioMaxMsg = validationMessage;
    }

    private final class CropRationClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            toggleButtonStatus(selectedTextView, false);

            TextView currentTextView = (TextView) view;
            toggleButtonStatus(currentTextView, true);

            selectedTextView = currentTextView;

            AspectRatio aspectRatio = (AspectRatio) currentTextView.getTag();
            if (aspectRatio.isFree()) {
                cropPanel.setFixedAspectRatio(false);
            } else if (aspectRatio.isFitImage()) {
                Bitmap currentBmp = ensureEditActivity().getMainBit();
                cropPanel.setAspectRatio(currentBmp.getWidth(), currentBmp.getHeight());
            } else {
                cropPanel.setAspectRatio(aspectRatio.getX(), aspectRatio.getY());
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

        if (aspectRatios == null) {
            aspectRatios = getDefaultAspectRatios();
        }

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
        RectF cropRect = cropPanel.getCropWindowRect();
        float cropAspectRatio = (float) (cropRect.right - cropRect.left) / (cropRect.bottom - cropRect.top);

        if (aspectRatioMin != NO_ASPECT_RATIO_LIMIT &&
                cropAspectRatio < aspectRatioMin) {
            Toast.makeText(getContext(), aspectRatioMinMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        if (aspectRatioMax != NO_ASPECT_RATIO_LIMIT &&
                cropAspectRatio > aspectRatioMax) {
            Toast.makeText(getContext(), aspectRatioMaxMsg, Toast.LENGTH_SHORT).show();
            return;
        }

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
