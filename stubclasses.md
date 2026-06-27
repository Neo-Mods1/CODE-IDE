# Stub Classes Reference

This document lists all stub/minimal classes created to satisfy compilation.
These are placeholder implementations that need to be properly implemented later.

---

## Copied from AndroidIDE (package renamed `com.itsaky.androidide` → `com.neo.ide`)

### Lookup
| File | Package | Type |
|------|---------|------|
| `Lookup.java` | `com.neo.ide.lookup` | Service locator |
| `LookupProvider.java` | `com.neo.ide.lookup` | Lookup provider |
| `ForwardingLookup.kt` | `com.neo.ide.lookup` | Delegating lookup |
| `ServiceRegisteredException.java` | `com.neo.ide.lookup` | Exception |
| `internal/DefaultLookup.java` | `com.neo.ide.lookup` | Default impl |

### Utils
| File | Package | Type |
|------|---------|------|
| `StopWatch.kt` | `com.neo.ide.utils` | Timing utility |
| `DocumentUtils.java` | `com.neo.ide.utils` | Document helpers |
| `SourceClassTrie.kt` | `com.neo.ide.utils` | Source class index |
| `ClassTrie.kt` | `com.neo.ide.utils` | Class index |
| `ServiceLoader.java` | `com.neo.ide.utils` | Service loading |
| `Cache.java` | `com.neo.ide.utils` | Generic cache |

### Progress
| File | Package | Type |
|------|---------|------|
| `ProgressManager.kt` | `com.neo.ide.progress` | Progress tracking |

### EventBus Events
| File | Package | Type |
|------|---------|------|
| `EventReceiver.kt` | `com.neo.ide.eventbus.events` | Event receiver |
| `editor/DocumentEvents.kt` | `com.neo.ide.eventbus.events.editor` | Document events |
| `file/FileEvents.kt` | `com.neo.ide.eventbus.events.file` | File events |
| `project/ProjectEvents.kt` | `com.neo.ide.eventbus.events.project` | Project events |

### Javac Services
| File | Package | Type |
|------|---------|------|
| `CacheFSInfoSingleton.kt` | `com.neo.ide.javac.services.fs` | FS info cache |
| `CachedJarFileSystem.kt` | `com.neo.ide.javac.services.fs` | Cached JAR FS |
| `CachingJarFileSystemProvider.kt` | `com.neo.ide.javac.services.fs` | JAR FS provider |

### XML Resources
| File | Package | Type |
|------|---------|------|
| `IResourceTable.kt` | `com.neo.ide.xml.res` | Resource table API |
| `ResourceTableRegistry.kt` | `com.neo.ide.xml.resources` | Resource table registry |

### Tasks
| File | Package | Type |
|------|---------|------|
| `TaskExecutor.kt` | `com.neo.ide.tasks` | Async task execution |

### Tooling API
| File | Package | Type |
|------|---------|------|
| `IProject.kt` | `com.neo.ide.tooling.api` | Project interface |
| `IAndroidProject.kt` | `com.neo.ide.tooling.api` | Android project |
| `IGradleProject.kt` | `com.neo.ide.tooling.api` | Gradle project |
| `IJavaProject.kt` | `com.neo.ide.tooling.api` | Java project |
| `ProjectType.kt` | `com.neo.ide.tooling.api` | Project type enum |
| `GradleTask.kt` | `com.neo.ide.tooling.api.models` | Gradle task model |
| `BasicProjectMetadata.kt` | `com.neo.ide.tooling.api.models` | Basic project info |
| `BasicAndroidVariantMetadata.kt` | `com.neo.ide.tooling.api.models` | Variant info |
| `BuildVariantInfo.kt` | `com.neo.ide.tooling.api.models` | Build variant info |
| `ToolingServerMetadata.kt` | `com.neo.ide.tooling.api.models` | Server metadata |
| `StringParameter.kt` | `com.neo.ide.tooling.api.models.params` | String param |
| `SelectProjectResult.kt` | `com.neo.ide.tooling.api.models.result` | Select result |
| `utils.kt` | `com.neo.ide.tooling.api.util` | `findPackageName()` |

### Builder Model
| File | Package | Type |
|------|---------|------|
| `DefaultAndroidGradlePluginProjectFlags.kt` | `com.neo.ide.builder.model` | AGP flags |
| `DefaultJavaCompileOptions.kt` | `com.neo.ide.builder.model` | Java compile opts |
| `DefaultLibrary.kt` | `com.neo.ide.builder.model` | Library model |
| `DefaultProjectSyncIssues.kt` | `com.neo.ide.builder.model` | Sync issues |
| `DefaultSourceSetContainer.kt` | `com.neo.ide.builder.model` | Source sets |
| `DefaultViewBindingOptions.kt` | `com.neo.ide.builder.model` | View binding opts |
| `IJavaCompilerSettings.kt` | `com.neo.ide.builder.model` | Compiler settings |
| `modelConstants.kt` | `com.neo.ide.builder.model` | `UNKNOWN_PACKAGE` |

---

## Created as Stubs (minimal/no-op implementations)

| File | Package | Type | Notes |
|------|---------|------|-------|
| `BuildConfig.kt` | `com.neo.ide` | object | App build config |
| `BaseApplication.kt` | `com.neo.ide.app` | open class | Notification channels |
| `ToolsManager.kt` | `com.neo.ide.managers` | object | Asset management |
| `BuildPreferences.kt` | `com.neo.ide.preferences.internal` | object | HashMap-backed prefs |
| `DevOpsPreferences.kt` | `com.neo.ide.preferences.internal` | object | HashMap-backed prefs |
| `Event.kt` | `com.neo.ide.eventbus.events` | open class | Base event |
| `LogLine.kt` | `com.neo.ide.models` | data class | Log entry |
| `Range.kt` | `com.neo.ide.models` | data class | Text range |
| `ToolingServerNotStartedException.kt` | `com.neo.ide.services` | class | Exception |
| `ICancelChecker.kt` | `com.neo.ide.progress` | interface | Cancel check |
| `CoroutineUtils.kt` | `com.neo.ide.tasks` | ext fn | `cancelIfActive()` |
| `ThrowableUtils.kt` | `com.neo.ide.tasks` | ext fn | `ifCancelledOrInterrupted()` |
| `ShellUtils.kt` | `com.neo.ide.shell` | ext fn | `executeProcessAsync()` |
| `IProjectQueries.kt` | `com.neo.ide.tooling.api` | interface | Project queries |
| `IModuleProject.kt` | `com.neo.ide.tooling.api` | interface | Module project |
| `LogSenderConfig.kt` | `com.neo.ide.tooling.api` | object | Config constants |
| `AndroidArtifactMetadata.kt` | `com.neo.ide.tooling.api.models` | open class | Artifact info |
| `AndroidProjectMetadata.kt` | `com.neo.ide.tooling.api.models` | open class | Android project |
| `AndroidVariantMetadata.kt` | `com.neo.ide.tooling.api.models` | open class | Variant info |
| `GradleArtifact.kt` | `com.neo.ide.tooling.api.models` | open class | Gradle artifact |
| `JavaContentRoot.kt` | `com.neo.ide.tooling.api.models` | open class | Content root |
| `JavaModuleCompilerSettings.kt` | `com.neo.ide.tooling.api.models` | open class | Compiler settings |
| `JavaModuleDependency.kt` | `com.neo.ide.tooling.api.models` | sealed class | Dependencies |
| `JavaProjectMetadata.kt` | `com.neo.ide.tooling.api.models` | open class | Java project |
| `JavaSourceDirectory.kt` | `com.neo.ide.tooling.api.models` | open class | Source dir |
| `Launchable.kt` | `com.neo.ide.tooling.api.models` | open class | Launchable base |
| `ProjectMetadata.kt` | `com.neo.ide.tooling.api.models` | open class | Base metadata |
| `AndroidModulePropertyCopier.kt` | `com.neo.ide.tooling.api.util` | object | Sync copier |
| `PluginIdentifier.kt` | `com.neo.ide.tooling.model` | data class | Plugin id |
| `ProjectIdentifier.kt` | `com.neo.ide.tooling.model` | data class | Project id |
| `Environment.kt` | `com.neo.ide.utils` | object | Path constants |
| `ILogger.kt` | `com.neo.ide.utils` | interface | Logger |
| `LogUtils.kt` | `com.neo.ide.utils` | object | Stack trace utils |
| `VMUtils.kt` | `com.neo.ide.utils` | object | JVM detection |
| `AndroidPluginVersion.kt` | `com.neo.ide.utils` | class | AGP version |
| `StringExtUtils.kt` | `com.neo.ide.utils` | ext fn | String extensions |
| `FlashbarUtils.kt` | `com.neo.ide.utils` | ext fn | Toast-based flash |
| `JvmStdErrAppender.kt` | `com.neo.ide.logging` | class | Logback appender |
| `IDELogFormatEncoder.kt` | `com.neo.ide.logging.encoder` | class | Logback encoder |
| `XmlRegistry.kt` | `com.neo.ide.xml.registry` | interface | XML registry |
| `IResourceTablePackage.kt` | `com.neo.ide.xml.res` | interface | Resource pkg |
| `ISearchResult.kt` | `com.neo.ide.xml.res` | interface | Search result |
| `ApiVersions.kt` | `com.neo.ide.xml.versions` | class | API versions |
| `ApiVersionsRegistry.kt` | `com.neo.ide.xml.versions` | interface | Versions registry |
| `WidgetTable.kt` | `com.neo.ide.xml.widgets` | class | Widget table |
| `WidgetTableRegistry.kt` | `com.neo.ide.xml.widgets` | interface | Widget registry |
| `ZipFileSystemProvider.kt` | `com.neo.ide.zipfs2` | open class | ZIP provider |
| `ZipFileSystem.kt` | `com.neo.ide.zipfs2` | open class | ZIP filesystem |
| `JarFileSystemProvider.kt` | `com.neo.ide.zipfs2` | class | JAR provider |
| `projects/R.kt` | `com.neo.ide.projects` | object | Resource IDs |
| `resources/R.kt` | `com.neo.ide.resources` | object | Resource IDs |

---

## Simplified/Replaced (not copied from AndroidIDE)

| File | Package | Type | Notes |
|------|---------|------|-------|
| `GradleBuildService.kt` | `com.neo.ide.services.builder` | class | Simplified service impl |
| `TaskExecutor.kt` | `com.neo.ide.tasks` | object | Uses Handler instead of ThreadUtils |

---

## Total: 93 stub/copied files

### What still needs real implementation:
1. **GradleBuildService** — simplified, needs Gradle Tooling API integration
2. **ProjectManagerImpl** — needs workspace/project management
3. **AndroidModule** — needs Android project model building
4. **ModuleProject** — needs source/class path resolution
5. **FileManager** — needs file event handling
6. **All XML/Resource table classes** — need AAPT2 resource parsing
7. **Javac services** — need JAR filesystem caching
8. **FlashbarUtils** — uses Toast, should use Material Snackbar
