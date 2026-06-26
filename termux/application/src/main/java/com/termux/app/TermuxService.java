package com.termux.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.app.AlarmManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.app.terminal.TermuxTerminalSessionActivityClient;
import com.termux.app.terminal.TermuxTerminalSessionServiceClient;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.shell.command.ExecutionCommand;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Foreground service that manages terminal sessions.
 * Adapted from AndroidIDE's TermuxService — stripped of plugin support.
 */
public class TermuxService extends Service {

    private static final String LOG_TAG = "TermuxService";
    private static final String NOTIFICATION_CHANNEL_ID = "termux_service";
    private static final int NOTIFICATION_ID = 1337;

    private static final String ACTION_STOP_SERVICE = "com.termux.app.STOP_SERVICE";
    private static final String ACTION_NEW_SESSION = "com.termux.app.NEW_SESSION";

    private final IBinder mBinder = new LocalBinder();
    private final List<TermuxSession> mTermuxSessions = new ArrayList<>();

    private TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    private TermuxTerminalSessionServiceClient mTermuxTerminalSessionServiceClient;

    private TermuxSession.TermuxSessionClient mTermuxSessionClient;

    private PowerManager.WakeLock mWakeLock;
    private WifiManager.WifiLock mWifiLock;

    private boolean mWantsToStop = false;
    private int mNotificationCounter = 0;

    private Handler mHandler;

    public class LocalBinder extends Binder {
        public TermuxService getService() {
            return TermuxService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(getMainLooper());
        mTermuxTerminalSessionServiceClient = new TermuxTerminalSessionServiceClient(this);
        Logger.logInfo(LOG_TAG, "TermuxService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        startForeground(NOTIFICATION_ID, buildNotification());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        releaseWifiLock();
        for (TermuxSession session : mTermuxSessions) {
            session.finish();
        }
        mTermuxSessions.clear();
        Logger.logInfo(LOG_TAG, "TermuxService destroyed");
    }

    // --- Session Management ---

    public void setTermuxTerminalSessionActivityClient(TermuxTerminalSessionActivityClient client) {
        mTermuxTerminalSessionActivityClient = client;
        // Update all existing sessions' clients
        for (TermuxSession session : mTermuxSessions) {
            session.getTerminalSession().setTerminalSessionClient(
                client != null ? client : mTermuxTerminalSessionServiceClient
            );
        }
    }

    public TermuxTerminalSessionActivityClient getTermuxTerminalSessionActivityClient() {
        return mTermuxTerminalSessionActivityClient;
    }

    /**
     * Create a new terminal session.
     */
    @Nullable
    public TermuxSession createTermuxSession(
            String workingDirectory,
            boolean isFailSafe,
            String sessionName
    ) {
        String execCommand;
        String[] execArguments;

        if (isFailSafe) {
            execCommand = "/system/bin/sh";
            execArguments = new String[]{};
        } else {
            execCommand = getShellCommand();
            execArguments = new String[]{};
        }

        if (workingDirectory == null) {
            workingDirectory = getFilesDir() + "/home";
        }

        String[] env = buildEnvironment(workingDirectory);

        ExecutionCommand executionCommand = new ExecutionCommand(
            UUID.randomUUID().toString(),
            execCommand,
            execArguments,
            workingDirectory,
            env,
            false,
            sessionName
        );
        executionCommand.shellCreateMode = ExecutionCommand.ShellCreateMode.APPEND;

        TerminalSession terminalSession = new TerminalSession(
            execCommand,
            workingDirectory,
            env,
            null, null,
            mTermuxTerminalSessionActivityClient != null
                ? mTermuxTerminalSessionActivityClient
                : mTermuxTerminalSessionServiceClient
        );
        terminalSession.mSessionName = sessionName;

        TermuxSession termuxSession = new TermuxSession(
            terminalSession,
            executionCommand,
            null,
            false
        );

        synchronized (mTermuxSessions) {
            mTermuxSessions.add(termuxSession);
        }

        updateNotification();
        acquireWakeLock();
        acquireWifiLock();

        return termuxSession;
    }

    /**
     * Remove a terminal session.
     */
    public int removeTermuxSession(TermuxSession session) {
        int index;
        synchronized (mTermuxSessions) {
            index = mTermuxSessions.indexOf(session);
            if (index >= 0) {
                mTermuxSessions.remove(index);
            }
        }

        if (mTermuxSessions.isEmpty()) {
            mWantsToStop = true;
        } else {
            updateNotification();
        }

        return index;
    }

    public int removeTermuxSession(TerminalSession terminalSession) {
        TermuxSession termuxSession = getTermuxSessionForTerminalSession(terminalSession);
        if (termuxSession != null) {
            return removeTermuxSession(termuxSession);
        }
        return -1;
    }

    public TermuxSession getTermuxSession(int index) {
        synchronized (mTermuxSessions) {
            if (index >= 0 && index < mTermuxSessions.size()) {
                return mTermuxSessions.get(index);
            }
        }
        return null;
    }

    public TermuxSession getTermuxSessionForTerminalSession(TerminalSession terminalSession) {
        synchronized (mTermuxSessions) {
            for (TermuxSession session : mTermuxSessions) {
                if (session.getTerminalSession() == terminalSession) {
                    return session;
                }
            }
        }
        return null;
    }

    public TerminalSession getTerminalSessionForHandle(String sessionHandle) {
        synchronized (mTermuxSessions) {
            for (TermuxSession session : mTermuxSessions) {
                if (session.getExecutionCommand().execId.equals(sessionHandle)) {
                    return session.getTerminalSession();
                }
            }
        }
        return null;
    }

    public int getIndexOfSession(TerminalSession terminalSession) {
        synchronized (mTermuxSessions) {
            for (int i = 0; i < mTermuxSessions.size(); i++) {
                if (mTermuxSessions.get(i).getTerminalSession() == terminalSession) {
                    return i;
                }
            }
        }
        return -1;
    }

    public TermuxSession getLastTermuxSession() {
        synchronized (mTermuxSessions) {
            if (!mTermuxSessions.isEmpty()) {
                return mTermuxSessions.get(mTermuxSessions.size() - 1);
            }
        }
        return null;
    }

    public int getTermuxSessionsSize() {
        synchronized (mTermuxSessions) {
            return mTermuxSessions.size();
        }
    }

    public List<TermuxSession> getTermuxSessions() {
        synchronized (mTermuxSessions) {
            return Collections.unmodifiableList(new ArrayList<>(mTermuxSessions));
        }
    }

    public boolean wantsToStop() {
        return mWantsToStop;
    }

    // --- Environment ---

    private String[] buildEnvironment(String workingDirectory) {
        String homeDir = getFilesDir() + "/home";
        String prefixDir = getFilesDir() + "/usr";

        java.util.Map<String, String> env = new java.util.LinkedHashMap<>();
        env.put("HOME", homeDir);
        env.put("PREFIX", prefixDir);
        env.put("TMPDIR", homeDir + "/tmp");
        env.put("ANDROID_HOME", homeDir + "/android-sdk");
        env.put("ANDROID_SDK_ROOT", homeDir + "/android-sdk");
        env.put("JAVA_HOME", prefixDir + "/opt/openjdk");
        env.put("PATH", prefixDir + "/bin:" + homeDir + "/android-sdk/cmdline-tools/latest/bin:" + homeDir + "/android-sdk/platform-tools");
        env.put("LANG", "en_US.UTF-8");
        env.put("LC_ALL", "en_US.UTF-8");
        env.put("SYSROOT", prefixDir);

        java.util.List<String> envList = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, String> entry : env.entrySet()) {
            envList.add(entry.getKey() + "=" + entry.getValue());
        }
        return envList.toArray(new String[0]);
    }

    private String getShellCommand() {
        String[] shells = {
            "/data/data/com.termux/files/usr/bin/bash",
            "/system/bin/sh"
        };
        for (String shell : shells) {
            if (new java.io.File(shell).exists()) {
                return shell;
            }
        }
        return "/system/bin/sh";
    }

    // --- Notification ---

    private Notification buildNotification() {
        createNotificationChannel();

        Intent mainIntent = new Intent(this, com.neo.ide.activities.HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent stopIntent = new Intent(this, TermuxService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent newSessionIntent = new Intent(this, TermuxService.class);
        newSessionIntent.setAction(ACTION_NEW_SESSION);
        PendingIntent newSessionPendingIntent = PendingIntent.getService(
            this, 2, newSessionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        int sessionCount = getTermuxSessionsSize();
        String title = sessionCount + " terminal session" + (sessionCount != 1 ? "s" : "");

        return builder
            .setContentTitle(title)
            .setContentText("CODE-IDE terminal")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build();
    }

    private void updateNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(NOTIFICATION_ID, buildNotification());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Terminal Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Keeps terminal sessions running in background");
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    // --- Wake/Wifi Locks ---

    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null) {
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CODE-IDE::TerminalService");
                mWakeLock.acquire(60 * 60 * 1000L); // 1 hour max
            }
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void acquireWifiLock() {
        if (mWifiLock == null) {
            android.net.wifi.WifiManager wm = (android.net.wifi.WifiManager) getApplicationContext()
                .getSystemService(WIFI_SERVICE);
            if (wm != null) {
                mWifiLock = wm.createWifiLock(
                    android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                    "CODE-IDE::TerminalService"
                );
                mWifiLock.acquire();
            }
        }
    }

    private void releaseWifiLock() {
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
            mWifiLock = null;
        }
    }

    // --- Static Start/Stop ---

    public static void start(Context context) {
        Intent intent = new Intent(context, TermuxService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, TermuxService.class);
        intent.setAction(ACTION_STOP_SERVICE);
        context.startService(intent);
    }
}
