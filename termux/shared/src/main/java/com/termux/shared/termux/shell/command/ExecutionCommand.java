package com.termux.shared.termux.shell.command;

import java.util.UUID;

/**
 * Represents a command to be executed in a terminal session.
 * Simplified from AndroidIDE's ExecutionCommand.
 */
public class ExecutionCommand {

    public enum ShellCreateMode {
        APPEND,
        REPLACE
    }

    public String execId;
    public String executablePath;
    public String[] arguments;
    public String workingDirectory;
    public String[] environment;
    public boolean isPluginExecutionCommand;
    public ShellCreateMode shellCreateMode;
    public String shellName;
    public int mPid;

    public ExecutionCommand() {
        this.execId = UUID.randomUUID().toString();
    }

    public ExecutionCommand(
            String execId,
            String executablePath,
            String[] arguments,
            String workingDirectory,
            String[] environment,
            boolean isPluginExecutionCommand,
            String shellName
    ) {
        this.execId = execId != null ? execId : UUID.randomUUID().toString();
        this.executablePath = executablePath;
        this.arguments = arguments;
        this.workingDirectory = workingDirectory;
        this.environment = environment;
        this.isPluginExecutionCommand = isPluginExecutionCommand;
        this.shellName = shellName;
    }

    public boolean isPluginExecutionCommandWithPendingResult() {
        return false;
    }
}
