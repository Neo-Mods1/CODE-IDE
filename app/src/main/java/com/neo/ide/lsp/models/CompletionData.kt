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



package com.neo.ide.lsp.models

/** Contains information about a completion item. */
interface ICompletionData

/**
 * Information about a class-related completion item.
 *
 * @property className The fully qualified name of the class. Example: `pck.outer.inner`.
 * @property flatName The flat name of the class. Example: `pck.outer$inner`.
 * @property isCompleteData Whether the data provided by this [ClassCompletionData] is complete.
 * @property isNested Whether the given class is a nested class or not.
 * @property topLevelClass If [isNested] is true, then this must be set to the fully qualified name
 * of the top-level class.
 * @property simpleName The simple name of the class.
 * @property nameWithoutTopLevel The name of this class without the fully qualified name of its top
 * level class. For example, the value of this property for class name
 * `com.my.pck.MyClass.Inner.InnerInner` will be `Inner.InnerInner`
 */
data class ClassCompletionData
@JvmOverloads
constructor(
  val className: String,
  val isCompleteData: Boolean = false,
  val flatName: String = className,
  val simpleName: String = className.substringAfterLast(delimiter = '.'),
  val isNested: Boolean = false,
  val topLevelClass: String = ""
) : ICompletionData {

  val nameWithoutTopLevel: String = if (isNested) {
    className.substring(topLevelClass.length + 1)
  } else {
    className
  }
}

/**
 * Information about a member of a class.
 *
 * @property memberName The simple name of the class member.
 * @property classInfo Information about the class [memberName] is a member of.
 */
interface MemberCompletionData : ICompletionData {
  val memberName: String
  val classInfo: ClassCompletionData
}

/** Information about a field-related completion item. */
data class FieldCompletionData(
  override val memberName: String,
  override val classInfo: ClassCompletionData
) : MemberCompletionData

/**
 * Information about a method-related completion item.
 *
 * @property erasedParameterTypes The erased parameter types of the method.
 * @property plusOverloads The number of existing overloaded versions of this method.
 */
data class MethodCompletionData(
  override val memberName: String,
  override val classInfo: ClassCompletionData,
  val parameterTypes: List<String>,
  val erasedParameterTypes: List<String>,
  val plusOverloads: Int
) : MemberCompletionData
