package iamutkarshtiwari.github.io.ananas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public static Dialog getLoadingDialog(Context context, int titleId,
                                          boolean canCancel) {
        return getLoadingDialog(context, context.getString(titleId), canCancel);
    }


    public static Dialog getLoadingDialog(Context context, String title,
                                          boolean canCancel) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(canCancel);
        dialog.setMessage(title);
        return dialog;
    }
}
