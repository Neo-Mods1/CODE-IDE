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



package com.neo.ide.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.neo.ide.fragments.DiagnosticsListFragment;
import com.neo.ide.fragments.SearchResultFragment;
import com.neo.ide.fragments.output.AppLogFragment;
import com.neo.ide.fragments.output.BuildOutputFragment;
import com.neo.ide.fragments.output.IDELogFragment;
import com.neo.ide.resources.R;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorBottomSheetTabAdapter extends FragmentStateAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(EditorBottomSheetTabAdapter.class);
  private final List<Tab> fragments;

  public EditorBottomSheetTabAdapter(@NonNull FragmentActivity fragmentActivity) {
    super(fragmentActivity);

    var index = -1;
    this.fragments = new ArrayList<>();
    this.fragments.add(
        new Tab(
            fragmentActivity.getString(R.string.build_output),
            BuildOutputFragment.class,
            ++index));
    this.fragments.add(
        new Tab(fragmentActivity.getString(R.string.app_logs), AppLogFragment.class, ++index));
    this.fragments.add(
        new Tab(fragmentActivity.getString(R.string.ide_logs), IDELogFragment.class, ++index));
    this.fragments.add(
        new Tab(
            fragmentActivity.getString(R.string.view_diags),
            DiagnosticsListFragment.class,
            ++index));
    this.fragments.add(
        new Tab(
            fragmentActivity.getString(R.string.view_search_results),
            SearchResultFragment.class,
            ++index));
  }

  public Fragment getFragmentAtIndex(int index) {
    return getFragmentById(getItemId(index));
  }

  @Nullable
  public Fragment getFragmentById(long itemId) {
    final var fragments = getFragments();
    if (fragments != null) {
      return fragments.get(itemId);
    }

    return null;
  }

  @Nullable
  private LongSparseArray<Fragment> getFragments() {
    try {
      final var field = FragmentStateAdapter.class.getDeclaredField("mFragments");
      field.setAccessible(true);
      return (LongSparseArray<Fragment>) field.get(this);
    } catch (Throwable th) {
      LOG.error("Unable to reflect fragment list from adapter.");
    }

    return null;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    try {
      final var tab = fragments.get(position);
      final var klass = Class.forName(tab.name).asSubclass(Fragment.class);
      final var constructor = klass.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Throwable th) {
      throw new RuntimeException("Unable to create fragment", th);
    }
  }

  @Override
  public int getItemCount() {
    return fragments.size();
  }

  public String getTitle(int position) {
    return fragments.get(position).title;
  }

  @Nullable
  public BuildOutputFragment getBuildOutputFragment() {
    return findFragmentByClass(BuildOutputFragment.class);
  }

  @Nullable
  private <T extends Fragment> T findFragmentByClass(Class<T> clazz) {
    final var name = clazz.getName();
    for (final var tab : this.fragments) {
      if (tab.name.equals(name)) {
        return (T) getFragmentById(tab.itemId);
      }
    }

    return null;
  }

  @Nullable
  public AppLogFragment getLogFragment() {
    return findFragmentByClass(AppLogFragment.class);
  }

  @Nullable
  public DiagnosticsListFragment getDiagnosticsFragment() {
    return findFragmentByClass(DiagnosticsListFragment.class);
  }

  @Nullable
  public SearchResultFragment getSearchResultFragment() {
    return findFragmentByClass(SearchResultFragment.class);
  }

  public <T extends Fragment> int findIndexOfFragmentByClass(@NonNull Class<T> tClass) {
    final var name = tClass.getName();
    for (int i = 0; i < this.fragments.size(); i++) {
      final var tab = this.fragments.get(i);
      if (tab.name.equals(name)) {
        return i;
      }
    }

    return -1;
  }

  static class Tab {

    final String title;
    final String name;
    final long itemId;

    public Tab(String title, @NonNull Class<? extends Fragment> fragment, long id) {
      this.title = title;
      this.name = fragment.getName();
      this.itemId = id;
    }
  }
}
