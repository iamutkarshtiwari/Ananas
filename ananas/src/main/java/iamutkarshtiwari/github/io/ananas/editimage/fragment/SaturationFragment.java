package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;
import iamutkarshtiwari.github.io.ananas.editimage.view.SaturationView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;

public class SaturationFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_CONTRAST;
    public static final String TAG = SaturationFragment.class.getName();
    SaturationView mSaturationView;
    SeekBar mSeekBar;
    private View mainView;
    private View mBackToMenu;

    private boolean start = true;

    public static SaturationFragment newInstance() {
        SaturationFragment fragment = new SaturationFragment();
        return fragment;
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

        mBackToMenu = mainView.findViewById(R.id.back_to_main);

        this.mSaturationView = ensureEditActivity().mSaturationView;
        mBackToMenu.setOnClickListener(new SaturationFragment.BackToMenuClick());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                activity.mSaturationView.setSaturation(value / 10f);
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

        activity.mSaturationView.setImageBitmap(activity.getMainBit());
        activity.mSaturationView.setVisibility(View.VISIBLE);
        initView();
        activity.bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mSaturationView.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
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

    private void back() {
        getActivity().onBackPressed();
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
