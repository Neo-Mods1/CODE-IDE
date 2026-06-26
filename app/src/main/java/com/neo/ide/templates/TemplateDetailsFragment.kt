/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.neo.ide.R
import com.neo.ide.project.ProjectGenerator

class TemplateDetailsFragment : Fragment() {

    private var template: ProjectTemplate? = null
    private var widgetsRecycler: RecyclerView? = null
    private var progress: LinearProgressIndicator? = null
    private var title: MaterialTextView? = null
    private var onProjectCreated: ((String) -> Unit)? = null
    private var onPrevious: (() -> Unit)? = null

    private var projectName = "My Application"
    private var packageName = "com.example.myapplication"
    private var saveLocation = ""
    private var language = ProjectTemplate.TemplateLanguage.JAVA
    private var useKts = false
    private var minSdk = 21
    private var targetSdk = 34
    private var compileSdk = 34

    private var languageIndex = 0
    private var minSdkIndex = 0
    private var targetSdkIndex = 0
    private var compileSdkIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_template_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        widgetsRecycler = view.findViewById(R.id.widgets)
        progress = view.findViewById(R.id.progress)
        title = view.findViewById(R.id.title)
        val previousButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.previous)
        val finishButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.finish)

        saveLocation = com.neo.ide.utils.IDEEnvironment.PROJECTS_DIR.absolutePath

        widgetsRecycler?.layoutManager = LinearLayoutManager(requireContext())

        previousButton.setOnClickListener { onPrevious?.invoke() }
        finishButton.setOnClickListener { createProject() }

        template?.let { bindTemplate(it) }
    }

    fun setTemplate(t: ProjectTemplate) {
        template = t
        if (view != null) bindTemplate(t)
    }

    fun setOnProjectCreatedListener(listener: (String) -> Unit) {
        onProjectCreated = listener
    }

    fun setOnPreviousListener(listener: () -> Unit) {
        onPrevious = listener
    }

    private fun bindTemplate(template: ProjectTemplate) {
        title?.setText(template.nameRes)

        val sdkVersions = SdkVersion.entries.toList()
        val sdkDisplayNames = sdkVersions.map { it.displayName() }
        val languageNames = buildList {
            if (template.supportsJava) add(ProjectTemplate.TemplateLanguage.JAVA.displayName)
            if (template.supportsKotlin) add(ProjectTemplate.TemplateLanguage.KOTLIN.displayName)
        }

        languageIndex = 0
        minSdkIndex = sdkVersions.indexOfFirst { it.api == minSdk }.coerceAtLeast(0)
        targetSdkIndex = sdkVersions.indexOfFirst { it.api == targetSdk }.coerceAtLeast(0)
        compileSdkIndex = sdkVersions.indexOfFirst { it.api == compileSdk }.coerceAtLeast(0)

        val widgets = mutableListOf<TemplateWidgetsAdapter.TemplateWidget>()

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.TextField(
            label = getString(R.string.tpl_project_name),
            value = projectName,
            onValueChanged = { projectName = it }
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.TextField(
            label = getString(R.string.package_name),
            value = packageName,
            onValueChanged = { packageName = it }
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.TextField(
            label = getString(R.string.save_location),
            value = saveLocation,
            editable = false
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.Spinner(
            label = getString(R.string.language),
            items = languageNames,
            selectedIndex = languageIndex,
            onItemSelected = { idx ->
                languageIndex = idx
                language = if (idx == 0 && template.supportsJava) {
                    ProjectTemplate.TemplateLanguage.JAVA
                } else {
                    ProjectTemplate.TemplateLanguage.KOTLIN
                }
            }
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.Spinner(
            label = getString(R.string.min_sdk),
            items = sdkDisplayNames,
            selectedIndex = minSdkIndex,
            onItemSelected = { idx ->
                minSdkIndex = idx
                minSdk = sdkVersions[idx].api
            }
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.Spinner(
            label = getString(R.string.target_sdk),
            items = sdkDisplayNames,
            selectedIndex = targetSdkIndex,
            onItemSelected = { idx ->
                targetSdkIndex = idx
                targetSdk = sdkVersions[idx].api
            }
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.Spinner(
            label = getString(R.string.compile_sdk),
            items = sdkDisplayNames,
            selectedIndex = compileSdkIndex,
            onItemSelected = { idx ->
                compileSdkIndex = idx
                compileSdk = sdkVersions[idx].api
            }
        ))

        widgets.add(TemplateWidgetsAdapter.TemplateWidget.CheckBox(
            label = getString(R.string.use_kts),
            checked = useKts,
            onCheckedChanged = { useKts = it }
        ))

        widgetsRecycler?.adapter = TemplateWidgetsAdapter(widgets) {}
    }

    private fun createProject() {
        val tmpl = template ?: return

        if (projectName.isBlank()) {
            Toast.makeText(requireContext(), R.string.invalid_project_name, Toast.LENGTH_SHORT).show()
            return
        }

        if (packageName.isBlank() || !packageName.matches(Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)*$"))) {
            Toast.makeText(requireContext(), R.string.invalid_package_name, Toast.LENGTH_SHORT).show()
            return
        }

        val projectDir = java.io.File(saveLocation, projectName)
        if (projectDir.exists() && projectDir.listFiles()?.isNotEmpty() == true) {
            Toast.makeText(requireContext(), R.string.project_already_exists, Toast.LENGTH_SHORT).show()
            return
        }

        progress?.visibility = View.VISIBLE

        Thread {
            try {
                val generator = ProjectGenerator(
                    projectName = projectName,
                    packageName = packageName,
                    projectDir = projectDir,
                    language = language,
                    useKts = useKts,
                    minSdk = minSdk,
                    targetSdk = targetSdk,
                    compileSdk = compileSdk,
                    template = tmpl
                )
                val result = generator.generate()

                activity?.runOnUiThread {
                    progress?.visibility = View.GONE
                    if (result.isSuccess) {
                        Toast.makeText(requireContext(), R.string.project_created, Toast.LENGTH_SHORT).show()
                        onProjectCreated?.invoke(projectDir.absolutePath)
                    } else {
                        Toast.makeText(requireContext(),
                            result.exceptionOrNull()?.message ?: getString(R.string.project_creation_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    progress?.visibility = View.GONE
                    Toast.makeText(requireContext(), e.message ?: getString(R.string.project_creation_failed), Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        widgetsRecycler = null
        progress = null
        title = null
    }
}
