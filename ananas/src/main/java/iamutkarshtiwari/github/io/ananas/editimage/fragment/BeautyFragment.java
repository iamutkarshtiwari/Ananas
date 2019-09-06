package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fliter.PhotoProcessing;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BeautyFragment extends BaseEditFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String TAG = BeautyFragment.class.getName();

    public static final int INDEX = ModuleConfig.INDEX_BEAUTY;

    private View mainView;
    private Dialog dialog;

    private SeekBar smoothValueBar;
    private SeekBar whiteValueBar;

    private CompositeDisposable disposable = new CompositeDisposable();
    private Disposable beautyDisposable;
    private Bitmap finalBmp;

    private int smooth = 0;
    private int whiteSkin = 0;


    public static BeautyFragment newInstance() {
        return new BeautyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_beauty, null);

        smoothValueBar = mainView.findViewById(R.id.smooth_value_bar);
        whiteValueBar = mainView.findViewById(R.id.white_skin_value_bar);
        dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_loading,
                false);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View backToMenu = mainView.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单

        smoothValueBar.setOnSeekBarChangeListener(this);
        whiteValueBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        doBeautyTask();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    protected void doBeautyTask() {
        if (beautyDisposable != null && !beautyDisposable.isDisposed()) {
            beautyDisposable.dispose();
        }
        smooth = smoothValueBar.getProgress();
        whiteSkin = whiteValueBar.getProgress();

        if (smooth == 0 && whiteSkin == 0) {
            activity.mainImage.setImageBitmap(activity.getMainBit());
            return;
        }

        beautyDisposable = beautify(smooth, whiteSkin)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> dialog.show())
                .doFinally(() -> dialog.dismiss())
                .subscribe(bitmap -> {
                    if (bitmap == null)
                        return;
                    activity.mainImage.setImageBitmap(bitmap);
                    finalBmp = bitmap;
                }, e -> {
                    // Do nothing on error
                });
        disposable.add(beautyDisposable);
    }

    private Single<Bitmap> beautify(int smoothVal, int whiteSkinVal) {
        return Single.fromCallable(() -> {
            Bitmap srcBitmap = Bitmap.createBitmap(
                    activity.getMainBit().copy(
                            Bitmap.Config.ARGB_8888, true)
            );
            PhotoProcessing.handleSmoothAndWhiteSkin(srcBitmap, smoothVal, whiteSkinVal);
            return srcBitmap;
        });
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void backToMain() {
        this.smooth = 0;
        this.whiteSkin = 0;
        smoothValueBar.setProgress(0);
        whiteValueBar.setProgress(0);

        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图

        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_BEAUTY;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        activity.bannerFlipper.showNext();
    }

    public void applyBeauty() {
        if (finalBmp != null && (smooth != 0 || whiteSkin != 0)) {
            activity.changeMainBitmap(finalBmp, true);
        }

        backToMain();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.clear();
    }
}
