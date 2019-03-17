package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.FilterAdapter;
import iamutkarshtiwari.github.io.ananas.editimage.fliter.PhotoProcessing;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;


public class FilterListFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_FILTER;
    public static final String TAG = FilterListFragment.class.getName();
    private View mainView;
    private View backBtn;
    private RecyclerView mFilterRecyclerView;
    private FilterAdapter mFilterAdapter;

    private Bitmap filterBit;

    private LinearLayout mFilterGroup;
    private Bitmap currentBitmap;

    public static FilterListFragment newInstance() {
        FilterListFragment fragment = new FilterListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_fliter, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backBtn = mainView.findViewById(R.id.back_to_main);
        mFilterRecyclerView = mainView.findViewById(R.id.filter_recycler);
        mFilterAdapter = new FilterAdapter(this, getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mFilterRecyclerView.setLayoutManager(layoutManager);
        mFilterRecyclerView.setAdapter(mFilterAdapter);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_FILTER;
        activity.filterListFragment.setCurrentBitmap(activity.getMainBit());
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        activity.bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        currentBitmap = activity.getMainBit();
        filterBit = null;
        activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();
    }

    public void applyFilterImage() {
        if (currentBitmap == activity.getMainBit()) {
            backToMain();
            return;
        } else {
            activity.changeMainBitmap(filterBit, true);
            backToMain();
        }
    }

    @Override
    public void onDestroy() {
        if (filterBit != null && (!filterBit.isRecycled())) {
            filterBit.recycle();
        }
        super.onDestroy();
    }

    public void enableFilter(int position) {
        if (position == 0) {
            activity.mainImage.setImageBitmap(activity.getMainBit());
            currentBitmap = activity.getMainBit();
            return;
        }
        ProcessingImage task = new ProcessingImage();
        task.execute(position);
    }

    private final class ProcessingImage extends AsyncTask<Integer, Void, Bitmap> {
        private Dialog dialog;
        private Bitmap srcBitmap;

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int type = params[0];
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }

            srcBitmap = Bitmap.createBitmap(activity.getMainBit().copy(
                    Bitmap.Config.RGB_565, true));
            return PhotoProcessing.filterPhoto(srcBitmap, type);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == null)
                return;
            if (filterBit != null && (!filterBit.isRecycled())) {
                filterBit.recycle();
            }
            filterBit = result;
            activity.mainImage.setImageBitmap(filterBit);
            currentBitmap = filterBit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.handing,
                    false);
            dialog.show();
        }

    }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }
}
