package iamutkarshtiwari.github.io.ananas.editimage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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
import iamutkarshtiwari.github.io.ananas.editimage.utils.BitmapUtils;
import iamutkarshtiwari.github.io.ananas.editimage.utils.FileUtil;
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
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";

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

    public String filePath;
    public String saveFilePath;
    private int imageWidth, imageHeight;
    private LoadImageTask mLoadImageTask;

    public int mode = MODE_NONE;

    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;

    private EditImageActivity mContext;
    private Bitmap mainBitmap;
    public ImageViewTouch mainImage;
    private View backBtn;

    public ViewFlipper bannerFlipper;
    private View applyBtn;
    private View saveBtn;

    public StickerView mStickerView;
    public CropImageView mCropPanel;
    public RotateImageView mRotatePanel;
    public TextStickerView mTextStickerView;
    public CustomPaintView mPaintView;
    public BrightnessView mBrightnessView;
    public SaturationView mSaturationView;

    public CustomViewPager bottomGallery;
    private BottomGalleryAdapter mBottomGalleryAdapter;
    private MainMenuFragment mMainMenuFragment;
    public StickerFragment mStickerFragment;
    public FilterListFragment mFilterListFragment;
    public CropFragment mCropFragment;
    public RotateFragment mRotateFragment;
    public AddTextFragment mAddTextFragment;
    public PaintFragment mPaintFragment;
    public BeautyFragment mBeautyFragment;
    private SaveImageTask mSaveImageTask;
    public BrightnessFragment mBrightnessFragment;
    public SaturationFragment mSaturationFragment;

    private RedoUndoController mRedoUndoController;

    /**
     * @param context
     * @param editImagePath
     * @param outputPath
     * @param requestCode
     */
    public static void start(Activity context, final String editImagePath, final String outputPath, final int requestCode) {
        if (TextUtils.isEmpty(editImagePath)) {
            Toast.makeText(context, R.string.no_choose, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(context, EditImageActivity.class);
        it.putExtra(EditImageActivity.FILE_PATH, editImagePath);
        it.putExtra(EditImageActivity.EXTRA_OUTPUT, outputPath);
        context.startActivityForResult(it, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lock orientation for this activity
        setLockScreenOrientation(true);
        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        initView();
        getData();
    }

    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        loadImage(filePath);
    }

    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        bannerFlipper = (ViewFlipper) findViewById(R.id.banner_flipper);
        bannerFlipper.setInAnimation(this, R.anim.in_bottom_to_top);
        bannerFlipper.setOutAnimation(this, R.anim.out_bottom_to_top);
        applyBtn = findViewById(R.id.apply);
        applyBtn.setOnClickListener(new ApplyBtnClick());
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new SaveBtnClick());

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mStickerView = (StickerView) findViewById(R.id.sticker_panel);
        mCropPanel = (CropImageView) findViewById(R.id.crop_panel);
        mRotatePanel = (RotateImageView) findViewById(R.id.rotate_panel);
        mTextStickerView = (TextStickerView) findViewById(R.id.text_sticker_panel);
        mPaintView = (CustomPaintView) findViewById(R.id.custom_paint_view);
        mBrightnessView = (BrightnessView) findViewById(R.id.brightness_panel);
        mSaturationView = (SaturationView) findViewById(R.id.contrast_panel);

        bottomGallery = (CustomViewPager) findViewById(R.id.bottom_gallery);
        mMainMenuFragment = MainMenuFragment.newInstance();
        mBottomGalleryAdapter = new BottomGalleryAdapter(
                this.getSupportFragmentManager());
        mStickerFragment = StickerFragment.newInstance();
        mFilterListFragment = FilterListFragment.newInstance();
        mCropFragment = CropFragment.newInstance();
        mRotateFragment = RotateFragment.newInstance();
        mAddTextFragment = AddTextFragment.newInstance();
        mPaintFragment = PaintFragment.newInstance();
        mBeautyFragment = BeautyFragment.newInstance();
        mBrightnessFragment = BrightnessFragment.newInstance();
        mSaturationFragment = SaturationFragment.newInstance();

        bottomGallery.setAdapter(mBottomGalleryAdapter);


        mainImage.setFlingListener(new ImageViewTouch.OnImageFlingListener() {
            @Override
            public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY > 1) {
                    closeInputMethod();
                }
            }
        });

        mRedoUndoController = new RedoUndoController(this, findViewById(R.id.redo_undo_panel));

        if (!PermissionUtils.hasPermissions(this, mRequiredPermissions)) {
            ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        }
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

    private void closeInputMethod() {
        if (mAddTextFragment.isAdded()) {
            mAddTextFragment.hideInput();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                    return mMainMenuFragment;
                case StickerFragment.INDEX:
                    return mStickerFragment;
                case FilterListFragment.INDEX:
                    return mFilterListFragment;
                case CropFragment.INDEX:
                    return mCropFragment;
                case RotateFragment.INDEX:
                    return mRotateFragment;
                case AddTextFragment.INDEX:
                    return mAddTextFragment;
                case PaintFragment.INDEX:
                    return mPaintFragment;
                case BeautyFragment.INDEX:
                    return mBeautyFragment;
                case BrightnessFragment.INDEX:
                    return mBrightnessFragment;
                case SaturationFragment.INDEX:
                    return mSaturationFragment;
            }
            return MainMenuFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 10;
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

    @Override
    public void onBackPressed() {
        switch (mode) {
            case MODE_STICKERS:
                mStickerFragment.backToMain();
                break;
            case MODE_FILTER:
                mFilterListFragment.backToMain();
                break;
            case MODE_CROP:
                mCropFragment.backToMain();
                break;
            case MODE_ROTATE:
                mRotateFragment.backToMain();
                break;
            case MODE_TEXT:
                mAddTextFragment.backToMain();
                break;
            case MODE_PAINT:
                mPaintFragment.backToMain();
                break;
            case MODE_BEAUTY:
                mBeautyFragment.backToMain();
                break;
            case MODE_BRIGHTNESS:
                mBrightnessFragment.backToMain();
                break;
            case MODE_SATURATION:
                mSaturationFragment.backToMain();
                break;
            default:
                break;
        }

        if (canAutoExit()) {
            onSaveTaskDone();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.exit_without_save)
                    .setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mContext.finish();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private final class ApplyBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (mode) {
                case MODE_STICKERS:
                    mStickerFragment.applyStickers();
                    break;
                case MODE_FILTER:
                    mFilterListFragment.applyFilterImage();
                    break;
                case MODE_CROP:
                    mCropFragment.applyCropImage();
                    break;
                case MODE_ROTATE:
                    mRotateFragment.applyRotateImage();
                    break;
                case MODE_TEXT:
                    mAddTextFragment.applyTextImage();
                    break;
                case MODE_PAINT:
                    mPaintFragment.savePaintImage();
                    break;
                case MODE_BEAUTY:
                    mBeautyFragment.applyBeauty();
                    break;
                case MODE_BRIGHTNESS:
                    mBrightnessFragment.applyBrightness();
                    break;
                case MODE_SATURATION:
                    mSaturationFragment.applySaturation();
                    break;
                default:
                    break;
            }
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

    protected void doSaveImage() {
        if (mOpTimes <= 0)
            return;

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mainBitmap);
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
                mRedoUndoController.switchMainBit(mainBitmap, newBit);
                increaseOpTimes();
            }
            mainBitmap = newBit;
            mainImage.setImageBitmap(mainBitmap);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        if (mRedoUndoController != null) {
            mRedoUndoController.onDestroy();
        }
        setLockScreenOrientation(false);
    }

    protected void setLockScreenOrientation(boolean lock) {
        if (Build.VERSION.SDK_INT >= 18) {
            setRequestedOrientation(lock?ActivityInfo.SCREEN_ORIENTATION_LOCKED:ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            return;
        }

        if (lock) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case 0: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break; // value 1
                case 2: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); break; // value 9
                case 1: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break; // value 0
                case 3: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break; // value 8
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

    protected void onSaveTaskDone() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILE_PATH, filePath);
        returnIntent.putExtra(EXTRA_OUTPUT, saveFilePath);
        returnIntent.putExtra(IMAGE_IS_EDIT, mOpTimes > 0);

        FileUtil.ablumUpdate(this, saveFilePath);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(saveFilePath))
                return false;

            return BitmapUtils.saveBitmap(params[0], saveFilePath);
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
