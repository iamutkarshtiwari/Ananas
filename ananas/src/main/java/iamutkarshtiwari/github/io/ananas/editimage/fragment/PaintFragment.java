package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.ColorListAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.ui.ColorPicker;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3;
import iamutkarshtiwari.github.io.ananas.editimage.view.CustomPaintView;
import iamutkarshtiwari.github.io.ananas.editimage.view.PaintModeView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PaintFragment extends BaseEditFragment implements View.OnClickListener, ColorListAdapter.IColorListAction {
    public static final int INDEX = ModuleConfig.INDEX_PAINT;
    public static final String TAG = PaintFragment.class.getName();

    private View mainView;

    public final int[] paintColors = {
            Color.BLACK,
            Color.DKGRAY,
            Color.GRAY,
            Color.LTGRAY,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
    };
    public boolean isEraser = false;

    private View backToMenu;
    private View popView;
    private PaintModeView paintModeView;
    private RecyclerView colorListView;
    private CustomPaintView customPaintView;
    private PopupWindow setStrokeWidthWindow;
    private ColorPicker colorPicker;
    private ImageView eraserView;
    private SeekBar strokeWidthSeekBar;
    private Dialog loadingDialog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static PaintFragment newInstance() {
        return new PaintFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_paint, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadingDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.loading,
                false);
        customPaintView = getActivity().findViewById(R.id.custom_paint_view);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        paintModeView = mainView.findViewById(R.id.paint_thumb);
        colorListView = mainView.findViewById(R.id.paint_color_list);
        eraserView = mainView.findViewById(R.id.paint_eraser);

        backToMenu.setOnClickListener(this);

        colorPicker = new ColorPicker(getActivity(), 255, 0, 0);
        initColorListView();
        paintModeView.setOnClickListener(this);

        initStrokeWidthPopWindow();

        eraserView.setOnClickListener(this);
        updateEraserView();
    }

    private void initColorListView() {
        colorListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        colorListView.setLayoutManager(stickerListLayoutManager);
        ColorListAdapter colorListAdapter = new ColorListAdapter(this, paintColors, this);
        colorListView.setAdapter(colorListAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == backToMenu) {
            backToMain();
        } else if (view == paintModeView) {
            setStokeWidth();
        } else if (view == eraserView) {
            toggleEraserView();
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

        customPaintView.setVisibility(View.GONE);
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_PAINT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();

        customPaintView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onColorSelected(int position, int color) {
        setPaintColor(color);
    }

    @Override
    public void onMoreSelected(int position) {
        colorPicker.show();
        Button okColor = colorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(v -> {
            setPaintColor(colorPicker.getColor());
            colorPicker.dismiss();
        });
    }

    protected void setPaintColor(final int paintColor) {
        paintModeView.setPaintStrokeColor(paintColor);

        updatePaintView();
    }

    private void updatePaintView() {
        isEraser = false;
        updateEraserView();

        customPaintView.setColor(paintModeView.getStokenColor());
        customPaintView.setWidth(paintModeView.getStokenWidth());
    }

    protected void setStokeWidth() {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        strokeWidthSeekBar.setMax(paintModeView.getMeasuredHeight());
        strokeWidthSeekBar.setProgress((int) paintModeView.getStokenWidth());
        strokeWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintModeView.setPaintStrokeWidth(progress);
                updatePaintView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        int[] locations = new int[2];
        activity.bottomGallery.getLocationOnScreen(locations);
        setStrokeWidthWindow.showAtLocation(activity.bottomGallery,
                Gravity.NO_GRAVITY, 0, locations[1] - popView.getMeasuredHeight());
    }

    private void initStrokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).
                inflate(R.layout.view_set_stoke_width, null);
        setStrokeWidthWindow = new PopupWindow(
                popView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        strokeWidthSeekBar = popView.findViewById(R.id.stoke_width_seekbar);

        setStrokeWidthWindow.setFocusable(true);
        setStrokeWidthWindow.setOutsideTouchable(true);
        setStrokeWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStrokeWidthWindow.setAnimationStyle(R.style.popwin_anim_style);

        paintModeView.setPaintStrokeColor(Color.WHITE);
        paintModeView.setPaintStrokeWidth(20);

        updatePaintView();
    }

    private void toggleEraserView() {
        isEraser = !isEraser;
        updateEraserView();
    }

    private void updateEraserView() {
        eraserView.setImageResource(isEraser ? R.drawable.eraser_seleced : R.drawable.eraser_normal);
        customPaintView.setEraser(isEraser);
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
}
