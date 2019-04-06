package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import iamutkarshtiwari.github.io.ananas.R;

public class ColorListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int TYPE_COLOR = 1;
    private static final int TYPE_MORE = 2;

    public interface IColorListAction {
        void onColorSelected(final int position, final int color);
        void onMoreSelected(final int position);
    }

    private int[] colorsData;
    private IColorListAction actionCallback;

    public ColorListAdapter(int[] colors, IColorListAction action) {
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
        holder.moreBtn.setOnClickListener(v -> {
            if (actionCallback != null) {
                actionCallback.onMoreSelected(position);
            }
        });
    }

    public class ColorViewHolder extends ViewHolder {
        View colorPanelView;

        ColorViewHolder(View itemView) {
            super(itemView);
            this.colorPanelView = itemView.findViewById(R.id.color_panel_view);
        }
    }

    public class MoreViewHolder extends ViewHolder {
        View moreBtn;

        MoreViewHolder(View itemView) {
            super(itemView);
            this.moreBtn = itemView.findViewById(R.id.color_panel_more);
        }

    }
}
