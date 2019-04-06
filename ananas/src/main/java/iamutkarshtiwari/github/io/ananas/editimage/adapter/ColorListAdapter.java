package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.viewholders.ColorViewHolder;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.viewholders.MoreViewHolder;
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnColorSelected;

public class ColorListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int TYPE_COLOR = 1;
    private static final int TYPE_MORE = 2;

    private int[] colorsData;
    private OnColorSelected actionCallback;

    public ColorListAdapter(int[] colors, OnColorSelected action) {
        super();
        this.colorsData = colors;
        this.actionCallback = action;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;
        if (viewType == TYPE_COLOR) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.view_color_panel, parent, false);
            viewHolder = new ColorViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_color_more_panel, parent, false);
            viewHolder = new MoreViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_COLOR) {
            onBindColorViewHolder((ColorViewHolder) holder, position);
        } else if (type == TYPE_MORE) {
            onBindColorMoreViewHolder((MoreViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return colorsData.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return colorsData.length == position ? TYPE_MORE : TYPE_COLOR;
    }

    private void onBindColorViewHolder(final ColorViewHolder holder, final int position) {
        holder.colorPanelView.setBackgroundColor(colorsData[position]);
        holder.colorPanelView.setOnClickListener(v -> {
            if (actionCallback != null) {
                actionCallback.onColorSelected(position, colorsData[position]);
            }
        });
    }

    private void onBindColorMoreViewHolder(final MoreViewHolder holder, final int position) {
        holder.moreButton.setOnClickListener(v -> {
            if (actionCallback != null) {
                actionCallback.onMoreSelected(position);
            }
        });
    }
}
