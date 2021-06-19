package iamutkarshtiwari.github.io.imageeditorsample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import iamutkarshtiwari.github.io.ananas.editimage.utils.BitmapUtils;
import iamutkarshtiwari.github.io.imageeditorsample.imagepicker.activity.ImagePickerActivity;
import iamutkarshtiwari.github.io.imageeditorsample.imagepicker.utils.FileUtilsKt;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_PERMISSION_STORAGE = 1;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    private ImageView imgView;
    private Bitmap mainBitmap;
    private Dialog loadingDialog;

    private int imageWidth, imageHeight;
    private String path;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    ActivityResultLauncher<Intent> editResultLauncher;
    ActivityResultLauncher<Intent> pickResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActivityResultLaunchers();
        initView();
    }

    private void setupActivityResultLaunchers() {
        pickResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        handleSelectFromAlbum(data);
                    }
                });
        editResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        handleEditorImage(data);
                    }
                });
    }

    @Override
    protected void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        imgView = findViewById(R.id.img);

        View selectAlbum = findViewById(R.id.photo_picker);
        View editImage = findViewById(R.id.edit_image);
        selectAlbum.setOnClickListener(this);
        editImage.setOnClickListener(this);

        loadingDialog = BaseActivity.getLoadingDialog(this, R.string.iamutkarshtiwari_github_io_ananas_loading,
                false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_image:
                editImageClick();
                break;
            case R.id.photo_picker:
                selectFromAlbum();
                break;
        }
    }

    private void editImageClick() {
        File outputFile = FileUtilsKt.generateEditFile();
        try {
            Intent intent = new ImageEditorIntentBuilder(this, path, outputFile.getAbsolutePath())
                    .withAddText()
                    .withPaintFeature()
                    .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    .withBeautyFeature()
                    .withStickerFeature()
                    .withEditorTitle("Photo Editor")
                    .forcePortrait(true)
                    .setSupportActionBarVisibility(false)
                    .build();

            EditImageActivity.start(editResultLauncher, intent, this);
        } catch (Exception e) {
            Toast.makeText(this, R.string.iamutkarshtiwari_github_io_ananas_not_selected, Toast.LENGTH_SHORT).show();
            Log.e("Demo App", e.getMessage());
        }
    }

    private void selectFromAlbum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openAlbumWithPermissionsCheck();
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        pickResultLauncher.launch(intent);
    }

    private void openAlbumWithPermissionsCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);
            return;
        }
        openAlbum();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case SELECT_GALLERY_IMAGE_CODE:
//                    handleSelectFromAblum(data);
//                    break;
//                case TAKE_PHOTO_CODE:
//                    handleTakePhoto();
//                    break;
//                case ACTION_REQUEST_EDITIMAGE:
//                    handleEditorImage(data);
//                    break;
//            }
//        }
//    }

//    private void handleTakePhoto() {
//        if (photoFile != null) {
//            path = photoFile.getPath();
//            loadImage(path);
//        }
//    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);

        if (isImageEdit) {
            Toast.makeText(this, getString(R.string.ananas_image_editor_save_path, newFilePath), Toast.LENGTH_LONG).show();
        } else {
            newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
        }

        loadImage(newFilePath);
    }

    private void handleSelectFromAlbum(Intent data) {
        path = data.getStringExtra(ImagePickerActivity.BUNDLE_EXTRA_IMAGE_PATH);
        loadImage(path);
    }

    private void loadImage(String imagePath) {
        compositeDisposable.clear();
        Disposable applyRotationDisposable = loadBitmapFromFile(imagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialog.show())
                .doFinally(() -> loadingDialog.dismiss())
                .subscribe(
                        this::setMainBitmap,
                        e -> { e.printStackTrace();
                            Toast.makeText(
                                this, R.string.iamutkarshtiwari_github_io_ananas_load_error, Toast.LENGTH_SHORT).show();}
                );

        compositeDisposable.add(applyRotationDisposable);
    }

    private void setMainBitmap(Bitmap sourceBitmap) {
        if (mainBitmap != null) {
            mainBitmap.recycle();
            mainBitmap = null;
            System.gc();
        }
        mainBitmap = sourceBitmap;
        imgView.setImageBitmap(mainBitmap);
    }

    private Single<Bitmap> loadBitmapFromFile(String filePath) {
        return Single.fromCallable(() ->
                BitmapUtils.getSampledBitmap(
                        filePath,
                        imageWidth / 4,
                        imageHeight / 4
                )
        );
    }
}
