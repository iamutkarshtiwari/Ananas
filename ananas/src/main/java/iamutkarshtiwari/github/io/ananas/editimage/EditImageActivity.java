package iamutkarshtiwari.github.io.ananas.editimage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.isseiaoki.simplecropview.CropImageView;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.AddTextFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BeautyFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BrightnessFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.CropFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.FilterListFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.MainMenuFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.PaintFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.RotateFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.SaturationFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.StickerFragment;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnMainBitmapChangeListener;
import iamutkarshtiwari.github.io.ananas.editimage.utils.BitmapUtils;
import iamutkarshtiwari.github.io.ananas.editimage.utils.PermissionUtils;
import iamutkarshtiwari.github.io.ananas.editimage.view.BrightnessView;
import iamutkarshtiwari.github.io.ananas.editimage.view.CustomPaintView;
import iamutkarshtiwari.github.io.ananas.editimage.view.CustomViewPager;
import iamutkarshtiwari.github.io.ananas.editimage.view.RotateImageView;
import iamutkarshtiwari.github.io.ananas.editimage.view.SaturationView;
import iamutkarshtiwari.github.io.ananas.editimage.view.StickerView;
import iamutkarshtiwari.github.io.ananas.editimage.view.TextStickerView;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouch;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;
import iamutkarshtiwari.github.io.ananas.editimage.widget.RedoUndoController;


public class EditImageActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 110;

    private String[] mRequiredPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String IMAGE_IS_EDIT = "image_is_edit";

    public static final int MODE_NONE = 0;
    public static final int MODE_STICKERS = 1;
    public static final int MODE_FILTER = 2;
    public static final int MODE_CROP = 3;
    public static final int MODE_ROTATE = 4;
    public static final int MODE_TEXT = 5;
    public static final int MODE_PAINT = 6;
    public static final int MODE_BEAUTY = 7;
    public static final int MODE_BRIGHTNESS = 8;
    public static final int MODE_SATURATION = 9;

    public String sourceFilePath;
    public String outputFilePath;
    private int imageWidth, imageHeight;
    private LoadImageTask mLoadImageTask;

    public int mode = MODE_NONE;

    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;
    protected boolean isPortraitForced = false;

    private EditImageActivity mContext;
    private Bitmap mainBitmap;
    public ImageViewTouch mainImage;

    public ViewFlipper bannerFlipper;

    public StickerView mStickerView;
    public CropImageView mCropPanel;
    public RotateImageView rotatePanel;
    public TextStickerView mTextStickerView;
    public CustomPaintView mPaintView;
    public BrightnessView mBrightnessView;
    public SaturationView mSaturationView;

    public CustomViewPager bottomGallery;
    private MainMenuFragment mainMenuFragment;
    public StickerFragment stickerFragment;
    public FilterListFragment filterListFragment;
    public CropFragment cropFragment;
    public RotateFragment rotateFragment;
    public AddTextFragment addTextFragment;
    public PaintFragment paintFragment;
    public BeautyFragment beautyFragment;
    private SaveImageTask saveImageTask;
    public BrightnessFragment brightnessFragment;
    public SaturationFragment saturationFragment;

    private RedoUndoController redoUndoController;

    private OnMainBitmapChangeListener onMainBitmapChangeListener;

    /**
     * @param activity Source activity
     * @param intent from ImageEditorIntentBuilder
     * @param requestCode for result
     */
    public static void start(Activity activity, Intent intent, int requestCode) {
        if (TextUtils.isEmpty(intent.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH))) {
            Toast.makeText(activity, R.string.no_choose, Toast.LENGTH_SHORT).show();
            return;
        }
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        initView();
        getData();
    }

    private void getData() {
        isPortraitForced = getIntent().getBooleanExtra(ImageEditorIntentBuilder.FORCE_PORTRAIT, false);
        sourceFilePath = getIntent().getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
        outputFilePath = getIntent().getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
        loadImage(sourceFilePath);
    }

    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        bannerFlipper = (ViewFlipper) findViewById(R.id.banner_flipper);
        bannerFlipper.setInAnimation(this, R.anim.in_bottom_to_top);
        bannerFlipper.setOutAnimation(this, R.anim.out_bottom_to_top);
        View applyBtn = findViewById(R.id.apply);
        applyBtn.setOnClickListener(new ApplyBtnClick());
        View saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new SaveBtnClick());

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);

        View backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mStickerView = findViewById(R.id.sticker_panel);
        mCropPanel = findViewById(R.id.crop_panel);
        rotatePanel = findViewById(R.id.rotate_panel);
        mTextStickerView = findViewById(R.id.text_sticker_panel);
        mPaintView = findViewById(R.id.custom_paint_view);
        mBrightnessView = findViewById(R.id.brightness_panel);
        mSaturationView = findViewById(R.id.contrast_panel);
        bottomGallery = findViewById(R.id.bottom_gallery);

        mainMenuFragment = MainMenuFragment.newInstance();
        mainMenuFragment.setArguments(getIntent().getExtras());

        BottomGalleryAdapter bottomGalleryAdapter = new BottomGalleryAdapter(
                this.getSupportFragmentManager());
        stickerFragment = StickerFragment.newInstance();
        filterListFragment = FilterListFragment.newInstance();
        cropFragment = CropFragment.newInstance();
        rotateFragment = RotateFragment.newInstance();
        paintFragment = PaintFragment.newInstance();
        beautyFragment = BeautyFragment.newInstance();
        brightnessFragment = BrightnessFragment.newInstance();
        saturationFragment = SaturationFragment.newInstance();
        addTextFragment = AddTextFragment.newInstance();
        setOnMainBitmapChangeListener((OnMainBitmapChangeListener) addTextFragment);

        bottomGallery.setAdapter(bottomGalleryAdapter);


        mainImage.setFlingListener(new ImageViewTouch.OnImageFlingListener() {
            @Override
            public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY > 1) {
                    closeInputMethod();
                }
            }
        });

        redoUndoController = new RedoUndoController(this, findViewById(R.id.redo_undo_panel));

        if (!PermissionUtils.hasPermissions(this, mRequiredPermissions)) {
            ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    private void setOnMainBitmapChangeListener(OnMainBitmapChangeListener listener) {
        onMainBitmapChangeListener = listener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lock orientation for this activity
        if (isPortraitForced) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setLockScreenOrientation(true);
        }
    }

    private void closeInputMethod() {
        if (addTextFragment.isAdded()) {
            addTextFragment.hideInput();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        switch (mode) {
            case MODE_STICKERS:
                stickerFragment.backToMain();
                break;
            case MODE_FILTER:
                filterListFragment.backToMain();
                break;
            case MODE_CROP:
                cropFragment.backToMain();
                break;
            case MODE_ROTATE:
                rotateFragment.backToMain();
                break;
            case MODE_TEXT:
                addTextFragment.backToMain();
                break;
            case MODE_PAINT:
                paintFragment.backToMain();
                break;
            case MODE_BEAUTY:
                beautyFragment.backToMain();
                break;
            case MODE_BRIGHTNESS:
                brightnessFragment.backToMain();
                break;
            case MODE_SATURATION:
                saturationFragment.backToMain();
                break;
            default:
                if (canAutoExit()) {
                    onSaveTaskDone();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage(R.string.exit_without_save)
                            .setCancelable(false).setPositiveButton(R.string.confirm, (dialog, id) -> mContext.finish()).setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;
        }
    }

    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            Log.e("Image Editor", "Image is null");
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    /**
     * @param newBit
     * @param needPushUndoStack
     */
    public void changeMainBitmap(Bitmap newBit, boolean needPushUndoStack) {
        if (newBit == null)
            return;

        if (mainBitmap == null || mainBitmap != newBit) {
            if (needPushUndoStack) {
                redoUndoController.switchMainBit(mainBitmap, newBit);
                increaseOpTimes();
            }
            mainBitmap = newBit;
            mainImage.setImageBitmap(mainBitmap);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

            if (mode == MODE_TEXT) {
                onMainBitmapChangeListener.onMainBitmapChange();
            }
        }
    }

    protected void onSaveTaskDone() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ImageEditorIntentBuilder.SOURCE_PATH, sourceFilePath);
        returnIntent.putExtra(ImageEditorIntentBuilder.OUTPUT_PATH, outputFilePath);
        returnIntent.putExtra(IMAGE_IS_EDIT, mOpTimes > 0);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    protected void doSaveImage() {
        if (mOpTimes <= 0)
            return;

        if (saveImageTask != null) {
            saveImageTask.cancel(true);
        }

        saveImageTask = new SaveImageTask();
        saveImageTask.execute(mainBitmap);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            changeMainBitmap(result, false);
        }
    }

    private final class SaveBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mOpTimes == 0) {
                onSaveTaskDone();
            } else {
                doSaveImage();
            }
        }
    }

    /**
     * @author panyi
     */
    private final class BottomGalleryAdapter extends FragmentPagerAdapter {
        public BottomGalleryAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case MainMenuFragment.INDEX:
                    return mainMenuFragment;
                case StickerFragment.INDEX:
                    return stickerFragment;
                case FilterListFragment.INDEX:
                    return filterListFragment;
                case CropFragment.INDEX:
                    return cropFragment;
                case RotateFragment.INDEX:
                    return rotateFragment;
                case AddTextFragment.INDEX:
                    return addTextFragment;
                case PaintFragment.INDEX:
                    return paintFragment;
                case BeautyFragment.INDEX:
                    return beautyFragment;
                case BrightnessFragment.INDEX:
                    return brightnessFragment;
                case SaturationFragment.INDEX:
                    return saturationFragment;
            }
            return MainMenuFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }

        if (saveImageTask != null) {
            saveImageTask.cancel(true);
        }

        if (redoUndoController != null) {
            redoUndoController.onDestroy();
        }
        if (isPortraitForced) {

        } else {
            setLockScreenOrientation(false);
        }
    }

    protected void setLockScreenOrientation(boolean lock) {
        if (Build.VERSION.SDK_INT >= 18) {
            setRequestedOrientation(lock ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            return;
        }

        if (lock) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case 0:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break; // value 1
                case 2:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break; // value 9
                case 1:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break; // value 0
                case 3:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break; // value 8
            }
        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // value 10
    }

    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public void resetOpTimes() {
        isBeenSaved = true;
    }

    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    private final class ApplyBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (mode) {
                case MODE_STICKERS:
                    stickerFragment.applyStickers();
                    break;
                case MODE_FILTER:
                    filterListFragment.applyFilterImage();
                    break;
                case MODE_CROP:
                    cropFragment.applyCropImage();
                    break;
                case MODE_ROTATE:
                    rotateFragment.applyRotateImage();
                    break;
                case MODE_TEXT:
                    addTextFragment.applyTextImage();
                    break;
                case MODE_PAINT:
                    paintFragment.savePaintImage();
                    break;
                case MODE_BEAUTY:
                    beautyFragment.applyBeauty();
                    break;
                case MODE_BRIGHTNESS:
                    brightnessFragment.applyBrightness();
                    break;
                case MODE_SATURATION:
                    saturationFragment.applySaturation();
                    break;
                default:
                    break;
            }
        }
    }

    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(outputFilePath))
                return false;

            return BitmapUtils.saveBitmap(params[0], outputFilePath);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = EditImageActivity.getLoadingDialog(mContext, R.string.saving_image, false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result) {
                resetOpTimes();
                onSaveTaskDone();
            } else {
                Toast.makeText(mContext, R.string.save_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap getMainBit() {
        return mainBitmap;
    }
}