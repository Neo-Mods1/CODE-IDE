package com.neo.ide.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.neo.ide.R
import java.io.File

class OpenProjectActivity : AppCompatActivity() {

    private val folderPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { handleFolderSelected(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_project)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.open_project)

        toolbar.setNavigationOnClickListener { finish() }

        val selectButton = findViewById<MaterialButton>(R.id.select_folder_button)
        selectButton.setOnClickListener {
            folderPicker.launch(null)
        }

        loadRecentProjects()
    }

    private fun handleFolderSelected(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            // Some providers don't support persistable permissions
        }

        val path = getPathFromUri(uri)
        if (path != null) {
            val dir = File(path)
            if (dir.exists() && dir.isDirectory) {
                val isProject = dir.listFiles()?.any {
                    it.name == "build.gradle" || it.name == "build.gradle.kts"
                            || it.name == "settings.gradle" || it.name == "settings.gradle.kts"
                            || it.name == "app"
                } == true

                if (isProject) {
                    RecentProjectsManager.addProject(this, path)
                    val resultIntent = Intent().apply {
                        putExtra("project_path", path)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, R.string.no_project_selected, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        val docId = uri.lastPathSegment ?: return null

        if (docId.contains("primary:")) {
            val path = docId.substringAfter("primary:")
            return "${android.os.Environment.getExternalStorageDirectory()}/$path"
        }

        if (docId.startsWith("/")) return docId

        return uri.path
    }

    private fun loadRecentProjects() {
        val recentList = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.recent_projects_list)
        val recentProjects = RecentProjectsManager.getRecentProjects(this)

        if (recentProjects.isEmpty()) {
            recentList.text = getString(R.string.no_recent_projects)
        } else {
            recentList.text = recentProjects.joinToString("\n") { path ->
                val name = File(path).name
                "$name\n$path"
            }
        }
    }
}
