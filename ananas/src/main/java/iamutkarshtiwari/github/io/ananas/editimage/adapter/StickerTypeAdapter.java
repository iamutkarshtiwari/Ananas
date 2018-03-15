package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.fragment.StickerFragment;


public class StickerTypeAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int[] typeIcon = {R.drawable.stickers_type_animal,
            R.drawable.stickers_type_motion, R.drawable.stickers_type_cos,
            R.drawable.stickers_type_mark, R.drawable.stickers_type_decoration};
    private String[] stickerPath ;
    private String[] stickerPathName;
    private int[] stickerCount;
    private StickerFragment mStickerFragment;
    private ImageClick mImageClick = new ImageClick();

    public StickerTypeAdapter(StickerFragment fragment) {
        super();
        this.mStickerFragment = fragment;
        stickerPath = mStickerFragment.getResources().getStringArray(R.array.types);
        stickerPathName = mStickerFragment.getResources().getStringArray(R.array.type_names);
        stickerCount = mStickerFragment.getResources().getIntArray(R.array.type_count);
    }

    public class ImageHolder extends ViewHolder {
        public ImageView icon;
        public TextView text;

        public ImageHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);
            this.text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    @Override
    public int getItemCount() {
        return stickerPathName.length;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_type_item, parent, false);
        ImageHolder holer = new ImageHolder(v);
        return holer;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageHolder imageHoler = (ImageHolder) holder;
        String name = stickerPathName[position];
        imageHoler.text.setText(name);
        imageHoler.text.setTag(0, stickerPath[position]);
        imageHoler.text.setTag(1, stickerCount[position]);
        imageHoler.text.setOnClickListener(mImageClick);
    }

    private final class ImageClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            String data = (String) v.getTag(0);
            int count = (int) v.getTag(1);
            mStickerFragment.swipToStickerDetails(data, count);
        }
    }
}
