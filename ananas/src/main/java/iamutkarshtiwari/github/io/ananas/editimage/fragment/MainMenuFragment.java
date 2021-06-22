package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.crop.CropFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.mainmenu.MenuSectionnActions;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.paint.PaintFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;


public class MainMenuFragment extends BaseEditFragment implements
        View.OnClickListener, MenuSectionnActions {
    public static final int INDEX = ModuleConfig.INDEX_MAIN;

    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;

    private View stickerBtn;
    private View filterBtn;
    private View cropBtn;
    private View rotateBtn;
    private View textBtn;
    private View paintBtn;
    private View beautyBtn;
    private View brightnessBtn;
    private View saturationBtn;
    private Bundle intentBundle;

    private final BehaviorSubject<Boolean> menuOptionsClickableSubject = BehaviorSubject.create();
    private final CompositeDisposable disposable = new CompositeDisposable();

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
                null);
        intentBundle = getArguments();
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stickerBtn = mainView.findViewById(R.id.btn_stickers);
        filterBtn = mainView.findViewById(R.id.btn_filter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        textBtn = mainView.findViewById(R.id.btn_text);
        paintBtn = mainView.findViewById(R.id.btn_paint);
        beautyBtn = mainView.findViewById(R.id.btn_beauty);
        brightnessBtn = mainView.findViewById(R.id.btn_brightness);
        saturationBtn = mainView.findViewById(R.id.btn_contrast);

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.STICKER_FEATURE, false)) {
            stickerBtn.setVisibility(View.VISIBLE);
            stickerBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.FILTER_FEATURE, false)) {
            filterBtn.setVisibility(View.VISIBLE);
            filterBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.CROP_FEATURE, false)) {
            cropBtn.setVisibility(View.VISIBLE);
            cropBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.ROTATE_FEATURE, false)) {
            rotateBtn.setVisibility(View.VISIBLE);
            rotateBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.ADD_TEXT_FEATURE, false)) {
            textBtn.setVisibility(View.VISIBLE);
            textBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.PAINT_FEATURE, false)) {
            paintBtn.setVisibility(View.VISIBLE);
            paintBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BEAUTY_FEATURE, false)) {
            beautyBtn.setVisibility(View.VISIBLE);
            beautyBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BRIGHTNESS_FEATURE, false)) {
            brightnessBtn.setVisibility(View.VISIBLE);
            brightnessBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.SATURATION_FEATURE, false)) {
            saturationBtn.setVisibility(View.VISIBLE);
            saturationBtn.setOnClickListener(this);
        }

        subscribeMenuOptionsSubject();
    }

    private void subscribeMenuOptionsSubject() {
        disposable.add(
                menuOptionsClickableSubject
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(isClickable -> {
                                    stickerBtn.setClickable(isClickable);
                                    filterBtn.setClickable(isClickable);
                                    cropBtn.setClickable(isClickable);
                                    rotateBtn.setClickable(isClickable);
                                    textBtn.setClickable(isClickable);
                                    paintBtn.setClickable(isClickable);
                                    beautyBtn.setClickable(isClickable);
                                    brightnessBtn.setClickable(isClickable);
                                    saturationBtn.setClickable(isClickable);
                                }, error -> { }
                        )
        );
    }

    @Override
    public void setMenuOptionsClickable(boolean isClickable) {
        menuOptionsClickableSubject.onNext(isClickable);
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void backToMain() {
        //do nothing
    }

    @Override
    public void onClick(View v) {
        if (v == stickerBtn) {
            onStickClick();
        } else if (v == filterBtn) {
            onFilterClick();
        } else if (v == cropBtn) {
            onCropClick();
        } else if (v == rotateBtn) {
            onRotateClick();
        } else if (v == textBtn) {
            onAddTextClick();
        } else if (v == paintBtn) {
            onPaintClick();
        } else if (v == beautyBtn) {
            onBeautyClick();
        } else if (v == brightnessBtn) {
            onBrightnessClick();
        } else if (v == saturationBtn) {
            onContrastClick();
        }
    }

    private void onStickClick() {
        activity.bottomGallery.setCurrentItem(StickerFragment.INDEX);
        activity.stickerFragment.onShow();
    }

    private void onFilterClick() {
        activity.bottomGallery.setCurrentItem(FilterListFragment.INDEX);
        activity.filterListFragment.onShow();
    }

    private void onCropClick() {
        activity.bottomGallery.setCurrentItem(CropFragment.INDEX);
        activity.cropFragment.onShow();
    }

    private void onRotateClick() {
        activity.bottomGallery.setCurrentItem(RotateFragment.INDEX);
        activity.rotateFragment.onShow();
    }


    private void onAddTextClick() {
        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
        activity.addTextFragment.onShow();
    }

    private void onPaintClick() {
        activity.bottomGallery.setCurrentItem(PaintFragment.INDEX);
        activity.paintFragment.onShow();
    }

    private void onBeautyClick() {
        activity.bottomGallery.setCurrentItem(BeautyFragment.INDEX);
        activity.beautyFragment.onShow();
    }

    private void onBrightnessClick() {
        activity.bottomGallery.setCurrentItem(BrightnessFragment.INDEX);
        activity.brightnessFragment.onShow();
    }

    private void onContrastClick() {
        activity.bottomGallery.setCurrentItem(SaturationFragment.INDEX);
        activity.saturationFragment.onShow();
    }

    @Override
    public void onDestroyView() {
        disposable.dispose();
        super.onDestroyView();
    }
}
