/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.termux.shared.theme;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {

    public static int getSystemAttrColor(Context context, int attr, int defaultValue) {
        try {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(attr, typedValue, true);
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return typedValue.data;
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
