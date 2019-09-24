package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;
import iamutkarshtiwari.github.io.ananas.editimage.view.BrightnessView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;


public class BrightnessFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_BRIGHTNESS;
    public static final String TAG = BrightnessFragment.class.getName();

    private static final int INITIAL_BRIGHTNESS = 0;

    private BrightnessView mBrightnessView;
    private SeekBar mSeekBar;
    private View mainView;

    public static BrightnessFragment newInstance() {
        return new BrightnessFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_brightness, null);
        mappingView(mainView);
        return mainView;
    }

    private void mappingView(View view) {
        mSeekBar = view.findViewById(R.id.seekBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View mBackToMenu = mainView.findViewById(R.id.back_to_main);

        this.mBrightnessView = ensureEditActivity().brightnessView;
        mBackToMenu.setOnClickListener(new BrightnessFragment.BackToMenuClick());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                activity.brightnessView.setBright(value / 10f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initView();
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_BRIGHTNESS;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.brightnessView.setImageBitmap(activity.getMainBit());
        activity.brightnessView.setVisibility(View.VISIBLE);
        initView();
        activity.bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.brightnessView.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
        activity.brightnessView.setBright(INITIAL_BRIGHTNESS);
    }

    public void applyBrightness() {
        if (mSeekBar.getProgress() == mSeekBar.getMax() / 2) {
            backToMain();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) mBrightnessView.getDrawable()).getBitmap();
        activity.changeMainBitmap(Utils.brightBitmap(bitmap, mBrightnessView.getBright()), true);
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax() / 2);
    }


    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
