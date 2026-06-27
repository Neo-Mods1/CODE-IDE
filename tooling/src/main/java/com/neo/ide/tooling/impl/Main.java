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

package com.neo.ide.tooling.impl;

import com.neo.ide.logging.JvmStdErrAppender;
import com.neo.ide.tooling.api.IToolingApiClient;
import com.neo.ide.tooling.api.util.ToolingApiLauncher;
import com.neo.ide.tooling.impl.internal.ProjectImpl;
import com.neo.ide.tooling.impl.progress.ForwardingProgressListener;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.gradle.tooling.ConfigurableLauncher;
import org.gradle.tooling.events.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);
  public static IToolingApiClient client;
  public static Future<Void> future;

  public static void main(String[] args) {

    // disable the JVM std.err appender
    System.setProperty(JvmStdErrAppender.PROP_JVM_STDERR_APPENDER_ENABLED, "false");

    LOG.debug("Starting Tooling API server...");
    final var project = new ProjectImpl();
    final var server = new ToolingApiServerImpl(project);
    final var launcher =
        ToolingApiLauncher.newServerLauncher(server, project, System.in, System.out);
    Main.future = launcher.startListening();
    Main.client = (IToolingApiClient) launcher.getRemoteProxy();
    server.connect(client);

    LOG.debug("Server started. Will run until shutdown message is received...");
    LOG.debug("Running on Java version: {}", System.getProperty("java.version", "<unknown>"));

    try {
      Main.future.get();
    } catch (CancellationException cancellationException) {
      // ignored
    } catch (InterruptedException | ExecutionException e) {
      LOG.error("An error occurred while waiting for shutdown message", e);
      if (e instanceof InterruptedException) {
        // set the interrupt flag
        Thread.currentThread().interrupt();
      }

    } finally {

      // Cleanup should be performed in ToolingApiServerImpl.shutdown()
      // this is to make sure that the daemons are stopped in case the client doesn't call shutdown()
      try {
        if (server.isInitialized() || server.isConnected()) {
          LOG.warn("Connection to tooling server closed without shutting it down!");
          server.shutdown().get();
        }
      } catch (InterruptedException | ExecutionException e) {
        LOG.error("An error occurred while shutting down tooling API server", e);
      } finally {
        Main.future = null;
        Main.client = null;

        LOG.info("Tooling API server shutdown complete");
      }
    }
  }

  public static void checkGradleWrapper() {
    if (client != null) {
      LOG.info("Checking gradle wrapper availability...");
      try {
        if (!client.checkGradleWrapperAvailability().get().isAvailable()) {
          LOG.warn(
              "Gradle wrapper is not available."
                  + " Client might have failed to ensure availability."
                  + " Build might fail.");
        } else {
          LOG.info("Gradle wrapper is available");
        }
      } catch (Throwable e) {
        LOG.warn("Unable to get Gradle wrapper availability from client", e);
      }
    }
  }

  @SuppressWarnings("NewApi")
  public static void finalizeLauncher(ConfigurableLauncher<?> launcher) {
    final var out = new LoggingOutputStream();
    launcher.setStandardError(out);
    launcher.setStandardOutput(out);
    launcher.setStandardInput(new ByteArrayInputStream("NoOp".getBytes(StandardCharsets.UTF_8)));
    launcher.addProgressListener(new ForwardingProgressListener(), progressUpdateTypes());

    if (client != null) {
      try {
        final var args = client.getBuildArguments().get();
        args.removeIf(Objects::isNull);
        args.removeIf(String::isBlank);

        LOG.debug("Arguments from tooling client: {}", args);
        launcher.addArguments(args);
      } catch (Throwable e) {
        LOG.error("Unable to get build arguments from tooling client", e);
      }
    }
  }

  public static Set<OperationType> progressUpdateTypes() {
    final Set<OperationType> types = new HashSet<>();

    // AndroidIDE currently does not handle any other type of events
    types.add(OperationType.TASK);
    types.add(OperationType.PROJECT_CONFIGURATION);

    return types;
  }
}
