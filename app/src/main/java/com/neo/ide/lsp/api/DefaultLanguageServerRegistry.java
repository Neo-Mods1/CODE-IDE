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



package com.neo.ide.lsp.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neo.ide.eventbus.events.project.ProjectInitializedEvent;
import com.neo.ide.projects.IWorkspace;
import com.neo.ide.utils.ILogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe implementation of {@link ILanguageServerRegistry}.
 *
 * @author Akash Yadav
 */
public class DefaultLanguageServerRegistry extends ILanguageServerRegistry {

  private final Map<String, ILanguageServer> mRegister = new HashMap<>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  @Override
  public void register(@NonNull final ILanguageServer server) {
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
    lock.writeLock().lock();
    try {
      final var old = mRegister.put(server.getServerId(), server);
      if (old != null) {
        mRegister.put(old.getServerId(), old);
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void connectClient(@NonNull final ILanguageClient client) {
    Objects.requireNonNull(client);
    lock.readLock().lock();
    try {
      for (final var server : mRegister.values()) {
        server.connectClient(client);
      }
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void unregister(@NonNull final String serverId) {
    lock.writeLock().lock();
    try {
      final var registered = mRegister.remove(serverId);
      if (registered == null) {
        throw new IllegalStateException("No server found for the given server ID");
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void destroy() {
    EventBus.getDefault().unregister(this);
    lock.readLock().lock();
    try {
      for (var server : mRegister.values()) {
        server.shutdown();
      }
    } finally {
      lock.readLock().unlock();
    }

    lock.writeLock().lock();
    try {
      mRegister.clear();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Nullable
  @Override
  public ILanguageServer getServer(@NonNull final String serverId) {
    lock.readLock().lock();
    try {
      return mRegister.get(serverId);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  @SuppressWarnings("unused")
  public void onProjectInitialized(ProjectInitializedEvent event) {
    final var workspace = event.get(IWorkspace.class);
    if (workspace == null) {
      return;
    }

    ILogger.ROOT.debug("Dispatching ProjectInitializedEvent to language servers...");
    for (final var server : mRegister.values()) {
      server.setupWorkspace(workspace);
    }
  }
}
