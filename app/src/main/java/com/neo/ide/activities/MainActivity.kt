package com.neo.ide.activities

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.neo.ide.R
import com.neo.ide.adapters.BottomPanelPagerAdapter
import com.neo.ide.adapters.EditorTabAdapter
import com.neo.ide.adapters.FileTreeAdapter
import com.neo.ide.models.EditorTab
import com.neo.ide.models.FileNode
import com.neo.ide.models.FileType
import com.neo.ide.utils.FileUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var editorTabs: TabLayout
    private lateinit var editorContainer: View
    private lateinit var editorPlaceholder: View
    private lateinit var codeEditorArea: FrameLayout
    private lateinit var bottomPanel: LinearLayout
    private lateinit var bottomPanelTabs: TabLayout
    private lateinit var bottomPanelViewpager: ViewPager2
    private lateinit var fileTreeRecycler: RecyclerView
    private lateinit var projectNameText: TextView

    private lateinit var fileTreeAdapter: FileTreeAdapter
    private lateinit var editorTabAdapter: EditorTabAdapter
    private lateinit var bottomPanelPagerAdapter: BottomPanelPagerAdapter

    private val openTabs = mutableListOf<EditorTab>()
    private var activeTab: EditorTab? = null
    private var rootFileNodes = mutableListOf<FileNode>()
    private var currentProjectRoot: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupToolbar()
        setupDrawer()
        setupEditorTabs()
        setupBottomPanel()
        setupFileTree()
        loadDemoProject()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_drawer)
        toolbar = findViewById(R.id.toolbar)
        editorTabs = findViewById(R.id.editor_tabs)
        editorContainer = findViewById(R.id.editor_container)
        editorPlaceholder = findViewById(R.id.editor_placeholder)
        codeEditorArea = findViewById(R.id.code_editor_area)
        bottomPanel = findViewById(R.id.bottom_panel)
        bottomPanelTabs = findViewById(R.id.bottom_panel_tabs)
        bottomPanelViewpager = findViewById(R.id.bottom_panel_viewpager)
        fileTreeRecycler = findViewById(R.id.file_tree_recycler)
        projectNameText = navigationView.findViewById(R.id.drawer_project_name)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = getString(R.string.app_name)
        }

        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.drawer_files -> {
                    showFileTree()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.drawer_search -> {
                    showSearchPanel()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.drawer_logs -> {
                    showLogsPanel()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.drawer_settings -> {
                    showSettings()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }

        val closeBtn = navigationView.findViewById<ImageButton>(R.id.drawer_close_btn)
        closeBtn?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setupEditorTabs() {
        editorTabAdapter = EditorTabAdapter(
            onTabClick = { tab -> switchToTab(tab) },
            onTabLongClick = { tab, view -> showTabContextMenu(tab, view) },
            onTabClose = { tab -> closeTab(tab) }
        )

        val tabRecycler = editorTabs.getChildAt(0) as? RecyclerView
        tabRecycler?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = editorTabAdapter
        }

        editorTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    if (position < openTabs.size) {
                        switchToTab(openTabs[position])
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    if (position < openTabs.size) {
                        showTabContextMenu(openTabs[position], it.view)
                    }
                }
            }
        })
    }

    private fun setupBottomPanel() {
        bottomPanelPagerAdapter = BottomPanelPagerAdapter(this)
        bottomPanelViewpager.adapter = bottomPanelPagerAdapter

        TabLayoutMediator(bottomPanelTabs, bottomPanelViewpager) { tab, position ->
            tab.text = bottomPanelPagerAdapter.getPanelTitle(position)
        }.attach()
    }

    private fun setupFileTree() {
        fileTreeAdapter = FileTreeAdapter(
            onFileClick = { fileNode -> openFile(fileNode) },
            onFileLongClick = { fileNode -> showFileContextMenu(fileNode) },
            onFolderToggle = { fileNode -> toggleFolder(fileNode) }
        )

        fileTreeRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileTreeAdapter
            setHasFixedSize(true)
            itemAnimator = null
        }
    }

    private fun loadDemoProject() {
        val externalStorage = Environment.getExternalStorageDirectory()
        val demoProject = File(externalStorage, "MyProjects/DemoApp")

        if (!demoProject.exists()) {
            demoProject.mkdirs()
            createDemoProjectStructure(demoProject)
        }

        currentProjectRoot = demoProject
        projectNameText.text = demoProject.name

        rootFileNodes.clear()
        rootFileNodes.addAll(FileUtils.buildFileTree(demoProject))
        fileTreeAdapter.submitList(FileUtils.flattenTree(rootFileNodes))

        val allNodes = mutableListOf<FileNode>()
        for (node in rootFileNodes) {
            allNodes.add(node)
            if (node.isDirectory && node.isExpanded) {
                collectExpandedNodes(node, allNodes)
            }
        }
        fileTreeAdapter.submitList(allNodes)
    }

    private fun collectExpandedNodes(node: FileNode, result: MutableList<FileNode>) {
        for (child in node.children) {
            result.add(child)
            if (child.isDirectory && child.isExpanded) {
                collectExpandedNodes(child, result)
            }
        }
    }

    private fun createDemoProjectStructure(root: File) {
        val srcDir = File(root, "app/src/main/java/com/demo/app")
        srcDir.mkdirs()

        File(root, "app/build.gradle.kts").apply {
            writeText("""
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.demo.app"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.demo.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}
            """.trimIndent())
        }

        File(root, "build.gradle.kts").apply {
            writeText("""
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
}
            """.trimIndent())
        }

        File(root, "settings.gradle.kts").apply {
            writeText("""
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DemoApp"
include(":app")
            """.trimIndent())
        }

        File(root, "gradle.properties").apply {
            writeText("android.useAndroidX=true\nkotlin.code.style=official")
        }

        File(srcDir, "MainActivity.kt").apply {
            writeText("""
package com.demo.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
            """.trimIndent())
        }

        File(srcDir, "Application.kt").apply {
            writeText("""
package com.demo.app

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
            """.trimIndent())
        }

        val resDir = File(root, "app/src/main/res/layout")
        resDir.mkdirs()

        File(resDir, "activity_main.xml").apply {
            writeText("""
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textSize="24sp" />
        
</LinearLayout>
            """.trimIndent())
        }

        val manifestDir = File(root, "app/src/main")
        manifestDir.mkdirs()

        File(manifestDir, "AndroidManifest.xml").apply {
            writeText("""
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:name=".Application"
        android:label="Demo App"
        android:theme="@style/Theme.AppCompat.Light.DarkActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
            """.trimIndent())
        }

        val valuesDir = File(root, "app/src/main/res/values")
        valuesDir.mkdirs()

        File(valuesDir, "strings.xml").apply {
            writeText("""
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Demo App</string>
</resources>
            """.trimIndent())
        }
    }

    private fun openFile(fileNode: FileNode) {
        if (fileNode.isDirectory) return

        val existingTab = openTabs.find { it.filePath == fileNode.file.absolutePath }
        if (existingTab != null) {
            switchToTab(existingTab)
            return
        }

        val tab = EditorTab(
            file = fileNode.file,
            fileName = fileNode.name,
            filePath = fileNode.file.absolutePath,
            extension = fileNode.extension
        )

        openTabs.add(tab)
        switchToTab(tab)
        updateTabs()
    }

    private fun switchToTab(tab: EditorTab) {
        activeTab?.isActive = false
        tab.isActive = true
        activeTab = tab

        editorPlaceholder.visibility = View.GONE
        codeEditorArea.visibility = View.VISIBLE

        updateTabs()
        updateEditorContent(tab)
    }

    private fun closeTab(tab: EditorTab) {
        openTabs.remove(tab)
        
        if (activeTab == tab) {
            activeTab = null
            if (openTabs.isNotEmpty()) {
                switchToTab(openTabs.last())
            } else {
                editorPlaceholder.visibility = View.VISIBLE
                codeEditorArea.visibility = View.GONE
            }
        }

        updateTabs()
    }

    private fun closeOtherTabs(tab: EditorTab) {
        openTabs.clear()
        openTabs.add(tab)
        activeTab = tab
        updateTabs()
    }

    private fun closeAllTabs() {
        openTabs.clear()
        activeTab = null
        editorPlaceholder.visibility = View.VISIBLE
        codeEditorArea.visibility = View.GONE
        updateTabs()
    }

    private fun updateTabs() {
        editorTabs.removeAllTabs()
        for (tab in openTabs) {
            val tabItem = editorTabs.newTab()
            tabItem.text = tab.fileName
            tabItem.tag = tab
            editorTabs.addTab(tabItem)
        }

        activeTab?.let { tab ->
            val position = openTabs.indexOf(tab)
            if (position != -1) {
                editorTabs.selectTab(editorTabs.getTabAt(position))
            }
        }
    }

    private fun updateEditorContent(tab: EditorTab) {
        val content = FileUtils.readFileContent(tab.file)
        val editorView = TextView(this).apply {
            text = content
            setTextColor(resources.getColor(R.color.md_theme_onSurface, theme))
            textSize = 14f
            setPadding(
                resources.getDimensionPixelSize(R.dimen.spacing_lg),
                resources.getDimensionPixelSize(R.dimen.spacing_md),
                resources.getDimensionPixelSize(R.dimen.spacing_lg),
                resources.getDimensionPixelSize(R.dimen.spacing_md)
            )
            typeface = android.graphics.Typeface.MONOSPACE
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true
            movementMethod = android.text.method.ScrollingMovementMethod()
        }

        codeEditorArea.removeAllViews()
        codeEditorArea.addView(editorView)
    }

    private fun toggleFolder(fileNode: FileNode) {
        fileNode.isExpanded = !fileNode.isExpanded
        val allNodes = mutableListOf<FileNode>()
        for (node in rootFileNodes) {
            allNodes.add(node)
            if (node.isDirectory && node.isExpanded) {
                collectExpandedNodes(node, allNodes)
            }
        }
        fileTreeAdapter.submitList(allNodes)
    }

    private fun showTabContextMenu(tab: EditorTab, anchorView: View) {
        val popup = PopupMenu(this, anchorView, Gravity.END)
        popup.menuInflater.inflate(R.menu.tab_context_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_tab_save -> {
                    saveTab(tab)
                    true
                }
                R.id.action_tab_format -> {
                    Toast.makeText(this, "Format coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_tab_close -> {
                    closeTab(tab)
                    true
                }
                R.id.action_tab_close_others -> {
                    closeOtherTabs(tab)
                    true
                }
                R.id.action_tab_close_all -> {
                    closeAllTabs()
                    true
                }
                R.id.action_tab_copy_path -> {
                    copyToClipboard(tab.filePath)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showFileContextMenu(fileNode: FileNode) {
        val popup = PopupMenu(this, fileTreeRecycler)
        popup.menu.add(0, 1, 0, getString(R.string.filetree_new_file))
        popup.menu.add(0, 2, 1, getString(R.string.filetree_new_folder))
        popup.menu.add(0, 3, 2, getString(R.string.filetree_rename))
        popup.menu.add(0, 4, 3, getString(R.string.filetree_delete))
        popup.menu.add(0, 5, 4, getString(R.string.filetree_copy))

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    Toast.makeText(this, "New file coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                2 -> {
                    Toast.makeText(this, "New folder coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                3 -> {
                    Toast.makeText(this, "Rename coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                4 -> {
                    Toast.makeText(this, "Delete coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                5 -> {
                    copyToClipboard(fileNode.file.absolutePath)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun saveTab(tab: EditorTab) {
        tab.markDirty()
        updateTabs()
        Toast.makeText(this, "Saved: ${tab.fileName}", Toast.LENGTH_SHORT).show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("path", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showFileTree() {
        bottomPanelViewpager.currentItem = 0
    }

    private fun showSearchPanel() {
        bottomPanelViewpager.currentItem = BottomPanelPagerAdapter.PANEL_SEARCH
    }

    private fun showLogsPanel() {
        bottomPanelViewpager.currentItem = BottomPanelPagerAdapter.PANEL_LOGS
    }

    private fun showSettings() {
        Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showSearchPanel()
                true
            }
            R.id.action_save -> {
                activeTab?.let { saveTab(it) }
                true
            }
            R.id.action_undo -> {
                Toast.makeText(this, "Undo coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_redo -> {
                Toast.makeText(this, "Redo coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}
