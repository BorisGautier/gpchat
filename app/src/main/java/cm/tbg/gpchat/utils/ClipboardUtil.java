package cm.tbg.gpchat.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;


public class ClipboardUtil {
    //copy a String to the System Clipboard
    public static void copyTextToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        clipboard.setPrimaryClip(clip);
    }

}
