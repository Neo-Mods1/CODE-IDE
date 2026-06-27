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



package com.neo.ide.lsp.xml.utils

import com.neo.ide.lsp.xml.providers.completion.transformToEntryName

/** Transforms tag names to styleable entry names. */
interface ITagTransformer {
  /** Returns the styleable entry name for the given tag name and its parent's tag name. */
  fun transform(tag: String, parent: String = ""): String
}

internal object NoOpTagTransformer : ITagTransformer {
  override fun transform(tag: String, parent: String): String {
    return ""
  }
}

internal open class SimpleTagTransformer : ITagTransformer {
  override fun transform(tag: String, parent: String): String {
    return transformToEntryName(tag)
  }
}

internal object DrawableTagTransformer : ITagTransformer {
  override fun transform(tag: String, parent: String): String {
    if (tag == "corners") {
      return "DrawableCorners"
    }

    val prefix =
      if (parent == "item" || parent.isNotEmpty()) {
        toEntry(parent).run { if (startsWith('#')) toEntry(tag) else this }
      } else tag
    val suffix =
      if (parent.isNotEmpty() && !parent.startsWith('#')) transformToEntryName(tag) else ""

    return "${prefix}Drawable${suffix}"
  }
}

internal object AnimTagTransformer : ITagTransformer {
  override fun transform(tag: String, parent: String): String {
    if (tag == "set") {
      return "AnimationSet"
    }

    return "${toEntry(tag)}${animEntrySuffix(tag)}"
  }

  /** Returns the suffix that is required to get the styleable entry for an anim resource. */
  private fun animEntrySuffix(tag: String): String {
    return when {
      tag.endsWith("Animation") -> ""
      tag.endsWith("Interpolator") -> ""
      else -> "Animation"
    }
  }
}

internal object AnimatorTagTransformer : SimpleTagTransformer() {
  override fun transform(tag: String, parent: String): String {
    return toEntry(tag)
  }
}

internal object TransitionTagTransformer : SimpleTagTransformer() {
  override fun transform(tag: String, parent: String): String {
    if (tag == "target") {
      return TRANSITION_TARGET
    }
    return super.transform(tag, parent)
  }
}

internal object MenuTagTransformer : ITagTransformer {
  override fun transform(tag: String, parent: String): String {
    return transformToEntryName(tag, "Menu")
  }
}

/**
 * Convert the tag name to entry name.
 *
 * @param tag The tag name.
 */
private fun toEntry(tag: String): String {
  if (tag.startsWith('#')) {
    return tag
  }

  when (tag) {
    "selector" -> return "StateList"
    "animated-selector" -> return "AnimatedStateList"
    "shape" -> return "Gradient"
    "set" -> return "AnimatorSet"
    "keyframe" -> return "KeyFrame"
    "objectAnimator" -> return "PropertyAnimator"
  }

  return transformToEntryName(tag)
}
