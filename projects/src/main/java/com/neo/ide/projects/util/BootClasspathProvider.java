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

package com.neo.ide.projects.util;

import com.neo.ide.projects.classpath.JarFsClasspathReader;
import com.neo.ide.utils.ClassTrie;
import com.neo.ide.utils.StopWatch;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides class names from the boot classpath (i.e. android.jar).
 *
 * @author Akash Yadav
 */
public class BootClasspathProvider {

  private static final Map<String, ClassTrie> bootClasspathClasses = new ConcurrentHashMap<>();
  private static final Logger LOG = LoggerFactory.getLogger(BootClasspathProvider.class);

  /**
   * Updates the boot classpath cache. If a classpath is already indexed, it is skipped.
   *
   * @param classpaths The full file paths of JAR files to index.
   * @return <code>true</code> if the the classpath cache was updated. <code>false</code> otherwise.
   * <p>This method <code>true</code> if and only if any new classpath classes are added into
   * the cache.
   */
  public static synchronized boolean update(Collection<String> classpaths) {
    final var watch = new StopWatch("Indexing " + classpaths.size() + " bootclasspaths");
    var count = 0;
    for (final var classpath : classpaths) {
      if (bootClasspathClasses.containsKey(classpath)) {
        LOG.info("Skipping indexing for boot classpath as it is already indexed: {}", classpath);
        continue;
      }

      LOG.debug("Indexing boot classpath: {}", classpath);
      final var classes =
          new JarFsClasspathReader().listClasses(Collections.singleton(new File(classpath)));
      final var trie = new ClassTrie();
      for (final var info : classes) {
        if (!info.isTopLevel()) {
          continue;
        }

        trie.append(info.getName());
      }

      bootClasspathClasses.put(classpath, trie);
      count += classes.size();
    }

    watch.log();
    return count > 0;
  }

  /**
   * Drops entries for all the given classpaths from the cache.
   *
   * @param classpaths The classpaths to drop entry for.
   */
  public static synchronized void dropAll(Collection<String> classpaths) {
    classpaths.forEach(BootClasspathProvider::drop);
  }

  /**
   * Drops the entry for the given classpath from the cache.
   *
   * @param classpath The classpath to drop entry for.
   */
  public static synchronized void drop(String classpath) {
    bootClasspathClasses.remove(classpath);
  }

  /**
   * Returns all cached <strong>top-level</strong> classes from all the given classpath locations.
   *
   * @param classpaths The classpaths to get class list from.
   * @return The cached <strong>top-level</strong> classes from the given classpaths.
   */
  public static synchronized Set<String> getTopLevelClasses(Collection<String> classpaths) {
    final var result = new TreeSet<String>();
    if (classpaths == null || classpaths.isEmpty()) {
      return result;
    }

    for (final String classpath : classpaths) {
      final var trie = bootClasspathClasses.get(classpath);
      if (trie == null) {
        continue;
      }

      result.addAll(trie.allClassNames());
    }

    return result;
  }

  /**
   * Returns all the {@link ClassTrie} entries.
   *
   * @return All {@link ClassTrie} entries.
   */
  public static Collection<ClassTrie> getAllEntries() {
    return bootClasspathClasses.values();
  }
}
