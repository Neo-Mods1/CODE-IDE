package com.termux.shared.interact;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * Clipboard utilities.
 * Simplified from AndroidIDE's ShareUtils.
 */
public class ShareUtils {

    public static void copyTextToClipboard(Context context, String text) {
        if (context == null || TextUtils.isEmpty(text)) return;
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("terminal", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static String getTextStringFromClipboardIfSet(Context context, boolean allowEmpty) {
        if (context == null) return null;
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null || !clipboard.hasPrimaryClip()) return null;

        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null || clip.getItemCount() == 0) return null;

        CharSequence text = clip.getItemAt(0).getText();
        if (text == null) return null;

        String result = text.toString();
        if (!allowEmpty && TextUtils.isEmpty(result)) return null;

        return result;
    }
}
