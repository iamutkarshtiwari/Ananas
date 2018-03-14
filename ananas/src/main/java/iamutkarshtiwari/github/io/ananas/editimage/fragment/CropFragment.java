package iamutkarshtiwari.github.io.ananas.editimage.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig;
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CropFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_CROP;
    public static final String TAG = CropFragment.class.getName();
    private View mainView;
    private View backToMenu;
    private LinearLayout ratioList;
    public CropImageView mCropPanel;


    private enum RatioText {
        FREE("FREE"),
        FIT_IMAGE("FIT IMAGE"),
        SQUARE("1:1"),
        RATIO_3_4("3:4"),
        RATIO_4_3("4:3"),
        RATIO_9_16("9:16"),
        RATIO_16_9("16_9");

        private String ratioText;

        RatioText(String ratioText) {
            this.ratioText = ratioText;
        }

        public String getRatioText() {
            return ratioText;
        }
    }

    private CropImageView.CropMode getCropModeFromIndex(int index) {
        return CropImageView.CropMode.values()[index];
    }

    private List<TextView> textViewList = new ArrayList<TextView>();

    public static int SELECTED_COLOR = Color.YELLOW;
    public static int UNSELECTED_COLOR = Color.WHITE;
    private CropRationClick mCropRationClick = new CropRationClick();
    public TextView selctedTextView;

    public static CropFragment newInstance() {
        CropFragment fragment = new CropFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
        return mainView;
    }

    private void setUpRatioList() {
        // init UI
        ratioList.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.leftMargin = 20;
        params.rightMargin = 20;
        RatioText[] ratioTextList = RatioText.values();
        for (int i = 0; i < ratioTextList.length; i++) {
            TextView text = new TextView(activity);
            text.setTextColor(UNSELECTED_COLOR);
            text.setTextSize(15);
            text.setText(ratioTextList[i].ratioText);
            textViewList.add(text);
            ratioList.addView(text, params);
            text.setTag(i);
            if (i == 0) {
                selctedTextView = text;
            }
            text.setTag(CropImageView.CropMode.values()[i]);
            text.setOnClickListener(mCropRationClick);
        }
        selctedTextView.setTextColor(SELECTED_COLOR);
    }


    private final class CropRationClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            TextView curTextView = (TextView) v;
            selctedTextView.setTextColor(UNSELECTED_COLOR);
            CropImageView.CropMode cropMode = (CropImageView.CropMode) v.getTag();
            selctedTextView = curTextView;
            selctedTextView.setTextColor(SELECTED_COLOR);
            mCropPanel.setCropMode(cropMode);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        ratioList = (LinearLayout) mainView.findViewById(R.id.ratio_list_group);
        setUpRatioList();
        this.mCropPanel = ensureEditActivity().mCropPanel;
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_CROP;

        activity.mCropPanel.setVisibility(View.VISIBLE);
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        RectF r = activity.mainImage.getBitmapRect();
        mCropPanel.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
    }


    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }


    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        mCropPanel.setVisibility(View.GONE);
        activity.mainImage.setScaleEnabled(true);// 恢复缩放功能
        activity.bottomGallery.setCurrentItem(0);
        if (selctedTextView != null) {
            selctedTextView.setTextColor(UNSELECTED_COLOR);
        }
        activity.bannerFlipper.showPrevious();
    }


    public void applyCropImage() {
        // System.out.println("保存剪切图片");
        final Uri savedImageUri = getImageUri(getContext(), activity.getMainBit());

        mCropPanel.crop(savedImageUri)
                .execute(new CropCallback() {
                    @Override
                    public void onSuccess(Bitmap cropped) {
                        activity.changeMainBitmap(cropped, true);
                        backToMain();
                        deleteImageFile(savedImageUri);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        backToMain();
                        deleteImageFile(savedImageUri);
                        Toast.makeText(getContext(), "Error while saving image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void deleteImageFile(Uri savedPath) {
        File fdelete = new File(savedPath.toString());
        fdelete.delete();
    }


    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
