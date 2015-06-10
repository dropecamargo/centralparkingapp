package com.koiti.centralparking.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;

import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.Toast;

import com.koiti.centralparking.R;
import com.koiti.centralparking.data.GalleryData;
import com.koiti.centralparking.models.Image;
import com.koiti.centralparking.utils.ConstantsUtils;
import com.koiti.centralparking.utils.ImageCache.ImageCacheParams;
import com.koiti.centralparking.utils.ImageFetcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GalleryDetailFragment extends Fragment {

    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private GridView mGridView;

    public String mCurrentPhotoPath;
    private GalleryData gallery_data;
    public ArrayList<Image> gallery_images;

    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        gallery_data = new GalleryData(getActivity().getApplicationContext());
        gallery_images = gallery_data.getImagesGallery();

        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        // Set memory cache to 25% of app memory
        cacheParams.setMemCacheSizePercent(0.25f);

        // Loading images into our ImageView asynchronously
        mImageFetcher = new ImageFetcher(getActivity());
        mImageFetcher.setLoadingImage(R.drawable.aeropuerto);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery_detail, null);
        mGridView = (GridView) view.findViewById(R.id.gallery_items);
        mGridView.setAdapter(mAdapter);
        //mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                mImageFetcher.setPauseWork(false);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                    if (numColumns > 0) {
                        int columnWidth = (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                        mAdapter.setNumColumns(numColumns);
                        mAdapter.setItemHeight(columnWidth);
                        mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_photo, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete_image).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_image:
                GalleryDetailFragment.this.actionImage();
                return true;
            case R.id.action_delete_image:
                GalleryDetailFragment.this.actionDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void actionImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = gallery_data.createImageFile();
                mCurrentPhotoPath = null;
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCurrentPhotoPath = photoFile.getAbsolutePath();

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, ConstantsUtils.REQUEST_TAKE_PHOTO);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), R.string.msg_camera_error_file, Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                // Error occurred while creating the File
                StringBuffer msg_error = new StringBuffer().append(getResources().getString(R.string.msg_camera_error_file)).append("[").append(e.getMessage()).append("]");
                Toast.makeText(getActivity().getApplicationContext(), msg_error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void actionDelete() {
        for (int i = 0; i < gallery_images.size(); i++) {
            Image image = gallery_images.get(i);
            if(image.getSelected()) {
//                File file = new File(image.getPath());
//                file.delete();
//
//                gallery_images.remove(i);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            switch(requestCode) {
                case ConstantsUtils.REQUEST_TAKE_PHOTO:
                    if(resultCode != Activity.RESULT_CANCELED) {
                        if (requestCode == ConstantsUtils.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                            GalleryDetailFragment.this.galleryAddPic();

                            File file = new File(mCurrentPhotoPath);
                            Image one_image = new Image(file.getName(), ConstantsUtils.GALLERY_NAME, file.getPath());
                            gallery_images.add(0,one_image);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                break;
            }
        } catch (NullPointerException e){
            Toast.makeText(getActivity(), getResources().getString(R.string.null_pointer_exception), Toast.LENGTH_LONG).show();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);

        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void displayOptionsSelected(){
        int count_selectd = 0;
        for (int i = 0; i < gallery_images.size(); i++) {
            Image image = gallery_images.get(i);
            if(image.getSelected()){
                count_selectd ++;
            }
        }
        if(count_selectd >0 ){
            menu.findItem(R.id.action_delete_image).setVisible(true);
        }else{
            menu.findItem(R.id.action_delete_image).setVisible(false);
        }
    }

    /**
     * The main adapter that backs the GridView.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;
        private int numColumns = 0;
        private int mItemHeight = 0;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater mInflater = LayoutInflater.from(mContext);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gallery_detail_row, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.gallery_image);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.image.setLayoutParams(mImageViewLayoutParams);
                holder.item_select = (CheckBox) convertView.findViewById(R.id.gallery_item_select);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            if(holder != null) {
                if (holder.image.getLayoutParams().height != mItemHeight) {
                    holder.image.setLayoutParams(mImageViewLayoutParams);
                }
                final Image current_image = getItem(position);
                holder.item_select.setChecked(current_image.getSelected());
                holder.item_select.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        current_image.setSelected(isChecked);
                        GalleryDetailFragment.this.displayOptionsSelected();
                    }
                });
                holder.image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + current_image.getPath()), "image/*");
                        startActivity(intent);
                    }
                });
                // Finally load the image asynchronously into the ImageView
                mImageFetcher.loadImage(current_image, holder.image);
            }
            return convertView;
        }

        public int getNumColumns() {
            return numColumns;
        }

        public void setNumColumns(int numColumns) {
            this.numColumns = numColumns;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(mItemHeight, mItemHeight);
            mImageFetcher.setImageSize(mItemHeight, mItemHeight);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (getNumColumns() == 0) {
                return 0;
            }
            return gallery_images.size();
        }

        @Override
        public Image getItem(int position) {
            return (Image) gallery_images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public static class ViewHolder {
        public CheckBox item_select;
        public ImageView image;
    }
}
