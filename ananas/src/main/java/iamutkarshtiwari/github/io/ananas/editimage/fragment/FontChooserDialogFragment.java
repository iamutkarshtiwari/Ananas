package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import iamutkarshtiwari.github.io.ananas.R;
import in.goodiebag.carouselpicker.CarouselPicker;

public class FontChooserDialogFragment extends DialogFragment {

    public static final String TAG = FontChooserDialogFragment.class.getSimpleName();
    @NonNull
    private final String text;
    private final int color;
    private final Typeface initialFont;
    private final int fontStyle;
    @NonNull
    private final HashMap<String, Typeface> fonts;

    private OnFontChosenListener onFontChosenListener;

    //Show dialog with provide text and text color
    public static FontChooserDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                 @NonNull String text,
                                                 @ColorInt int color,
                                                 Typeface initialFont,
                                                 int fontStyle,
                                                 @NonNull HashMap<String, Typeface> fonts) {
        FontChooserDialogFragment fragment =
                new FontChooserDialogFragment(text, color, initialFont, fontStyle, fonts);

        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    private FontChooserDialogFragment(@NonNull String text,
                                      @ColorInt int color,
                                      Typeface initialFont,
                                      int fontStyle,
                                      @NonNull HashMap<String, Typeface> fonts) {

        this.text = text;
        this.color = color;
        this.initialFont = initialFont;
        this.fontStyle = fontStyle;
        this.fonts = fonts;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            if (window != null) {
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_choose_font, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView doneTv = view.findViewById(R.id.choose_font_done_tv);
        CarouselPicker carouselPicker = view.findViewById(R.id.choose_font_carousel);

        int initialFontPosition = -1;
        int count = 0;

        carouselPicker.setOffscreenPageLimit(9);
        TreeSet<String> fontNames = new TreeSet<>(fonts.keySet());
        List<CarouselPicker.PickerItem> textItems = new ArrayList<>();

        for (String key : fontNames) {
            String name = key;
            Typeface font = fonts.get(key);

            if (initialFontPosition == -1 && font.equals(initialFont)) {
                initialFontPosition = count;
            }

            if (!TextUtils.isEmpty(text)) {
                name = text;
            }

            // Surround with spaces as there is a bug in TextView that cuts of the start
            // and end of fonts, particularly when using the italic style
            name = " " + name + " ";

            textItems.add(new CarouselPicker.TextItem(name, 20, color, font, CarouselPicker.TextItem.FontStyle.values()[fontStyle]));
            count++;
        }

        CarouselPicker.CarouselViewAdapter textAdapter =
                new CarouselPicker.CarouselViewAdapter(getContext(), textItems, 0);

        carouselPicker.setAdapter(textAdapter);
        carouselPicker.setCurrentItem(initialFontPosition);

        //Make a callback on activity when user is done with text editing
        doneTv.setOnClickListener(view1 -> {
            if (onFontChosenListener != null) {
                String fontName = new ArrayList<String>(fontNames).get(carouselPicker.getCurrentItem());
                Typeface font = ((CarouselPicker.TextItem) textItems.get(carouselPicker.getCurrentItem())).getFont();

                onFontChosenListener.onDone(fontName, font);
            }
            dismiss();
        });
    }

    //Callback to listener if user is done choosing font
    public void setOnFontChosenListener(OnFontChosenListener onFontChosenListener) {
        this.onFontChosenListener = onFontChosenListener;
    }

    public interface OnFontChosenListener {
        void onDone(String name, Typeface font);
    }
}
