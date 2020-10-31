package cm.tbg.gpchat.utils;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;

import cm.tbg.gpchat.R;

public class SnackbarUtil {
    public static void showDoesNotFireAppSnackbar(Activity context) {
        String text = String.format(context.getString(R.string.does_not_have_fireapp), context.getString(R.string.app_name));
        Snackbar.make(context.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }
}
