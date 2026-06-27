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



package com.neo.ide.lsp.java.utils

/**
 * @author Akash Yadav
 */
open class JavaType {

  var name: String? = null

  var kind: Int = KIND_UNKNOWN

  var arrayDims: Int = 0

  /**
   * Returns the internal representation of the type.
   */
  fun internalForm(): String {
    val sb = StringBuilder()
    if (arrayDims > 0) {
      sb.append("[".repeat(arrayDims))
    }

    if (kind == KIND_REF) {
      sb.append("L")
    }

    sb.append(name)

    if (kind == KIND_REF) {
      sb.append(";")
    }

    return sb.toString()
  }

  /**
   * Returns `true` if the type represents a primitive type.
   */
  fun isPrimitive(): Boolean {
    return when (kind) {
      KIND_BOOLEAN, KIND_BYTE, KIND_CHAR, KIND_DOUBLE, KIND_FLOAT, KIND_INT, KIND_LONG, KIND_SHORT -> true

      else -> false
    }
  }

  /**
   * Returns `true` if the type represents a void type.
   */
  fun isVoid(): Boolean {
    return kind == KIND_VOID
  }

  /**
   * Returns `true` if the type represents the `java.lang.String` type.
   */
  fun isStringType(): Boolean {
    return name == STRING.name
  }

  /**
   * Returns `true` if the type represents the `java.lang.Object` type.
   */
  fun isObjectType(): Boolean {
    return name == OBJECT.name
  }

  /**
   * Returns `true` if the type represents the `java.lang.Class` type.
   */
  fun isClassType(): Boolean {
    return name == CLASS.name
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JavaType) return false

    if (name != other.name) return false
    if (kind != other.kind) return false
    if (arrayDims != other.arrayDims) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name?.hashCode() ?: 0
    result = 31 * result + kind
    result = 31 * result + arrayDims
    return result
  }

  override fun toString(): String {
    return "JavaType(name=$name, kind=$kind, arrayDims=$arrayDims)"
  }

  companion object {
    const val KIND_UNKNOWN = -1
    const val KIND_BOOLEAN = 0
    const val KIND_BYTE = 1
    const val KIND_CHAR = 2
    const val KIND_DOUBLE = 3
    const val KIND_FLOAT = 4
    const val KIND_INT = 5
    const val KIND_LONG = 6
    const val KIND_SHORT = 7
    const val KIND_VOID = 8
    const val KIND_REF = 9

    const val TYPE_BOOLEAN = 'Z'
    const val TYPE_BYTE = 'B'
    const val TYPE_CHAR = 'C'
    const val TYPE_DOUBLE = 'D'
    const val TYPE_FLOAT = 'F'
    const val TYPE_INT = 'I'
    const val TYPE_LONG = 'J'
    const val TYPE_SHORT = 'S'
    const val TYPE_VOID = 'V'

    @JvmStatic
    val BOOLEAN = newInstance(name = TYPE_BOOLEAN, kind = KIND_BOOLEAN)

    @JvmStatic
    val BYTE = newInstance(name = TYPE_BYTE, kind = KIND_BYTE)

    @JvmStatic
    val CHAR = newInstance(name = TYPE_CHAR, kind = KIND_CHAR)

    @JvmStatic
    val DOUBLE = newInstance(name = TYPE_DOUBLE, kind = KIND_DOUBLE)

    @JvmStatic
    val FLOAT = newInstance(name = TYPE_FLOAT, kind = KIND_FLOAT)

    @JvmStatic
    val INT = newInstance(name = TYPE_INT, kind = KIND_INT)

    @JvmStatic
    val LONG = newInstance(name = TYPE_LONG, kind = KIND_LONG)

    @JvmStatic
    val SHORT = newInstance(name = TYPE_SHORT, kind = KIND_SHORT)

    @JvmStatic
    val VOID = newInstance(name = TYPE_VOID, kind = KIND_VOID)

    @JvmStatic
    val STRING = newInstance(name = "java/lang/String", kind = KIND_REF)

    @JvmStatic
    val OBJECT = newInstance(name = "java/lang/Object", kind = KIND_REF)

    @JvmStatic
    val CLASS = newInstance(name = "java/lang/Class", kind = KIND_REF)

    @JvmStatic
    fun kindForType(type: Char) = when (type) {
      TYPE_BOOLEAN -> KIND_BOOLEAN
      TYPE_BYTE -> KIND_BYTE
      TYPE_CHAR -> KIND_CHAR
      TYPE_DOUBLE -> KIND_DOUBLE
      TYPE_FLOAT -> KIND_FLOAT
      TYPE_INT -> KIND_INT
      TYPE_LONG -> KIND_LONG
      TYPE_SHORT -> KIND_SHORT
      TYPE_VOID -> KIND_VOID
      else -> KIND_UNKNOWN
    }

    @JvmStatic
    fun typeForKind(kind: Int) = when (kind) {
      KIND_BOOLEAN -> TYPE_BOOLEAN
      KIND_BYTE -> TYPE_BYTE
      KIND_CHAR -> TYPE_CHAR
      KIND_DOUBLE -> TYPE_DOUBLE
      KIND_FLOAT -> TYPE_FLOAT
      KIND_INT -> TYPE_INT
      KIND_LONG -> TYPE_LONG
      KIND_SHORT -> TYPE_SHORT
      KIND_VOID -> TYPE_VOID
      else -> TYPE_VOID
    }

    @JvmStatic
    fun primitiveFor(char: Char): JavaType? {
      return when (char) {
        TYPE_BOOLEAN -> BOOLEAN
        TYPE_BYTE -> BYTE
        TYPE_CHAR -> CHAR
        TYPE_DOUBLE -> DOUBLE
        TYPE_FLOAT -> FLOAT
        TYPE_INT -> INT
        TYPE_LONG -> LONG
        TYPE_SHORT -> SHORT
        TYPE_VOID -> VOID
        else -> null
      }
    }

    @JvmStatic
    fun newInstance(name: String?, kind: Int, arrayDims: Int = 0): JavaType {
      return JavaType().apply {
        this.name = name
        this.kind = kind
        this.arrayDims = arrayDims
      }
    }

    @JvmStatic
    fun newInstance(name: Char, kind: Int, arrayDims: Int = 0): JavaType {
      return newInstance(name.toString(), kind, arrayDims)
    }
  }
}