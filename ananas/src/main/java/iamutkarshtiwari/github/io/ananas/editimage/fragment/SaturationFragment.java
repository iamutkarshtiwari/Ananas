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
import iamutkarshtiwari.github.io.ananas.editimage.view.SaturationView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;

public class SaturationFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_CONTRAST;
    private static final int INITIAL_SATURATION = 100;
    public static final String TAG = SaturationFragment.class.getName();
    private SaturationView mSaturationView;
    private SeekBar mSeekBar;
    private View mainView;

    public static SaturationFragment newInstance() {
        return new SaturationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_saturation, null);
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

        this.mSaturationView = ensureEditActivity().saturationView;
        mBackToMenu.setOnClickListener(new SaturationFragment.BackToMenuClick());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                activity.saturationView.setSaturation(value / 10f);
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
        activity.mode = EditImageActivity.MODE_SATURATION;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.saturationView.setImageBitmap(activity.getMainBit());
        activity.saturationView.setVisibility(View.VISIBLE);
        initView();
        activity.bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.saturationView.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
        activity.saturationView.setSaturation(INITIAL_SATURATION);
    }

    public void applySaturation() {
        if (mSeekBar.getProgress() == mSeekBar.getMax()) {
            backToMain();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) mSaturationView.getDrawable()).getBitmap();
        activity.changeMainBitmap(Utils.saturationBitmap(bitmap, mSaturationView.getSaturation()), true);
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax());
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
