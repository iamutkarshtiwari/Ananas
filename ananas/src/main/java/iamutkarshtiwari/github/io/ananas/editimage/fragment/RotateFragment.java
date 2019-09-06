package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.view.RotateImageView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RotateFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_ROTATE;
    public static final int MIN_ROTATION_DEGREE = 0;
    public static final int MAX_ROTATION_DEGREE = 360;
    public static final String TAG = RotateFragment.class.getName();

    private View mainView;
    private SeekBar seekBar;
    private RotateImageView rotatePanel;
    private Dialog loadingDialog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static RotateFragment newInstance() {
        return new RotateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_rotate, null);
        loadingDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_loading,
                false);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View backToMenu = mainView.findViewById(R.id.back_to_main);
        seekBar = mainView.findViewById(R.id.rotate_bar);
        seekBar.setProgress(MIN_ROTATION_DEGREE);

        this.rotatePanel = ensureEditActivity().rotatePanel;
        backToMenu.setOnClickListener(new BackToMenuClick());
        seekBar.setOnSeekBarChangeListener(new RotateAngleChange());
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_ROTATE;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.rotatePanel.addBit(activity.getMainBit(),
                activity.mainImage.getBitmapRect());
        activity.rotateFragment.seekBar.setProgress(MIN_ROTATION_DEGREE);
        activity.rotatePanel.reset();
        activity.rotatePanel.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setVisibility(View.VISIBLE);
        this.rotatePanel.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
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

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    public void applyRotateImage() {
        if (seekBar.getProgress() == MIN_ROTATION_DEGREE || seekBar.getProgress() == MAX_ROTATION_DEGREE) {
            backToMain();
        } else {
            compositeDisposable.clear();
            Disposable applyRotationDisposable = applyRotation(activity.getMainBit())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(subscriber -> loadingDialog.show())
                    .doFinally(() -> loadingDialog.dismiss())
                    .subscribe(processedBitmap -> {
                        if (processedBitmap == null)
                            return;

                        applyAndExit(processedBitmap);
                    }, e -> {
                        // Do nothing on error
                    });

            compositeDisposable.add(applyRotationDisposable);
        }
    }

    private final class RotateAngleChange implements OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int angle,
                                      boolean fromUser) {
            rotatePanel.rotateImage(angle);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private Single<Bitmap> applyRotation(Bitmap sourceBitmap) {
        return Single.fromCallable(() -> {
            RectF imageRect = rotatePanel.getImageNewRect();
            Bitmap resultBitmap = Bitmap.createBitmap((int) imageRect.width(),
                    (int) imageRect.height(), Bitmap.Config.ARGB_4444);

            Canvas canvas = new Canvas(resultBitmap);
            int w = sourceBitmap.getWidth() >> 1;
            int h = sourceBitmap.getHeight() >> 1;

            float centerX = imageRect.width() / 2;
            float centerY = imageRect.height() / 2;

            float left = centerX - w;
            float top = centerY - h;

            RectF destinationRect = new RectF(left, top, left + sourceBitmap.getWidth(), top
                    + sourceBitmap.getHeight());
            canvas.save();
            canvas.rotate(
                    rotatePanel.getRotateAngle(),
                    imageRect.width() / 2,
                    imageRect.height() / 2
            );

            canvas.drawBitmap(
                    sourceBitmap,
                    new Rect(
                            0,
                            0,
                            sourceBitmap.getWidth(),
                            sourceBitmap.getHeight()),
                    destinationRect,
                    null);
            canvas.restore();
            return resultBitmap;
        });
    }

    private void applyAndExit(Bitmap resultBitmap) {
        activity.changeMainBitmap(resultBitmap, true);
        backToMain();
    }
}
