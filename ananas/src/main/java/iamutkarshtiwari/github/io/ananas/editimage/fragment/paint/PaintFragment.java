package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.MainMenuFragment;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3;
import iamutkarshtiwari.github.io.ananas.editimage.view.CustomPaintView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PaintFragment extends BaseEditFragment implements View.OnClickListener, BrushConfigDialog.Properties, EraserConfigDialog.Properties {

    public static final int INDEX = ModuleConfig.INDEX_PAINT;
    public static final String TAG = PaintFragment.class.getName();

    private static final float MAX_PERCENT = 100;
    private static final float MAX_ALPHA = 255;
    private static final float INITIAL_WIDTH = 50;

    private View mainView;

    private boolean isEraser = false;

    private View backToMenu;
    private CustomPaintView customPaintView;
    private LinearLayout eraserView;
    private LinearLayout brushView;

    private BrushConfigDialog brushConfigDialog;
    private EraserConfigDialog eraserConfigDialog;
    private Dialog loadingDialog;

    private float brushSize = INITIAL_WIDTH;
    private float eraserSize = INITIAL_WIDTH;
    private float brushAlpha = MAX_ALPHA;
    private int brushColor = Color.WHITE;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static PaintFragment newInstance() {
        return new PaintFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_paint, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadingDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_loading,
                false);
        customPaintView = ensureEditActivity().findViewById(R.id.custom_paint_view);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        eraserView = mainView.findViewById(R.id.eraser_btn);
        brushView = mainView.findViewById(R.id.brush_btn);
        mainView.findViewById(R.id.settings).setOnClickListener(this);

        setupOptionsConfig();

        backToMenu.setOnClickListener(this);

        setClickListeners();
        initStroke();
    }

    private void setupOptionsConfig() {
        brushConfigDialog = new BrushConfigDialog();
        brushConfigDialog.setPropertiesChangeListener(this);

        eraserConfigDialog = new EraserConfigDialog();
        eraserConfigDialog.setPropertiesChangeListener(this);
    }

    private void setClickListeners() {
        brushView.setOnClickListener(this);
        eraserView.setOnClickListener(this);
    }

    private void initStroke() {
        customPaintView.setWidth(INITIAL_WIDTH);
        customPaintView.setColor(Color.WHITE);
        customPaintView.setStrokeAlpha(MAX_ALPHA);
        customPaintView.setEraserStrokeWidth(INITIAL_WIDTH);
    }

    @Override
    public void onClick(View view) {
        if (view == backToMenu) {
            backToMain();
        } else if (view == eraserView) {
            if (!isEraser) {
                toggleButtons();
            }
        } else if (view == brushView) {
            if (isEraser) {
                toggleButtons();
            }
        } else if (view.getId() == R.id.settings) {
            showDialog(isEraser ? eraserConfigDialog : brushConfigDialog);
        }
    }

    private void showDialog(BottomSheetDialogFragment dialogFragment) {
        String tag = dialogFragment.getTag();

        // Avoid IllegalStateException "Fragment already added"
        if (dialogFragment.isAdded()) return;

        dialogFragment.show(requireFragmentManager(), tag);

        if (isEraser) {
            updateEraserSize();
        } else {
            updateBrushParams();
        }
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();

        customPaintView.reset();
        customPaintView.setVisibility(View.GONE);
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_PAINT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();

        customPaintView.setVisibility(View.VISIBLE);
    }

    private void toggleButtons() {
        isEraser = !isEraser;
        customPaintView.setEraser(isEraser);
        ((ImageView) eraserView.findViewById(R.id.eraser_icon)).setImageResource(isEraser ? R.drawable.ic_eraser_enabled : R.drawable.ic_eraser_disabled);
        ((ImageView) brushView.findViewById(R.id.brush_icon)).setImageResource(isEraser ? R.drawable.ic_brush_grey_24dp : R.drawable.ic_brush_white_24dp);
    }

    public void savePaintImage() {
        compositeDisposable.clear();

        Disposable applyPaintDisposable = applyPaint(activity.getMainBit())
                .flatMap(bitmap -> {
                    if (bitmap == null) {
                        return Single.error(new Throwable("Error occurred while applying paint"));
                    } else {
                        return Single.just(bitmap);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialog.show())
                .doFinally(() -> loadingDialog.dismiss())
                .subscribe(bitmap -> {
                    customPaintView.reset();
                    activity.changeMainBitmap(bitmap, true);
                    backToMain();
                }, e -> {
                    // Do nothing on error
                });

        compositeDisposable.add(applyPaintDisposable);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private Single<Bitmap> applyPaint(Bitmap mainBitmap) {
        return Single.fromCallable(() -> {
            Matrix touchMatrix = activity.mainImage.getImageViewMatrix();

            Bitmap resultBit = Bitmap.createBitmap(mainBitmap).copy(
                    Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(resultBit);

            float[] data = new float[9];
            touchMatrix.getValues(data);
            Matrix3 cal = new Matrix3(data);
            Matrix3 inverseMatrix = cal.inverseMatrix();
            Matrix matrix = new Matrix();
            matrix.setValues(inverseMatrix.getValues());

            handleImage(canvas, matrix);

            return resultBit;
        });
    }

    private void handleImage(Canvas canvas, Matrix matrix) {
        float[] f = new float[9];
        matrix.getValues(f);

        int dx = (int) f[Matrix.MTRANS_X];
        int dy = (int) f[Matrix.MTRANS_Y];

        float scale_x = f[Matrix.MSCALE_X];
        float scale_y = f[Matrix.MSCALE_Y];

        canvas.save();
        canvas.translate(dx, dy);
        canvas.scale(scale_x, scale_y);

        if (customPaintView.getPaintBit() != null) {
            canvas.drawBitmap(customPaintView.getPaintBit(), 0, 0, null);
        }
        canvas.restore();
    }

    @Override
    public void onColorChanged(int colorCode) {
        brushColor = colorCode;
        updateBrushParams();
    }

    @Override
    public void onOpacityChanged(int opacity) {
        brushAlpha = (opacity / MAX_PERCENT) * MAX_ALPHA;
        updateBrushParams();
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        if (isEraser) {
            this.eraserSize = brushSize;
            updateEraserSize();
        } else {
            this.brushSize = brushSize;
            updateBrushParams();
        }
    }

    private void updateBrushParams() {
        customPaintView.setColor(brushColor);
        customPaintView.setWidth(brushSize);
        customPaintView.setStrokeAlpha(brushAlpha);
    }

    private void updateEraserSize() {
        customPaintView.setEraserStrokeWidth(eraserSize);
    }
}
