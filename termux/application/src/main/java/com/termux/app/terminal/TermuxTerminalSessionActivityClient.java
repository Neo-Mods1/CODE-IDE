package com.termux.app.terminal;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.app.TermuxService;
import com.termux.shared.interact.ShareUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalColors;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Bridges TerminalSession to an Activity.
 * Adapted from AndroidIDE's TermuxTerminalSessionActivityClient.
 */
public class TermuxTerminalSessionActivityClient extends TermuxTerminalSessionClientBase {

    private static final String LOG_TAG = "TermuxSessionClient";
    private static final int MAX_SESSIONS = 8;

    protected final android.app.Activity mActivity;
    private TermuxService mService;
    private com.termux.view.TerminalView mTerminalView;
    private int mBellSoundId;

    public TermuxTerminalSessionActivityClient(android.app.Activity activity) {
        this.mActivity = activity;
    }

    public void setService(TermuxService service) {
        this.mService = service;
    }

    public TermuxService getService() {
        return mService;
    }

    public void setTerminalView(com.termux.view.TerminalView view) {
        this.mTerminalView = view;
    }

    public com.termux.view.TerminalView getTerminalView() {
        return mTerminalView;
    }

    // --- Lifecycle ---

    public void onCreate() {
        checkForFontAndColors();
    }

    public void onStart() {
        if (mService != null) {
            TermuxSession lastSession = mService.getLastTermuxSession();
            if (lastSession != null) {
                setCurrentSession(lastSession.getTerminalSession());
            }
        }
    }

    public void onResume() {}

    public void onStop() {}

    // --- TerminalSessionClient callbacks ---

    @Override
    public void onTextChanged(@NonNull TerminalSession changedSession) {
        if (mTerminalView != null && getCurrentSession() == changedSession) {
            mTerminalView.onScreenUpdated();
        }
    }

    @Override
    public void onTitleChanged(@NonNull TerminalSession updatedSession) {}

    @Override
    public void onSessionFinished(@NonNull TerminalSession finishedSession) {
        if (mService == null) return;

        int index = mService.getIndexOfSession(finishedSession);

        if (finishedSession.getExitStatus() == 0 || finishedSession.getExitStatus() == 130) {
            removeFinishedSession(finishedSession);
        }
    }

    @Override
    public void onCopyTextToClipboard(@NonNull TerminalSession session, String text) {
        if (mActivity == null) return;
        ShareUtils.copyTextToClipboard(mActivity, text);
    }

    @Override
    public void onPasteTextFromClipboard(@Nullable TerminalSession session) {
        if (mActivity == null) return;
        String text = ShareUtils.getTextStringFromClipboardIfSet(mActivity, true);
        if (text != null) {
            TerminalSession current = getCurrentSession();
            if (current != null && current.getEmulator() != null) {
                current.getEmulator().paste(text);
            }
        }
    }

    @Override
    public void onBell(@NonNull TerminalSession session) {}

    @Override
    public void onColorsChanged(@NonNull TerminalSession changedSession) {
        if (getCurrentSession() == changedSession) {
            updateBackgroundColor();
        }
    }

    @Override
    public void onTerminalCursorStateChange(boolean enabled) {}

    @Override
    public void setTerminalShellPid(@NonNull TerminalSession terminalSession, int pid) {
        if (mService == null) return;
        TermuxSession termuxSession = mService.getTermuxSessionForTerminalSession(terminalSession);
        if (termuxSession != null) {
            termuxSession.getExecutionCommand().mPid = pid;
        }
    }

    @Override
    public Integer getTerminalCursorStyle() {
        return null;
    }

    // --- Session management ---

    public TerminalSession getCurrentSession() {
        if (mService == null) return null;
        TermuxSession last = mService.getLastTermuxSession();
        return last != null ? last.getTerminalSession() : null;
    }

    public void setCurrentSession(TerminalSession session) {
        if (session == null) return;
        if (mTerminalView != null) {
            mTerminalView.attachSession(session);
        }
        updateBackgroundColor();
    }

    public void switchToSession(boolean forward) {
        if (mService == null) return;

        TerminalSession currentSession = getCurrentSession();
        int index = mService.getIndexOfSession(currentSession);
        int size = mService.getTermuxSessionsSize();

        if (forward) {
            if (++index >= size) index = 0;
        } else {
            if (--index < 0) index = size - 1;
        }

        TermuxSession termuxSession = mService.getTermuxSession(index);
        if (termuxSession != null) {
            setCurrentSession(termuxSession.getTerminalSession());
        }
    }

    public void switchToSession(int index) {
        if (mService == null) return;
        TermuxSession termuxSession = mService.getTermuxSession(index);
        if (termuxSession != null) {
            setCurrentSession(termuxSession.getTerminalSession());
        }
    }

    public void addNewSession(boolean isFailSafe, String sessionName) {
        addNewSession(isFailSafe, sessionName, null);
    }

    public void addNewSession(boolean isFailSafe, String sessionName, String workingDirectory) {
        if (mService == null) return;

        if (mService.getTermuxSessionsSize() >= MAX_SESSIONS) {
            if (mActivity != null) {
                new AlertDialog.Builder(mActivity)
                    .setTitle("Max sessions reached")
                    .setMessage("Cannot create more than " + MAX_SESSIONS + " sessions")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            }
            return;
        }

        if (workingDirectory == null) {
            TerminalSession currentSession = getCurrentSession();
            workingDirectory = currentSession != null ? currentSession.getCwd() : mActivity.getFilesDir() + "/home";
        }

        TermuxSession newSession = mService.createTermuxSession(workingDirectory, isFailSafe, sessionName);
        if (newSession != null) {
            setCurrentSession(newSession.getTerminalSession());
        }
    }

    public void removeFinishedSession(TerminalSession finishedSession) {
        if (mService == null) return;

        int index = mService.removeTermuxSession(finishedSession);
        int size = mService.getTermuxSessionsSize();

        if (size == 0) {
            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.finish();
            }
        } else {
            if (index >= size) index = size - 1;
            TermuxSession termuxSession = mService.getTermuxSession(index);
            if (termuxSession != null) {
                setCurrentSession(termuxSession.getTerminalSession());
            }
        }
    }

    // --- Utilities ---

    public void updateBackgroundColor() {
        if (mActivity == null) return;
        TerminalSession session = getCurrentSession();
        if (session != null && session.getEmulator() != null) {
            mActivity.getWindow().getDecorView().setBackgroundColor(
                session.getEmulator().mColors.mCurrentColors[257] // BACKGROUND index
            );
        }
    }

    public void checkForFontAndColors() {
        try {
            File colorsFile = new File(mActivity.getFilesDir(), "usr/etc/colors.properties");
            File fontFile = new File(mActivity.getFilesDir(), "usr/etc/font.ttf");

            final Properties props = new Properties();
            if (colorsFile.isFile()) {
                try (InputStream in = new FileInputStream(colorsFile)) {
                    props.load(in);
                }
            }

            TerminalColors.COLOR_SCHEME.updateWith(props);
            TerminalSession session = getCurrentSession();
            if (session != null && session.getEmulator() != null) {
                session.getEmulator().mColors.reset();
            }
            updateBackgroundColor();

            if (mTerminalView != null) {
                final Typeface newTypeface = (fontFile.exists() && fontFile.length() > 0)
                    ? Typeface.createFromFile(fontFile)
                    : Typeface.MONOSPACE;
                mTerminalView.setTypeface(newTypeface);
            }
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error in checkForFontAndColors()", e);
        }
    }

    String toToastTitle(TerminalSession session) {
        if (mService == null) return null;
        int indexOfSession = mService.getIndexOfSession(session);
        if (indexOfSession < 0) return null;

        StringBuilder toastTitle = new StringBuilder("[" + (indexOfSession + 1) + "]");
        if (!TextUtils.isEmpty(session.mSessionName)) {
            toastTitle.append(" ").append(session.mSessionName);
        }
        return toastTitle.toString();
    }
}
