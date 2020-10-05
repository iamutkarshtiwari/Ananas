package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.view.TextStickerItem;
import iamutkarshtiwari.github.io.ananas.editimage.view.TextStickerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AddTextFragment extends BaseEditFragment implements View.OnClickListener {

    public static final int INDEX = ModuleConfig.INDEX_ADDTEXT;
    public static final String TAG = AddTextFragment.class.getName();

    private View mainView;
    private TextStickerView textStickersParentView;

    private InputMethodManager inputMethodManager;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static AddTextFragment newInstance() {
        return new AddTextFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_add_text, container, false);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditImageActivity editImageActivity = ensureEditActivity();

        inputMethodManager = (InputMethodManager) editImageActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        textStickersParentView = editImageActivity.findViewById(R.id.text_sticker_panel);
        textStickersParentView.setDrawingCacheEnabled(true);
        textStickersParentView.setEditTextListener(item -> showTextEditDialog(item));

        View backToMenu = mainView.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        LinearLayout addTextButton = mainView.findViewById(R.id.add_text_btn);
        addTextButton.setOnClickListener(this);
    }

    private void showTextEditDialog(final TextStickerItem textItem) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(activity, textItem.text, textItem.fontPaint.getColor());

        textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode1) ->
                textItem.update(inputText, colorCode1));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_text_btn) {
            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(activity);
            textEditorDialogFragment.setOnTextEditorListener(this::addText);
        }
    }

    public void hideInput() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null && isInputMethodShow()) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isInputMethodShow() {
        return inputMethodManager.isActive();
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void backToMain() {
        hideInput();
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        textStickersParentView.clear();
        textStickersParentView.setVisibility(View.GONE);
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TEXT;
        activity.bannerFlipper.showNext();
        textStickersParentView.setVisibility(View.VISIBLE);
    }

    public void applyTextImage() {
        compositeDisposable.clear();
        textStickersParentView.hideHelper();

        Disposable saveStickerDisposable = applyStickerToImage(activity.getMainBit())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    if (bitmap == null) {
                        return;
                    }

                    textStickersParentView.clear();
                    activity.changeMainBitmap(bitmap, true);
                    backToMain();
                }, e -> {
                    Toast.makeText(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_save_error, Toast.LENGTH_SHORT).show();
                });

        compositeDisposable.add(saveStickerDisposable);
    }

    private Single<Bitmap> applyStickerToImage(Bitmap mainBitmap) {
        return Single.fromCallable(() -> {
            Bitmap resultBitmap = Bitmap.createBitmap(mainBitmap).copy(
                    Bitmap.Config.ARGB_8888, true);

            handleImage(new Canvas(resultBitmap));
            return resultBitmap;
        });
    }

    private void handleImage(Canvas canvas) {
        LinkedHashMap<Integer, TextStickerItem> addItems = textStickersParentView.getBank();
        for (Integer id : addItems.keySet()) {
            TextStickerItem item = addItems.get(id);

            item.updateForCanvas(canvas, textStickersParentView);
            item.draw(canvas);
        }
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

    @SuppressLint("ClickableViewAccessibility")
    private void addText(String text, final int colorCodeTextView) {
        textStickersParentView.addText(text, colorCodeTextView);
    }
}
