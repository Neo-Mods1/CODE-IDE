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

package com.neo.ide.ui

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import com.neo.ide.R

class DraggableBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var isExpanded = true
    private var isDragging = false
    private var lastY = 0f
    private var animator: ValueAnimator? = null

    private val minHeight: Int = resources.getDimension(R.dimen.bottom_panel_min_height).toInt()
    private val defaultHeight: Int = resources.getDimension(R.dimen.bottom_panel_default_height).toInt()
    private val maxHeight: Int = resources.getDimension(R.dimen.bottom_panel_max_height).toInt()

    private var dragHandle: View? = null
    private var onHeightChanged: ((Int) -> Unit)? = null

    init {
        orientation = VERTICAL
    }

    fun setDragHandle(handle: View) {
        dragHandle = handle
        handle.setOnTouchListener { _, event -> handleDrag(event) }
    }

    fun setOnHeightChangedListener(listener: (Int) -> Unit) {
        onHeightChanged = listener
    }

    private fun handleDrag(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
                isDragging = true
                animator?.cancel()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val deltaY = lastY - event.rawY
                    lastY = event.rawY
                    val newHeight = (height + deltaY).toInt().coerceIn(minHeight, maxHeight)
                    updateHeight(newHeight)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                snapToPosition()
                return true
            }
        }
        return false
    }

    private fun updateHeight(newHeight: Int) {
        val params = layoutParams
        params.height = newHeight
        layoutParams = params
        onHeightChanged?.invoke(newHeight)
    }

    private fun snapToPosition() {
        val currentHeight = height
        val targetHeight = when {
            currentHeight < minHeight + (defaultHeight - minHeight) / 2 -> minHeight
            currentHeight > maxHeight - (maxHeight - defaultHeight) / 2 -> maxHeight
            else -> defaultHeight
        }

        animator = ValueAnimator.ofInt(currentHeight, targetHeight).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                updateHeight(animation.animatedValue as Int)
            }
            start()
        }
    }

    fun toggle() {
        if (isExpanded) {
            collapse()
        } else {
            expand()
        }
    }

    fun expand() {
        isExpanded = true
        animateToHeight(defaultHeight)
    }

    fun collapse() {
        isExpanded = false
        animateToHeight(minHeight)
    }

    fun maximize() {
        isExpanded = true
        animateToHeight(maxHeight)
    }

    private fun animateToHeight(targetHeight: Int) {
        animator?.cancel()
        animator = ValueAnimator.ofInt(height, targetHeight).apply {
            duration = 250
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                updateHeight(animation.animatedValue as Int)
            }
            start()
        }
    }

    fun setExpanded(expanded: Boolean) {
        isExpanded = expanded
        if (expanded) {
            updateHeight(defaultHeight)
        } else {
            updateHeight(minHeight)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (isTouchOnHandle(ev)) {
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun isTouchOnHandle(event: MotionEvent): Boolean {
        dragHandle?.let { handle ->
            val location = IntArray(2)
            handle.getLocationOnScreen(location)
            val handleTop = location[1]
            val handleBottom = handleTop + handle.height
            val touchY = event.rawY.toInt()
            return touchY in handleTop..handleBottom
        }
        return false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
