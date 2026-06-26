package com.neo.ide.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.neo.ide.R
import com.neo.ide.app.BaseActivity
import com.neo.ide.templates.TemplateDetailsFragment
import com.neo.ide.templates.TemplateListFragment
import com.neo.ide.templates.ProjectTemplate

class CreateProjectActivity : BaseActivity() {

    private var selectedTemplate: ProjectTemplate? = null

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_create_project, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            showTemplateList()
        } else {
            selectedTemplate = savedInstanceState.getSerializable("template") as? ProjectTemplate
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("template", selectedTemplate)
    }

    private fun showTemplateList() {
        val fragment = TemplateListFragment()
        fragment.setOnTemplateSelectedListener { template ->
            selectedTemplate = template
            showTemplateDetails(template)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showTemplateDetails(template: ProjectTemplate) {
        val fragment = TemplateDetailsFragment()
        fragment.setTemplate(template)
        fragment.setOnPreviousListener {
            supportFragmentManager.popBackStack()
        }
        fragment.setOnProjectCreatedListener { projectPath ->
            RecentProjectsManager.addProject(this, projectPath)
            val resultIntent = Intent().apply {
                putExtra("project_path", projectPath)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
