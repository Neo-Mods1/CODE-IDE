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

/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.lookup.internal;

import com.google.auto.service.AutoService;
import com.neo.ide.lookup.Lookup;
import com.neo.ide.lookup.ServiceRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link Lookup}.
 *
 * @author Akash Yadav
 */
@AutoService(Lookup.class)
public final class DefaultLookup implements Lookup {

  private final Map<Class<?>, Key<?>> keyTable = new ConcurrentHashMap<>();
  private final Map<Key<?>, Object> services = new ConcurrentHashMap<>();

  @Override
  public <T> void register(final Class<T> klass, final T instance) {
    final Key<T> key = key(klass);
    keyTable.put(klass, key);
    register(key, instance);
  }

  @Override
  public <T> void update(final Class<T> klass, final T instance) {
    update(key(klass), instance);
  }

  @Override
  public <T> void unregister(final Class<T> klass) {
    unregister(key(klass));
  }

  @Nullable
  @Override
  public <T> T lookup(final Class<T> klass) {
    return lookup(key(klass));
  }

  @Override
  public <T> void register(final Key<T> key, final T instance) {
    registerOrUpdate(key, instance, false);
  }

  @Override
  public <T> void unregister(final Key<T> key) {
    services.remove(key);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T lookup(final Key<T> key) {
    return (T) services.get(key);
  }

  @Override
  public <T> void update(final Key<T> key, final T instance) {
    registerOrUpdate(key, instance, true);
  }

  @Override
  public void unregisterAll() {
    services.clear();
  }

  @SuppressWarnings("unchecked")
  private <T> void registerOrUpdate(final Key<T> key, final T instance, boolean update) {
    final T existing = (T) services.put(key, instance);
    if (existing != null && !update) {
      services.put(key, existing);
      throw new ServiceRegisteredException();
    }
  }

  @SuppressWarnings("unchecked")
  @NotNull
  private <T> Key<T> key(Class<T> klass) {
    final var key = keyTable.get(klass);
    if (key == null) {
      // Returning a new key instance will always make the lookup methods above return null
      return new Key<>();
    }

    return (Key<T>) key;
  }
}
