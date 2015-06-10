package com.koiti.centralparking.data;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;

import com.koiti.centralparking.R;
import com.koiti.centralparking.models.Image;
import com.koiti.centralparking.utils.BitmapManager;
import com.koiti.centralparking.utils.ConstantsUtils;

public class GalleryData {
	
	private static final String MAIN_DIR = "/" + ConstantsUtils.GALLERY_NAME + "/";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	private Context context;
	
	public GalleryData(Context context){
		this.context = context;
	}

	public ArrayList<Image> getImagesGallery() {
        ArrayList<Image> imagesArray = new ArrayList<Image>();
        Uri gallery_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if(gallery_uri != null){
            String where = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? ";
			String[] args = { ConstantsUtils.GALLERY_NAME };
			String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DISPLAY_NAME };
			Cursor cursor = context.getContentResolver().query(gallery_uri ,projection, where, args, null);

            if(cursor != null){
                if (cursor.moveToFirst()) {
			        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
			        int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
			        int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			        do {
			        	String name = cursor.getString(nameColumn);
			        	String album = cursor.getString(albumColumn);
			        	String path = cursor.getString(pathColumn);
			        	imagesArray.add(new Image(name, album, path));
			        } while (cursor.moveToNext());
			    }
			    cursor.close();
	        }
        }
		return imagesArray;
	}
	
	public Bitmap getThumbnail(Long id, String path){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapManager.rotatedBitmap(MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),id,Images.Thumbnails.MICRO_KIND,null),path);
		} catch (IOException e) { }
		return bitmap;
	}

    public void createGallery() {
        File gallery_dir = Environment.getExternalStoragePublicDirectory(ConstantsUtils.GALLERY_NAME);
        if (gallery_dir != null) {
            if(!gallery_dir.isDirectory()){
                gallery_dir.mkdirs();
            }
        }
    }

	public File createImageFile() throws IOException {
        // Valid gallery
        GalleryData.this.createGallery();

        // Create an image file name
        File storageDir = Environment.getExternalStoragePublicDirectory(ConstantsUtils.GALLERY_NAME);
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        File image = File.createTempFile(timeStamp, JPEG_FILE_SUFFIX, storageDir);
	    return image;
	}
	
	public static Boolean moveFile(File fileToMove, String pathDestination){
		File fileDestinationDir = new File(pathDestination);
		if(!fileDestinationDir.isDirectory()){	
			if (!fileDestinationDir.mkdirs()){ 
				return false;
			}
		}		
		String newPathFile = pathDestination + "/" + fileToMove.getName();
		Boolean success = fileToMove.renameTo(new File(newPathFile));
		return success;		
	}
	
	public static String[] getSecondaryStorageDirectories()
	{
	    // Final set of paths
	    final Set<String> rv = new HashSet<String>();
	    // All Secondary SD-CARDs (all exclude primary) separated by ":"
	    final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
	    
	    // Add all secondary storages
	    if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
	    {
	        String[] paths = rawSecondaryStoragesStr.split(":");
	        for(String s: paths) {
	            if(!s.contains("usb") && !s.contains("Usb") && !s.contains("USB")) {
	            	File dir = new File(s);
                    if(dir!=null && dir.canRead() && dir.isDirectory() && dir.listFiles().length>0) {
                    	rv.add(s);
                    }	            	
	            }
	        }  
	    }
	    return rv.toArray(new String[rv.size()]);
	}		
}