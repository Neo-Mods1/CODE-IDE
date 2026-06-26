/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.termux.app.terminal;

import android.app.Service;

import androidx.annotation.NonNull;

import com.termux.app.TermuxService;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;

import java.io.Closeable;

/**
 * Fallback TerminalSessionClient used when no Activity is bound.
 * Adapted from AndroidIDE's TermuxTerminalSessionServiceClient.
 */
public class TermuxTerminalSessionServiceClient extends TermuxTerminalSessionClientBase implements Closeable {

    private static final String LOG_TAG = "TermuxSessionServiceClient";

    private TermuxService mService;

    public TermuxTerminalSessionServiceClient(TermuxService service) {
        this.mService = service;
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
    public void close() {
        mService = null;
    }
}
