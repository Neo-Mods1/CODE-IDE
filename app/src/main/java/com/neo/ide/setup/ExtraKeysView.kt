/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.setup

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.termux.view.TerminalView

class ExtraKeysView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var terminalView: TerminalView? = null
    private var ctrlActive = false
    private var altActive = false
    private var shiftActive = false

    // AndroidIDE default layout: 2 rows x 7 columns
    private val keys = arrayOf(
        arrayOf("ESC", "/", "-", "HOME", "UP", "END", "PGUP"),
        arrayOf("TAB", "CTRL", "ALT", "LEFT", "DOWN", "RIGHT", "PGDN")
    )

    init {
        rowCount = 2
        columnCount = 7
        setBackgroundColor(Color.BLACK)
        setPadding(2, 2, 2, 4)
        buildKeys()
    }

    fun setTerminalView(view: TerminalView?) {
        terminalView = view
    }

    private fun buildKeys() {
        removeAllViews()
        for ((row, rowKeys) in keys.withIndex()) {
            for ((col, key) in rowKeys.withIndex()) {
                val btn = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                    text = getDisplayText(key)
                    setTextColor(Color.WHITE)
                    setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL), Typeface.NORMAL)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    insetTop = 0
                    insetBottom = 0
                    minimumHeight = 0
                    minimumWidth = 0
                    gravity = Gravity.CENTER
                    isAllCaps = false

                    val bg = android.graphics.drawable.GradientDrawable().apply {
                        setColor(Color.parseColor("#22FFFFFF"))
                        setStroke(1, Color.parseColor("#33FFFFFF"))
                        cornerRadius = 4f
                    }
                    background = bg
                    stateListAnimator = null

                    setOnClickListener { onKeyPress(key) }
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                (v.background as? android.graphics.drawable.GradientDrawable)
                                    ?.setColor(Color.parseColor("#44FFFFFF"))
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                (v.background as? android.graphics.drawable.GradientDrawable)
                                    ?.setColor(Color.parseColor("#22FFFFFF"))
                            }
                        }
                        false
                    }
                }

                val params = LayoutParams().apply {
                    width = 0
                    height = LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(col, 1f)
                    rowSpec = GridLayout.spec(row)
                    setMargins(2, 2, 2, 2)
                }
                addView(btn, params)
            }
        }
    }

    private fun getDisplayText(key: String): String {
        if (ctrlActive && key == "CTRL") return "CTRL ON"
        if (altActive && key == "ALT") return "ALT ON"
        return when (key) {
            "ESC" -> "ESC"
            "TAB" -> "⇥ TAB"
            "CTRL" -> "CTRL"
            "ALT" -> "ALT"
            "HOME" -> "↖"
            "END" -> "↘"
            "PGUP" -> "⇈"
            "PGDN" -> "⇊"
            "UP" -> "↑"
            "DOWN" -> "↓"
            "LEFT" -> "←"
            "RIGHT" -> "→"
            "-" -> "–"
            "/" -> "/"
            else -> key
        }
    }

    private fun onKeyPress(key: String) {
        val view = terminalView ?: return
        val session = view.mTermSession ?: return

        when (key) {
            "CTRL" -> {
                ctrlActive = !ctrlActive
                updateModifierAppearance()
                return
            }
            "ALT" -> {
                altActive = !altActive
                updateModifierAppearance()
                return
            }
            "ESC" -> sendKey(view, KeyEvent.KEYCODE_ESCAPE)
            "TAB" -> sendKey(view, KeyEvent.KEYCODE_TAB)
            "HOME" -> sendKey(view, KeyEvent.KEYCODE_MOVE_HOME)
            "END" -> sendKey(view, KeyEvent.KEYCODE_MOVE_END)
            "PGUP" -> sendKey(view, KeyEvent.KEYCODE_PAGE_UP)
            "PGDN" -> sendKey(view, KeyEvent.KEYCODE_PAGE_DOWN)
            "UP" -> sendKey(view, KeyEvent.KEYCODE_DPAD_UP)
            "DOWN" -> sendKey(view, KeyEvent.KEYCODE_DPAD_DOWN)
            "LEFT" -> sendKey(view, KeyEvent.KEYCODE_DPAD_LEFT)
            "RIGHT" -> sendKey(view, KeyEvent.KEYCODE_DPAD_RIGHT)
            "-" -> {
                val codePoint = '-'.code
                session.writeCodePoint(ctrlActive || altActive, codePoint)
            }
            "/" -> session.write(byteArrayOf('/'.code.toByte()), 0, 1)
        }

        // Auto-reset modifiers after key press
        if (ctrlActive) { ctrlActive = false; updateModifierAppearance() }
        if (altActive) { altActive = false; updateModifierAppearance() }
    }

    private fun sendKey(view: TerminalView, keyCode: Int) {
        val downTime = android.os.SystemClock.uptimeMillis()
        var metaState = 0
        if (ctrlActive) metaState = metaState or KeyEvent.META_CTRL_ON
        if (altActive) metaState = metaState or KeyEvent.META_ALT_ON
        val event = KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
        view.onKeyDown(keyCode, event)
    }

    private fun updateModifierAppearance() {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as? MaterialButton ?: continue
            val key = getKeyForIndex(i) ?: continue
            when (key) {
                "CTRL" -> {
                    child.text = if (ctrlActive) "CTRL ON" else "CTRL"
                    val bg = child.background as? android.graphics.drawable.GradientDrawable
                    bg?.setColor(if (ctrlActive) Color.parseColor("#66FF0000") else Color.parseColor("#22FFFFFF"))
                }
                "ALT" -> {
                    child.text = if (altActive) "ALT ON" else "ALT"
                    val bg = child.background as? android.graphics.drawable.GradientDrawable
                    bg?.setColor(if (altActive) Color.parseColor("#66FF0000") else Color.parseColor("#22FFFFFF"))
                }
            }
        }
    }

    private fun getKeyForIndex(index: Int): String? {
        val row = index / 7
        val col = index % 7
        if (row < keys.size && col < keys[row].size) return keys[row][col]
        return null
    }
}
