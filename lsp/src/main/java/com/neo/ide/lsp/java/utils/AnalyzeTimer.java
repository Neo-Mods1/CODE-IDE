/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    CODE-IDE • NeoMods                      ║
 * ║                  Advanced Android IDE Project              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 *  (っ◔◡◔)っ ♥
 *
 *  Developer         • NeoMods
 *  Telegram Contact  • @NeoModsDev
 *  Telegram Channel  • https://t.me/NeoModsChannel
 *
 * ──────────────────────────────────────────────────────────────
 *  PROJECT NOTICE
 * ──────────────────────────────────────────────────────────────
 *
 *  This source file is part of the CODE-IDE project.
 *
 *  Unauthorized copying, extraction, redistribution,
 *  mirroring, downloading, modification, or reuse of
 *  CODE-IDE source files is NOT permitted without
 *  explicit permission from the developer.
 *
 *  The application may expose certain components in
 *  read-only mode for educational or preview purposes,
 *  however this DOES NOT grant permission to reuse
 *  or redistribute the source code.
 *
 *  If you need access to the original source code,
 *  implementation details, licensing, or collaboration,
 *  please contact the developer directly.
 *
 *  © NeoMods — All Rights Reserved
 * ──────────────────────────────────────────────────────────────
 */



package com.neo.ide.lsp.java.utils;

import android.os.Handler;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 * A timer which helps the language server to run a lint check after a specified interval.
 *
 * @author Akash Yadav
 */
public class AnalyzeTimer {

  public static final long DEFAULT_INTERVAL = 400;
  @NonNull private final Handler timerHandler;
  @NonNull private final Runnable timerCallback;
  private long interval;
  private boolean started;

  /**
   * Creates a new AnalyzeTimer instance.
   *
   * @param timerCallback The callback that will be invoked after the timer has ended.
   */
  public AnalyzeTimer(@NonNull Runnable timerCallback) {
    this.timerHandler = new Handler();
    this.interval = DEFAULT_INTERVAL;
    this.timerCallback = timerCallback;

    Objects.requireNonNull(this.timerCallback, "Callback cannot be null");
  }

  /**
   * Get the interval set to this timer.
   *
   * @return The interval.
   */
  public long getInterval() {
    return interval;
  }

  /**
   * Set the interval for the lint timer.
   *
   * @param interval The interval after which <code>timerCallback</code> will be called.
   */
  public void setInterval(long interval) {
    this.interval = interval;

    if (this.interval <= 0) {
      throw new IllegalArgumentException("Invalid interval specified for timer.");
    }
  }

  /** Starts the timer. */
  public void start() {
    restart();
  }

  /** Restarts the timer. */
  public void restart() {
    timerHandler.removeCallbacks(timerCallback);
    timerHandler.postDelayed(timerCallback, interval);
    started = true;
  }

  /** Shutdown the timer. Cancels any running timers. */
  public void cancel() {
    timerHandler.removeCallbacks(timerCallback);
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
