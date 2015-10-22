package me.iwf.photopicker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.R;
import me.iwf.photopicker.adapter.PhotoGridAdapter;
import me.iwf.photopicker.adapter.PopupDirectoryListAdapter;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.OnPhotoClickListener;
import me.iwf.photopicker.utils.ImageCaptureManager;
import me.iwf.photopicker.utils.MediaStoreHelper;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static me.iwf.photopicker.PhotoPickerActivity.EXTRA_SHOW_GIF;
import static me.iwf.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoPickerFragment extends Fragment {
    private ImageCaptureManager captureManager;
    private PhotoGridAdapter photoGridAdapter;
    private PopupDirectoryListAdapter listAdapter;
    private List<PhotoDirectory> directories;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        directories = new ArrayList<>();
        photoGridAdapter = new PhotoGridAdapter(directories);
        listAdapter = new PopupDirectoryListAdapter(getActivity(), directories);
        captureManager = new ImageCaptureManager(getActivity());
        Bundle mediaStoreArgs = new Bundle();
        if (getActivity() instanceof PhotoPickerActivity) {
            mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, ((PhotoPickerActivity) getActivity()).isShowGif());
        }
        MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs, new MediaStoreHelper.PhotosResultCallback() {
            @Override public void onResultCallback(List<PhotoDirectory> dirs) {
                directories.clear();
                directories.addAll(dirs);
                photoGridAdapter.refreshDirectory(directories);
                photoGridAdapter.notifyDataSetChanged();
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        final View rootView = inflater.inflate(R.layout.fragment_photo_picker, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final Button btSwitchDirectory = (Button) rootView.findViewById(R.id.button);
        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(btSwitchDirectory);
        listPopupWindow.setAdapter(listAdapter);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        listPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                PhotoDirectory directory = directories.get(position);
                btSwitchDirectory.setText(directory.getName());
                photoGridAdapter.setCurrentDirectory(position);
                photoGridAdapter.notifyDataSetChanged();
            }
        });
        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override public void onClick(View v, int position, boolean showCamera) {
                List<Photo> photos = photoGridAdapter.getPhotos();
                int index = showCamera ? position - 1 : position;
                int size = showCamera ? photos.size() - 1 : photos.size();
                ArrayList<String> list = new ArrayList<>();
                int[] idxs = new int[size];
                for (Photo p : photos) {
                    if (p.type == PhotoGridAdapter.ITEM_TYPE_CAMERA)
                        continue;
                    list.add(p.getPath());
                    idxs[list.indexOf(p.getPath())] = p.checked ? 1 : 0;
                }
                ImagePagerFragment imagePagerFragment = ImagePagerFragment.newInstance(list, index, true, idxs);
                ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
            }
        });

        photoGridAdapter.setOnCameraClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                try {
                    Intent intent = captureManager.dispatchTakePictureIntent();
                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btSwitchDirectory.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else if (!getActivity().isFinishing()) {
                    listPopupWindow.setHeight(Math.round(rootView.getHeight() * 0.8f));
                    listPopupWindow.show();
                }
            }
        });
        rootView.findViewById(R.id.preview).setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                List<Photo> photos = photoGridAdapter.getPhotos();
                ArrayList<Photo> selectes = new ArrayList<>();
                for (Photo p : photos) {
                    if (p.type == PhotoGridAdapter.ITEM_TYPE_CAMERA)
                        continue;
                    if (!p.checked)
                        continue;
                    selectes.add(p);
                }
                int size = selectes.size();
                ArrayList<String> list = new ArrayList<String>();
                int[] idxs = new int[size];
                for (Photo p : selectes) {
                    list.add(p.getPath());
                    idxs[list.indexOf(p.getPath())] = 1;
                }
                ImagePagerFragment imagePagerFragment = ImagePagerFragment.newInstance(list, 0, true, idxs);
                ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
            }
        });
        //restore toolbar
        View tool = getActivity().findViewById(R.id.toolbar);
        if (tool != null)
            tool.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            captureManager.galleryAddPic();
            if (directories.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                photoGridAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setPreviewCount(int count) {
        View root = getView();
        if (root == null)
            return;
        TextView tv = (TextView) root.findViewById(R.id.preview);
        String txt = getString(R.string.img_preview);
        if(count > 0) txt += String.format("(%d)",count);
        tv.setEnabled(count > 0);
        tv.setText(txt);
    }

    public PhotoGridAdapter getPhotoGridAdapter() {
        return photoGridAdapter;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
        Timber.i("PickerFragment onStart");
    }

    @Override public void onResume() {
        super.onResume();
        Timber.i("PickerFragment onResume");
    }
}
