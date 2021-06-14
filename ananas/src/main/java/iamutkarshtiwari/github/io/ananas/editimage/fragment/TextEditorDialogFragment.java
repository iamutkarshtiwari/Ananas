package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.ColorPickerAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnTextEditorListener;

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();

    @NonNull
    private EditText addTextEditText;
    private TextView chooseFont;
    private String initialText;
    private int colorCode;
    private Typeface font;
    private int fontStyle;
    private final HashMap<String, Typeface> fonts;

    private InputMethodManager inputMethodManager;
    private OnTextEditorListener onTextEditorListener;

    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int initialColorCode,
                                                Typeface initialFont,
                                                int initialFontStyle,
                                                HashMap<String, Typeface> fonts) {
        TextEditorDialogFragment fragment =
                new TextEditorDialogFragment(inputText, initialColorCode, initialFont,
                        initialFontStyle, fonts);

        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    //Show dialog with default text input as empty and text color white
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                HashMap<String, Typeface> fonts) {
        return show(appCompatActivity, null, ContextCompat.getColor(appCompatActivity, R.color.white),
                null, 0, fonts);
    }

    private TextEditorDialogFragment(@NonNull String inputText,
                                     @ColorInt int initialColorCode,
                                     Typeface initialFont,
                                     int initialFontStyle,
                                     HashMap<String, Typeface> fonts) {
        this.initialText = inputText;
        this.colorCode = initialColorCode;
        this.font = initialFont;
        this.fontStyle = initialFontStyle;
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
        return inflater.inflate(R.layout.dialog_edit_text_sticker, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        addTextEditText = view.findViewById(R.id.add_text_edit_text);
        chooseFont = view.findViewById(R.id.add_text_choose_font);
        TextView addTextDoneTv = view.findViewById(R.id.add_text_done_tv);
        ImageView boldButton = view.findViewById(R.id.add_text_bold);
        ImageView italicButton = view.findViewById(R.id.add_text_italic);

        //Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getContext());

        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            this.colorCode = colorCode;
            addTextEditText.setTextColor(colorCode);
        });

        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);

        addTextEditText.setText(initialText);
        addTextEditText.setTextColor(colorCode);

        if (font == null) {
            font = chooseFont.getTypeface();
        } else {
            addTextEditText.setTypeface(font, fontStyle);
            chooseFont.setTypeface(font);
        }

        if (fonts != null && fonts.size() > 0) {
            boolean fontFound = false;

            for (Map.Entry<String, Typeface> fontOpt : fonts.entrySet()) {
                if (fontOpt.getValue().equals(font)) {
                    chooseFont.setText(fontOpt.getKey());
                    fontFound = true;
                    break;
                }
            }

            if (!fontFound) {
                fonts.put(getString(R.string.iamutkarshtiwari_github_io_ananas_default_font_name), font);
                chooseFont.setText(getString(R.string.iamutkarshtiwari_github_io_ananas_default_font_name));
            }

            chooseFont.setOnClickListener(v -> {
                inputMethodManager.hideSoftInputFromWindow(addTextEditText.getWindowToken(), 0);

                FontChooserDialogFragment fontChooserDialogFragment =
                        FontChooserDialogFragment.show((AppCompatActivity) getActivity(), addTextEditText.getText().toString(),
                                colorCode, font, fontStyle, fonts);

                fontChooserDialogFragment.setOnFontChosenListener((fontName, font) -> {
                    this.font = font;

                    chooseFont.setText(fontName);
                    chooseFont.setTypeface(font);

                    addTextEditText.setTypeface(font, fontStyle);
                });
            });

            toggleFontStyleButton(boldButton, fontIsBold());
            boldButton.setOnClickListener(v -> {
                boolean toBold = !fontIsBold();
                setFontStyle(toBold, fontIsItalic());
                toggleFontStyleButton(boldButton, toBold);
            });

            toggleFontStyleButton(italicButton, fontIsItalic());
            italicButton.setOnClickListener(v -> {
                boolean toItalic = !fontIsItalic();
                setFontStyle(fontIsBold(), toItalic);
                toggleFontStyleButton(italicButton, toItalic);
            });
        } else {
            chooseFont.setVisibility(View.GONE);
            boldButton.setVisibility(View.GONE);
            italicButton.setVisibility(View.GONE);
        }

        //Make a callback on activity when user is done with text editing
        addTextDoneTv.setOnClickListener(view1 -> {
            inputMethodManager.hideSoftInputFromWindow(view1.getWindowToken(), 0);

            String inputText = addTextEditText.getText().toString();
            if (!TextUtils.isEmpty(inputText) && onTextEditorListener != null) {
                onTextEditorListener.onDone(inputText, colorCode, font, fontStyle);
            }

            dismiss();
        });
    }

    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(OnTextEditorListener onTextEditorListener) {
        this.onTextEditorListener = onTextEditorListener;
    }

    private void toggleFontStyleButton(ImageView button, boolean toPressed) {
        if (toPressed) {
            button.setBackground(getResources().getDrawable(R.drawable.background_rounded_fill));
            button.setColorFilter(getResources().getColor(android.R.color.black));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.background_border));
            button.setColorFilter(null);
        }
    }

    private void setFontStyle(boolean bold, boolean italic) {
        if (bold && italic) {
            fontStyle = Typeface.BOLD_ITALIC;
        } else if (bold) {
            fontStyle = Typeface.BOLD;
        } else if (italic) {
            fontStyle = Typeface.ITALIC;
        } else {
            fontStyle = Typeface.NORMAL;
            // Workaround as the following does nothing:
            // addTextEditText.setTypeface(addTextEditText.getTypeface(), Typeface.NORMAL);
            //Typeface font = Typeface.create(chooseFont.getTypeface(), Typeface.NORMAL);
            //
        }

        addTextEditText.setTypeface(font, fontStyle);
    }

    private boolean fontIsBold() {
        return fontStyle == Typeface.BOLD || fontStyle == Typeface.BOLD_ITALIC;
    }

    private boolean fontIsItalic() {
        return fontStyle == Typeface.ITALIC || fontStyle == Typeface.BOLD_ITALIC;
    }
}
