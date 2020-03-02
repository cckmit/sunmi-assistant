package com.sunmi.assistant.dashboard;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author yinhui
 * @date 2020-02-28
 */
public class PageAdapter extends PagerAdapter {

    private static final String TAG = "FragmentStatePagerAdapt";
    private static final boolean DEBUG = false;
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private Fragment mCurrentPrimaryItem = null;

    private int perspective;
    private List<PageHost> data = new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public void setPages(List<PageHost> pages, int perspective) {
        if (pages == null || pages.isEmpty()) {
            return;
        }
        this.perspective = perspective;
        data = pages;
        notifyDataSetChanged();
    }

    public Fragment getItem(int i) {
        return data.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof PageContract.PageView) {
            PageContract.PageView page = (PageContract.PageView) object;
            return page.getPerspective() == this.perspective ? POSITION_UNCHANGED : POSITION_NONE;
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        if (container.getId() == -1) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment;
        if (this.mFragments.size() > position) {
            fragment = this.mFragments.get(position);
            if (fragment != null) {
                return fragment;
            }
        }

        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        fragment = this.getItem(position);
        if (this.mSavedState.size() > position) {
            Fragment.SavedState fss = this.mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }

        while (this.mFragments.size() <= position) {
            this.mFragments.add(null);
        }

        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        this.mFragments.set(position, fragment);
        this.mCurTransaction.add(container.getId(), fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        while (this.mSavedState.size() <= position) {
            this.mSavedState.add(null);
        }

        this.mSavedState.set(position, fragment.isAdded() ? this.mFragmentManager.saveFragmentInstanceState(fragment) : null);
        this.mFragments.set(position, null);
        this.mCurTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != this.mCurrentPrimaryItem) {
            if (this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }

            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            this.mCurrentPrimaryItem = fragment;
        }

    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if (this.mCurTransaction != null) {
            this.mCurTransaction.commitNowAllowingStateLoss();
            this.mCurTransaction = null;
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (this.mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[this.mSavedState.size()];
            this.mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }

        for (int i = 0; i < this.mFragments.size(); ++i) {
            Fragment f = this.mFragments.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }

                String key = "f" + i;
                this.mFragmentManager.putFragment(state, key, f);
            }
        }

        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            this.mSavedState.clear();
            this.mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; ++i) {
                    this.mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }

            Iterable<String> keys = bundle.keySet();
            Iterator var6 = keys.iterator();

            while (true) {
                while (true) {
                    String key;
                    do {
                        if (!var6.hasNext()) {
                            return;
                        }

                        key = (String) var6.next();
                    } while (!key.startsWith("f"));

                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = this.mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (this.mFragments.size() <= index) {
                            this.mFragments.add(null);
                        }

                        f.setMenuVisibility(false);
                        this.mFragments.set(index, f);
                    } else {
                        Log.w("FragmentStatePagerAdapt", "Bad fragment at key " + key);
                    }
                }
            }
        }
    }
}
