package com.termux.app.terminal;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.text.TextUtils;
import android.widget.ListView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.R;
import com.termux.app.TermuxService;
import com.termux.shared.interact.ShareUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalColors;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.terminal.TextStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * The TerminalSessionClient implementation that bridges TerminalSession to an Activity.
 * Adapted from AndroidIDE's TermuxTerminalSessionActivityClient.
 */
public class TermuxTerminalSessionActivityClient extends TermuxTerminalSessionClientBase {

    private static final String LOG_TAG = "TermuxSessionClient";
    private static final int MAX_SESSIONS = 8;

    protected final com.neo.ide.app.BaseActivity mActivity;
    private TermuxService mService;

    private SoundPool mBellSoundPool;
    private int mBellSoundId;

    public TermuxTerminalSessionActivityClient(com.neo.ide.app.BaseActivity activity) {
        this.mActivity = activity;
    }

    public void setService(TermuxService service) {
        this.mService = service;
    }

    public TermuxService getService() {
        return mService;
    }

    // --- Lifecycle callbacks ---

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

    public void onResume() {
        loadBellSoundPool();
    }

    public void onStop() {
        releaseBellSoundPool();
    }

    // --- TerminalSessionClient callbacks ---

    @Override
    public void onTextChanged(@NonNull TerminalSession changedSession) {
        // Subclass should handle view update
    }

    @Override
    public void onTitleChanged(@NonNull TerminalSession updatedSession) {
        // Subclass should handle title update
    }

    @Override
    public void onSessionFinished(@NonNull TerminalSession finishedSession) {
        if (mService == null) return;

        int index = mService.getIndexOfSession(finishedSession);

        if (mActivity != null && finishedSession != getCurrentSession()) {
            if (index >= 0) {
                Logger.logInfo(LOG_TAG, toToastTitle(finishedSession) + " - exited");
            }
        }

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
    public void onBell(@NonNull TerminalSession session) {
        if (mActivity == null) return;
        loadBellSoundPool();
        if (mBellSoundPool != null) {
            mBellSoundPool.play(mBellSoundId, 1.f, 1.f, 1, 0, 1.f);
        }
    }

    @Override
    public void onColorsChanged(@NonNull TerminalSession changedSession) {
        if (getCurrentSession() == changedSession) {
            updateBackgroundColor();
        }
    }

    @Override
    public void onTerminalCursorStateChange(boolean enabled) {
        // Optional: implement cursor blinking
    }

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
        if (session == null || mService == null) return;

        View terminalView = mActivity.findViewById(R.id.terminal_view);
        if (terminalView instanceof com.termux.view.TerminalView) {
            ((com.termux.view.TerminalView) terminalView).attachSession(session);
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
                session.getEmulator().mColors.mCurrentColors[TextStyle.COLOR_INDEX_BACKGROUND]
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

            final Typeface newTypeface = (fontFile.exists() && fontFile.length() > 0)
                ? Typeface.createFromFile(fontFile)
                : Typeface.MONOSPACE;

            View terminalView = mActivity.findViewById(R.id.terminal_view);
            if (terminalView instanceof com.termux.view.TerminalView) {
                ((com.termux.view.TerminalView) terminalView).setTypeface(newTypeface);
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
        String title = session.getTitle();
        if (!TextUtils.isEmpty(title)) {
            toastTitle.append(session.mSessionName == null ? " " : "\n");
            toastTitle.append(title);
        }
        return toastTitle.toString();
    }

    // --- Bell sound ---

    private synchronized void loadBellSoundPool() {
        if (mBellSoundPool == null) {
            mBellSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(
                new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .build()
            ).build();

            try {
                mBellSoundId = mBellSoundPool.load(mActivity, R.raw.bell, 1);
            } catch (Exception e) {
                Logger.logStackTraceWithMessage(LOG_TAG, "Failed to load bell sound", e);
            }
        }
    }

    private synchronized void releaseBellSoundPool() {
        if (mBellSoundPool != null) {
            mBellSoundPool.release();
            mBellSoundPool = null;
        }
    }
}
