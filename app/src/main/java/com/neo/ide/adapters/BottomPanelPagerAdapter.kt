/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neo.ide.fragments.BuildOutputFragment
import com.neo.ide.fragments.LogsFragment
import com.neo.ide.fragments.ProblemsFragment
import com.neo.ide.fragments.SearchResultsFragment
import com.neo.ide.fragments.TerminalFragment

class BottomPanelPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private val fragments = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = PANEL_COUNT

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            PANEL_BUILD -> BuildOutputFragment.newInstance()
            PANEL_PROBLEMS -> ProblemsFragment.newInstance()
            PANEL_LOGS -> LogsFragment.newInstance()
            PANEL_TERMINAL -> TerminalFragment.newInstance()
            PANEL_SEARCH -> SearchResultsFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid panel position: $position")
        }
        fragments[position] = fragment
        return fragment
    }

    fun getFragment(position: Int): Fragment? {
        return fragments[position]
    }

    fun getPanelTitle(position: Int): String {
        return when (position) {
            PANEL_BUILD -> "Build"
            PANEL_PROBLEMS -> "Problems"
            PANEL_LOGS -> "Logs"
            PANEL_TERMINAL -> "Terminal"
            PANEL_SEARCH -> "Search"
            else -> ""
        }
    }

    companion object {
        const val PANEL_COUNT = 5
        const val PANEL_BUILD = 0
        const val PANEL_PROBLEMS = 1
        const val PANEL_LOGS = 2
        const val PANEL_TERMINAL = 3
        const val PANEL_SEARCH = 4
    }
}
