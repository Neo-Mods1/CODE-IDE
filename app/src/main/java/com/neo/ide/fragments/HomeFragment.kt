/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R
import com.neo.ide.activities.MainActivity
import com.neo.ide.adapters.HomeActionsAdapter
import com.neo.ide.models.HomeScreenAction
import com.neo.ide.setup.TerminalSetupActivity

class HomeFragment : Fragment() {

    private var actionsList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionsList = view.findViewById(R.id.actions_list)

        val actions = HomeScreenAction(0, 0, 0).all().map { action ->
            action.apply {
                onClick = { handleAction(it) }
            }
        }

        actionsList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HomeActionsAdapter(actions)
        }
    }

    private fun handleAction(action: HomeScreenAction) {
        when (action.id) {
            HomeScreenAction.ACTION_CREATE_PROJECT -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            HomeScreenAction.ACTION_OPEN_PROJECT -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            HomeScreenAction.ACTION_OPEN_TERMINAL -> {
                startActivity(Intent(requireContext(), TerminalSetupActivity::class.java))
            }
            HomeScreenAction.ACTION_PREFERENCES -> {
                // TODO: Open preferences
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionsList = null
    }
}
