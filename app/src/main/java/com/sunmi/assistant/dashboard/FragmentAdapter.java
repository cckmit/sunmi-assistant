//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunmi.assistant.dashboard;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;

import sunmi.common.utils.log.LogCat;

public abstract class FragmentAdapter extends PagerAdapter {
    private static final String TAG = "FragmentStatePagerAdapt";
    private static final boolean DEBUG = false;
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private ArrayList<SavedState> mSavedState = new ArrayList();
    private ArrayList<Fragment> mFragments = new ArrayList();
    private Fragment mCurrentPrimaryItem = null;

    public FragmentAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public abstract Fragment getItem(int var1);

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        if (container.getId() == -1) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LogCat.d(Utils.TAG, "instantiateItem:" + position);
        Fragment fragment;
        if (this.mFragments.size() > position) {
            fragment = this.mFragments.get(position);
            if (fragment != null) {
                LogCat.d(Utils.TAG, "cache:" + fragment);
                return fragment;
            }
        }

        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        fragment = this.getItem(position);
        if (this.mSavedState.size() > position) {
            SavedState fss = this.mSavedState.get(position);
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

    protected void clearCache() {
        for (int i = 0, size = mFragments.size(); i < size; i++) {
            mFragments.set(i, null);
        }
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (this.mSavedState.size() > 0) {
            state = new Bundle();
            SavedState[] fss = new SavedState[this.mSavedState.size()];
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
                    this.mSavedState.add((SavedState) fss[i]);
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
