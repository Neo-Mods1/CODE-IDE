package com.neo.ide.setup

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import com.termux.view.TerminalView

class ExtraKeysView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var terminalView: TerminalView? = null
    private var ctrlActive = false
    private var altActive = false

    private val keys = arrayOf(
        arrayOf("ESC", "/", "-", "HOME", "UP", "END", "PGUP"),
        arrayOf("TAB", "CTRL", "ALT", "LEFT", "DOWN", "RIGHT", "PGDN")
    )

    init {
        rowCount = 2
        columnCount = 7
        setBackgroundColor(Color.BLACK)
        setPadding(0, 2, 0, 2)
        buildKeys()
    }

    fun setTerminalView(view: TerminalView?) {
        terminalView = view
    }

    private fun buildKeys() {
        for ((row, rowKeys) in keys.withIndex()) {
            for ((col, key) in rowKeys.withIndex()) {
                val btn = TextView(context).apply {
                    text = getDisplayText(key)
                    setTextColor(Color.WHITE)
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    setPadding(8, 6, 8, 6)
                    gravity = android.view.Gravity.CENTER
                    isClickable = true
                    isFocusable = true

                    val bg = android.graphics.drawable.GradientDrawable().apply {
                        setColor(Color.parseColor("#1AFFFFFF"))
                        setStroke(1, Color.parseColor("#33FFFFFF"))
                        cornerRadius = 4f
                    }
                    background = bg

                    setOnClickListener { onKeyPress(key) }
                    setOnTouchListener { v, event ->
                        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                            (v.background as? android.graphics.drawable.GradientDrawable)
                                ?.setColor(Color.parseColor("#33FFFFFF"))
                        } else if (event.action == android.view.MotionEvent.ACTION_UP ||
                            event.action == android.view.MotionEvent.ACTION_CANCEL) {
                            (v.background as? android.graphics.drawable.GradientDrawable)
                                ?.setColor(Color.parseColor("#1AFFFFFF"))
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
        return when (key) {
            "ESC" -> "ESC"
            "TAB" -> "TAB"
            "CTRL" -> if (ctrlActive) "⎈" else "CTRL"
            "ALT" -> if (altActive) "⎇" else "ALT"
            "HOME" -> "⇱"
            "END" -> "⇲"
            "PGUP" -> "⇑"
            "PGDN" -> "⇓"
            "UP" -> "↑"
            "DOWN" -> "↓"
            "LEFT" -> "←"
            "RIGHT" -> "→"
            else -> key
        }
    }

    private fun onKeyPress(key: String) {
        val view = terminalView ?: return
        val session = view.mTermSession ?: return

        when (key) {
            "CTRL" -> {
                ctrlActive = !ctrlActive
                updateKeyAppearance()
                return
            }
            "ALT" -> {
                altActive = !altActive
                updateKeyAppearance()
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
                if (ctrlActive) {
                    session.writeCodePoint(true, codePoint)
                    ctrlActive = false
                } else if (altActive) {
                    session.writeCodePoint(true, codePoint)
                    altActive = false
                } else {
                    session.write(byteArrayOf('-'.code.toByte()), 0, 1)
                }
                updateKeyAppearance()
            }
            "/" -> session.write(byteArrayOf('/'.code.toByte()), 0, 1)
        }

        if (key != "CTRL" && key != "ALT") {
            if (ctrlActive) {
                ctrlActive = false
                updateKeyAppearance()
            }
            if (altActive) {
                altActive = false
                updateKeyAppearance()
            }
        }
    }

    private fun sendKey(view: TerminalView, keyCode: Int) {
        val downTime = android.os.SystemClock.uptimeMillis()
        val event = KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, keyCode, 0,
            if (ctrlActive) KeyEvent.META_CTRL_ON else 0)
        view.onKeyDown(keyCode, event)
    }

    private fun updateKeyAppearance() {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as? TextView ?: continue
            val key = getKeyForIndex(i) ?: continue
            when (key) {
                "CTRL" -> {
                    child.setTextColor(if (ctrlActive) Color.parseColor("#FF0000") else Color.WHITE)
                    child.text = if (ctrlActive) "⎈" else "CTRL"
                }
                "ALT" -> {
                    child.setTextColor(if (altActive) Color.parseColor("#FF0000") else Color.WHITE)
                    child.text = if (altActive) "⎇" else "ALT"
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
