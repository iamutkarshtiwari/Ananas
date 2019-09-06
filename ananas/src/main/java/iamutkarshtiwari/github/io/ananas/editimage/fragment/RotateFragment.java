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
import android.widget.ImageView;

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


public class RotateFragment extends BaseEditFragment implements OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_ROTATE;
    public static final String TAG = RotateFragment.class.getName();

    private static final int RIGHT_ANGLE = 90;

    private View mainView;
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

        this.rotatePanel = ensureEditActivity().rotatePanel;
        setClickListeners();
    }

    private void setClickListeners() {
        View backToMenu = mainView.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        ImageView rotateLeft = mainView.findViewById(R.id.rotate_left);
        ImageView rotateRight = mainView.findViewById(R.id.rotate_right);
        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_ROTATE;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.rotatePanel.addBit(activity.getMainBit(),
                activity.mainImage.getBitmapRect());

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rotate_left) {
            int updatedAngle = rotatePanel.getRotateAngle() - RIGHT_ANGLE;
            rotatePanel.rotateImage(updatedAngle);
        } else if (id == R.id.rotate_right) {
            int updatedAngle = rotatePanel.getRotateAngle() + RIGHT_ANGLE;
            rotatePanel.rotateImage(updatedAngle);
        }
    }

    public void applyRotateImage() {
        if (rotatePanel.getRotateAngle() == 0 || (rotatePanel.getRotateAngle() % 360) == 0) {
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
