package iamutkarshtiwari.github.io.ananas.editimage.task;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;

import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3;

public abstract class StickerTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private Dialog dialog;

    private EditImageActivity mContext;

    public StickerTask(EditImageActivity activity) {
        this.mContext = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mContext.isFinishing())
            return;

        dialog = mContext.getLoadingDialog(mContext, R.string.saving_image,
                false);
        dialog.show();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Matrix touchMatrix = mContext.mainImage.getImageViewMatrix();

        Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
                Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(resultBit);

        float[] data = new float[9];
        touchMatrix.getValues(data);
        Matrix3 cal = new Matrix3(data);
        Matrix3 inverseMatrix = cal.inverseMatrix();
        Matrix m = new Matrix();
        m.setValues(inverseMatrix.getValues());
        handleImage(canvas, m);
        return resultBit;
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
        onPostResult(result);
        dialog.dismiss();
    }

    public abstract void handleImage(Canvas canvas, Matrix m);

    public abstract void onPostResult(Bitmap result);
}
