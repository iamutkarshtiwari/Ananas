package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.viewholders.FilterViewHolder;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.FilterListFragment;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int DEFAULT_TYPE = 1;

    private String[] filters;
    private String[] filterImages;
    private FilterListFragment filterListFragment;
    private Context context;

    public FilterAdapter(FilterListFragment fragment, Context context) {
        super();
        this.filterListFragment = fragment;
        this.context = context;
        filters = filterListFragment.getResources().getStringArray(R.array.iamutkarshtiwari_github_io_ananas_filters);
        filterImages = filterListFragment.getResources().getStringArray(R.array.iamutkarshtiwari_github_io_ananas_filter_drawable_list);
    }

    @Override
    public int getItemCount() {
        return filterImages.length;
    }

    @Override
    public int getItemViewType(int position) {
        return DEFAULT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.filter_item, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        final int position = pos;
        FilterViewHolder filterViewHolder = (FilterViewHolder) holder;
        String name = filters[position];
        filterViewHolder.text.setText(name);

        String imageUrl = "drawable/" + filterImages[position];
        int imageKey = filterListFragment.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
        filterViewHolder.icon.setImageDrawable(filterListFragment.getResources().getDrawable(imageKey));

        filterViewHolder.icon.setTag(position);
        filterViewHolder.icon.setOnClickListener(v -> filterListFragment.enableFilter(position));
    }
}
