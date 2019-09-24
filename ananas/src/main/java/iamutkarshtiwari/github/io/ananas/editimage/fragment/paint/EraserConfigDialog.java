package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import iamutkarshtiwari.github.io.ananas.R;

public class EraserConfigDialog extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    public EraserConfigDialog() {
        // Required empty public constructor
    }

    private Properties mProperties;

    public interface Properties {
        void onBrushSizeChanged(int brushSize);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eraser_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar eraserSizeSb = view.findViewById(R.id.sbSize);
        eraserSizeSb.setOnSeekBarChangeListener(this);
    }

    void setPropertiesChangeListener(Properties properties) {
        mProperties = properties;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int id = seekBar.getId();
        if (id == R.id.sbSize) {
            if (mProperties != null) {
                mProperties.onBrushSizeChanged(i);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
