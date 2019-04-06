package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import iamutkarshtiwari.github.io.ananas.R;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Integer> colorPickerColors;
    private OnColorPickerClickListener onColorPickerClickListener;

    public ColorPickerAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
        this.colorPickerColors = getKelly22Colors(context);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.color_picker_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        holder.colorPickerView.setBackgroundColor(colorPickerColors.get(position));
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }


    public void setOnColorPickerClickListener(OnColorPickerClickListener onColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View colorPickerView;

        ViewHolder(View itemView) {
            super(itemView);
            colorPickerView = itemView.findViewById(R.id.color_picker_view);
            itemView.setOnClickListener(v -> {
                if (onColorPickerClickListener != null)
                    onColorPickerClickListener.onColorPickerClickListener(colorPickerColors.get(getAdapterPosition()));
            });
        }
    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode);
    }

    private List<Integer> getKelly22Colors(Context context) {
        Resources resources = context.getResources();
        List<Integer> colorList = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            int resourceId = resources.getIdentifier("kelly_" + (i + 1), "color", context.getPackageName());
            colorList.add(resources.getColor(resourceId));
        }
        return colorList;
    }
}
