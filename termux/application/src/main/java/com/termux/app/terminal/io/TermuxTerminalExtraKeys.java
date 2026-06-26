package com.termux.app.terminal.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.termux.app.terminal.TermuxTerminalSessionActivityClient;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.extrakeys.ExtraKeysConstants;
import com.termux.shared.termux.extrakeys.ExtraKeysInfo;
import com.termux.shared.termux.terminal.io.TerminalExtraKeys;
import com.termux.view.TerminalView;

import org.json.JSONException;

/**
 * Extra keys handler with special buttons.
 * Adapted from AndroidIDE's TermuxTerminalExtraKeys.
 */
public class TermuxTerminalExtraKeys extends TerminalExtraKeys {

    private ExtraKeysInfo mExtraKeysInfo;
    private final Context mContext;
    private final TermuxTerminalSessionActivityClient mSessionActivityClient;
    private DrawerLayout mDrawerLayout;

    private static final String LOG_TAG = "TermuxTerminalExtraKeys";

    public TermuxTerminalExtraKeys(
            Context context,
            @NonNull TerminalView terminalView,
            TermuxTerminalSessionActivityClient sessionActivityClient
    ) {
        super(terminalView);
        mContext = context;
        mSessionActivityClient = sessionActivityClient;
        setExtraKeys();
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.mDrawerLayout = drawerLayout;
    }

    private void setExtraKeys() {
        mExtraKeysInfo = null;
        try {
            String extraKeys = "[[\"ESC\",\"/\",\"-\",\"HOME\",\"UP\",\"END\",\"PGUP\"],[\"TAB\",\"CTRL\",\"ALT\",\"LEFT\",\"DOWN\",\"RIGHT\",\"PGDN\"]]";
            String extraKeysStyle = "default";
            mExtraKeysInfo = new ExtraKeysInfo(extraKeys, extraKeysStyle, ExtraKeysConstants.CONTROL_CHARS_ALIASES);
        } catch (JSONException e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Could not load extra keys", e);
            try {
                mExtraKeysInfo = new ExtraKeysInfo(
                    "[[\"ESC\",\"/\",\"-\",\"HOME\",\"UP\",\"END\",\"PGUP\"],[\"TAB\",\"CTRL\",\"ALT\",\"LEFT\",\"DOWN\",\"RIGHT\",\"PGDN\"]]",
                    "default",
                    ExtraKeysConstants.CONTROL_CHARS_ALIASES
                );
            } catch (JSONException e2) {
                Logger.logStackTraceWithMessage(LOG_TAG, "Could not create default extra keys", e2);
            }
        }
    }

    public ExtraKeysInfo getExtraKeysInfo() {
        return mExtraKeysInfo;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onTerminalExtraKeyButtonClick(View view, String key, boolean ctrlDown, boolean altDown, boolean shiftDown, boolean fnDown) {
        if ("KEYBOARD".equals(key)) {
            toggleSoftKeyboard();
        } else if ("DRAWER".equals(key)) {
            toggleDrawer();
        } else if ("PASTE".equals(key)) {
            if (mSessionActivityClient != null) {
                mSessionActivityClient.onPasteTextFromClipboard(null);
            }
        } else if ("SCROLL".equals(key)) {
            toggleAutoScroll();
        } else {
            super.onTerminalExtraKeyButtonClick(view, key, ctrlDown, altDown, shiftDown, fnDown);
        }
    }

    private void toggleSoftKeyboard() {
        try {
            android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, 0);
            }
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Failed to toggle keyboard", e);
        }
    }

    private void toggleDrawer() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
    }

    private void toggleAutoScroll() {
        if (mSessionActivityClient != null) {
            TerminalView tv = mSessionActivityClient.getTerminalView();
            if (tv != null && tv.mEmulator != null) {
                tv.mEmulator.toggleAutoScrollDisabled();
            }
        }
    }
}
