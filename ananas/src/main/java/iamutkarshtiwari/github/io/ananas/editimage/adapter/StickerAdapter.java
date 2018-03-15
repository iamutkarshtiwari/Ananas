package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.StickerFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StickerAdapter extends RecyclerView.Adapter<ViewHolder> {
    public DisplayImageOptions imageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).showImageOnLoading(R.drawable.yd_image_tx)
            .build();

    private StickerFragment mStickerFragment;
    private ImageClick mImageClick = new ImageClick();
    private List<String> pathList = new ArrayList<String>();// 图片路径列表

    public StickerAdapter(StickerFragment fragment) {
        super();
        this.mStickerFragment = fragment;
    }

    public class ImageHolder extends ViewHolder {
        public ImageView image;

        public ImageHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.img);
        }
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_item, parent, false);
        ImageHolder holder = new ImageHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageHolder imageHolder = (ImageHolder) holder;
        String path = pathList.get(position);

        String imageUrl = "drawable/" + path;
        int imageKey = mStickerFragment.getResources().getIdentifier(imageUrl, "drawable", mStickerFragment.getContext().getPackageName());
        imageHolder.image.setImageDrawable(mStickerFragment.getResources().getDrawable(imageKey));
        imageHolder.image.setTag(imageUrl);
        imageHolder.image.setOnClickListener(mImageClick);
    }

    public void addStickerImages(String folderPath, int stickerCount) {
        pathList.clear();
        for (int i = 0; i < stickerCount; i++) {
            pathList.add(folderPath + "_" + Integer.toString(i+1));
        }
        this.notifyDataSetChanged();
    }

    private final class ImageClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            String data = (String) v.getTag();
            mStickerFragment.selectedStickerItem(data);
        }
    }
}
