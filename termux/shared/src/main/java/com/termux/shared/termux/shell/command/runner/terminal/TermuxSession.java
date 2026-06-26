/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.termux.shared.termux.shell.command.runner.terminal;

import com.termux.shared.termux.shell.command.ExecutionCommand;
import com.termux.terminal.TerminalSession;

/**
 * Wraps a TerminalSession with an ExecutionCommand for tracking.
 * Simplified from AndroidIDE's TermuxSession.
 */
public class TermuxSession {

    public interface TermuxSessionClient {
    }

    private final TerminalSession mTerminalSession;
    private final ExecutionCommand mExecutionCommand;
    private final TermuxSessionClient mTermuxSessionClient;
    private final boolean mIsSetStdoutOnExit;

    public TermuxSession(
            TerminalSession terminalSession,
            ExecutionCommand executionCommand,
            TermuxSessionClient termuxSessionClient,
            boolean setStdoutOnExit
    ) {
        this.mTerminalSession = terminalSession;
        this.mExecutionCommand = executionCommand;
        this.mTermuxSessionClient = termuxSessionClient;
        this.mIsSetStdoutOnExit = setStdoutOnExit;
    }

    public TerminalSession getTerminalSession() {
        return mTerminalSession;
    }

    public ExecutionCommand getExecutionCommand() {
        return mExecutionCommand;
    }

    public TermuxSessionClient getTermuxSessionClient() {
        return mTermuxSessionClient;
    }

    public boolean isSetStdoutOnExit() {
        return mIsSetStdoutOnExit;
    }

    public void finish() {
        if (mTerminalSession != null) {
            mTerminalSession.finishIfRunning();
        }
    }
}
