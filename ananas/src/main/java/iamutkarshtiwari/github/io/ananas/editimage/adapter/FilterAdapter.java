package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.FilterListFragment;

/**
 * Created by iamutkarshtiwari on 2018/03/13.
 */

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String[] filters;
    String[] filterImages;
    private FilterListFragment mFilterListFragment;
    private Context mContext;

    public FilterAdapter(FilterListFragment fragment, Context context) {
        super();
        this.mFilterListFragment = fragment;
        this.mContext = context;
        filters = mFilterListFragment.getResources().getStringArray(R.array.filters);
        filterImages = mFilterListFragment.getResources().getStringArray(R.array.filter_drawable_list);
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView text;

        public ImageHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.filter_image);
            this.text = (TextView) itemView.findViewById(R.id.filter_name);
        }
    }

    @Override
    public int getItemCount() {
        return filterImages.length;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.filter_item, parent, false);
        return new ImageHolder(view);
    }

    /**
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        final int position = pos;
        ImageHolder imageHolder = (ImageHolder) holder;
        String name = filters[position];
        imageHolder.text.setText(name);

        String imageUrl = "drawable/" + filterImages[position];
        int imageKey = mFilterListFragment.getResources().getIdentifier(imageUrl, "drawable", mContext.getPackageName());
        imageHolder.icon.setImageDrawable(mFilterListFragment.getResources().getDrawable(imageKey));

        imageHolder.icon.setTag(position);
        imageHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterListFragment.enableFilter(position);
            }
        });
    }
}
