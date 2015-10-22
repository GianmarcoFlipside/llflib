package me.iwf.photopicker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.llflib.cm.util.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import me.iwf.photopicker.R;
import me.iwf.photopicker.adapter.PhotoPagerAdapter;
import timber.log.Timber;

/**
 * Created by donglua on 15/6/21.
 */
public class ImagePagerFragment extends Fragment {
    private int[] mSeletedIdxs;
    private ArrayList<String> paths;
    private ViewPager mViewPager;
    private CheckBox mOrigin, mChecked;
    private PhotoPagerAdapter mPagerAdapter;

    private int currentItem = 0;
    private boolean mBarIsVisible = true;
    private boolean mIsSelectedMode = false;

    public static ImagePagerFragment newInstance(ArrayList<String> paths, int currentItem) {
        return newInstance(paths, currentItem, false, null);
    }

    public static ImagePagerFragment newInstance(ArrayList<String> paths, int currentItem, boolean isSelectedMode,
            int[] idxs) {
        ImagePagerFragment f = new ImagePagerFragment();
        f.setPhotos(paths, currentItem, isSelectedMode, idxs);
        return f;
    }

    private void setPhotos(ArrayList<String> paths, int currentItem, boolean selected, int[] idxs) {
        this.paths = paths;
        this.currentItem = currentItem;
        this.mIsSelectedMode = selected;
        mSeletedIdxs = idxs;
        if (selected && (idxs == null || idxs.length != paths.size())) {
            mSeletedIdxs = new int[paths.size()];
        }
        if (mViewPager != null) {
            mViewPager.setCurrentItem(currentItem);
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new PhotoPagerAdapter(getActivity(), paths);
        mPagerAdapter.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                changeToolbarStatus();
                changeBottomBarStatus();
            }
        });
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_pager, container, false);
        mOrigin = (CheckBox) rootView.findViewById(R.id.checkbox_q);
        mChecked = (CheckBox) rootView.findViewById(R.id.checkbox_select);
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_photos);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOffscreenPageLimit(5);
        mOrigin.setOnCheckedChangeListener(mCheckClick);
        mChecked.setOnCheckedChangeListener(mCheckClick);
        setViewListenerIfNeed(rootView, mViewPager);
        return rootView;
    }

    private CompoundButton.OnCheckedChangeListener mCheckClick = new CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.checkbox_select) {
                int checkedValue = isChecked ? 1 : 0;
                int pos = mViewPager.getCurrentItem();
                if (mSeletedIdxs[pos] != checkedValue) {
                    mSeletedIdxs[pos] = checkedValue;
                    changeSelectedTitle();
                }
                Timber.i(Arrays.toString(mSeletedIdxs));
            } else
                resetImageSize();
        }
    };

    private void changeToolbarStatus() {
        mBarIsVisible = !mBarIsVisible;
        int visible = mBarIsVisible ? View.VISIBLE : View.GONE;
        View view = getActivity().findViewById(R.id.toolbar);
        view.setVisibility(visible);
    }

    private void changeBottomBarStatus() {
        if (!mIsSelectedMode)
            return;
        View root = getView();
        if (root == null)
            return;
        int visible = mBarIsVisible ? View.VISIBLE : View.GONE;
        View view = root.findViewById(R.id.bottom_bar);
        view.setVisibility(visible);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }


    private void setViewListenerIfNeed(View root, ViewPager pager) {
        root.findViewById(R.id.bottom_bar).setVisibility(mIsSelectedMode ? View.VISIBLE : View.GONE);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override public void onPageSelected(int position) {
                pageSelected(position);
            }

            @Override public void onPageScrollStateChanged(int state) {
            }
        });
        pageSelected(currentItem);
        if (!mIsSelectedMode)
            return;
        changeSelectedTitle();
        resetImageSize();
    }

    private void pageSelected(int pos) {
        if (mIsSelectedMode) {
            if (mSeletedIdxs.length > 0)
                mChecked.setChecked(mSeletedIdxs[pos] > 0);
        } else {
            setPageTitle(pos);
        }
    }

    private void setPageTitle(int pos) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(String.format("%d/%d", pos, paths.size()));
    }

    private void changeSelectedTitle() {
        int count = 0;
        for (int i : mSeletedIdxs) {
            if (i > 0)
                count++;
        }
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(String.format("%d/%d", count, paths.size()));
        resetImageSize();
    }

    private void resetImageSize() {
        if (!mIsSelectedMode)
            return;
        String txt = getString(R.string.img_origin);
        if (!mOrigin.isChecked()) {
            mOrigin.setText(txt);
            return;
        }
        if (mSeletedIdxs != null) {
            int size = 0;
            for (int i = 0; i < mSeletedIdxs.length; i++) {
                if (mSeletedIdxs[i] == 0)
                    continue;
                String path = paths.get(i);
                if (path == null || path.length() == 0)
                    continue;
                File file = new File(path);
                if (!file.exists())
                    continue;
                size += file.length();
            }
            if (size > 0)
                txt += String.format("(%s)", Files.getFormatSize(size));
        }
        mOrigin.setText(txt);
    }
}
