package me.iwf.photopicker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.llflib.cm.util.Views;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.fragment.PhotoPickerFragment;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity {

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static int DEFAULT_MAX_COUNT = 9;
    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;
    private MenuItem menuDoneItem;
    private int maxCount = DEFAULT_MAX_COUNT;
    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;
    private boolean showGif = false;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        boolean showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        setShowGif(showGif);
        setContentView(R.layout.activity_photo_picker);
        Views.setApplyWindowInserts(this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.img_images);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPickerFragment);
        pickerFragment.getPhotoGridAdapter().setHasCamera(showCamera);
        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean onItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {
                int total = selectedItemCount+(isCheck?1:-1);
                menuDoneItem.setEnabled(total > 0);
                pickerFragment.setPreviewCount(total);
                if (total > maxCount) {
                    Toast.makeText(getActivity(), getString(R.string.over_max_count_tips, maxCount), LENGTH_LONG)
                            .show();
                    return false;
                }
                menuDoneItem.setTitle(getString(R.string.done_with_count, total, maxCount));
                return true;
            }
        });

    }


    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            super.onBackPressed();
        }
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, this.imagePagerFragment).addToBackStack(
                null).commit();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.menu_picker, menu);
            menuDoneItem = menu.findItem(R.id.done);
            menuDoneItem.setEnabled(false);
            menuIsInflated = true;
            return true;
        }
        return false;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.done) {
            Intent intent = new Intent();
            int count = pickerFragment.getPhotoGridAdapter().getSelectedCount();
            List<Photo> photos = pickerFragment.getPhotoGridAdapter().getPhotos();
            ArrayList<String> selectedPhotos = new ArrayList<>(count);
            for(Photo p:photos){
                if(p.checked)
                    selectedPhotos.add(p.getPath());
            }
            intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

}
