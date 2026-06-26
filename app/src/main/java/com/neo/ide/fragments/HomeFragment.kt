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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R
import com.neo.ide.activities.MainActivity
import com.neo.ide.adapters.HomeActionsAdapter
import com.neo.ide.models.HomeScreenAction
import com.neo.ide.project.CreateProjectActivity
import com.neo.ide.project.OpenProjectActivity
import com.neo.ide.project.RecentProjectsManager
import com.neo.ide.setup.TerminalSetupActivity
import com.neo.ide.terminal.TerminalActivity

class HomeFragment : Fragment() {

    private var actionsList: RecyclerView? = null

    private val createProjectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val projectPath = result.data?.getStringExtra("project_path")
        if (projectPath != null) {
            openProject(projectPath)
        }
    }

    private val openProjectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val projectPath = result.data?.getStringExtra("project_path")
        if (projectPath != null) {
            openProject(projectPath)
        }
    }

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
                createProjectLauncher.launch(Intent(requireContext(), CreateProjectActivity::class.java))
            }
            HomeScreenAction.ACTION_OPEN_PROJECT -> {
                openProjectLauncher.launch(Intent(requireContext(), OpenProjectActivity::class.java))
            }
            HomeScreenAction.ACTION_OPEN_TERMINAL -> {
                startActivity(Intent(requireContext(), TerminalActivity::class.java))
            }
            HomeScreenAction.ACTION_PREFERENCES -> {
                // TODO: Open preferences
            }
        }
    }

    private fun openProject(projectPath: String) {
        RecentProjectsManager.addProject(requireContext(), projectPath)
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("project_path", projectPath)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionsList = null
    }
}
