package com.medesd.esdplayer.Files.component;

import java.util.ArrayList;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;

import com.medesd.esdplayer.Files.activity.FilesActivity;

public class PhoneMediaVideoController {

	public static void loadAllVideoMedia() {

		new Thread(() -> {
			Cursor cursor=null;
			final ArrayList<VideoDetails> arrVideoDetails = new ArrayList<>();
			try {
				cursor=MediaStore.Video.query(FilesActivity.context.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projectionPhotos);
				  if (cursor != null) {
						  int videoIdColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
						  int bucketIdColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
						  int bucketNameColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
						  int dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
						  int dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
						  int resolutionColumn = cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION);
						  int sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
						  int displaynameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
						  int durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);



						if (cursor.getCount() >=1 ) {

							 while (cursor.moveToNext()) {
								 int imageId = cursor.getInt(videoIdColumn);
								 int bucketId = cursor.getInt(bucketIdColumn);
								 String bucketName = cursor.getString(bucketNameColumn);
								 String path = cursor.getString(dataColumn);
								 long dateTaken = cursor.getLong(dateColumn);
								 String resolution = cursor.getString(resolutionColumn);
								 String size = cursor.getString(sizeColumn);
								 String displayname = cursor.getString(displaynameColumn);
								 String duration = cursor.getString(durationColumn);
							  VideoDetails mVideoDetails=new VideoDetails(imageId, bucketId, bucketName, path, dateTaken, resolution, size, displayname, duration,null);
								arrVideoDetails.add(mVideoDetails);
							 }
						}
				  }

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
if (cursor != null) {
try {
						cursor.close();
} catch (Exception e) {
Log.e("tmessages", e.toString());
}
}
}

			runOnUIThread(() -> {
				if (loadallvideomediainterface!=null) {
					loadallvideomediainterface.loadVideo(arrVideoDetails);
				}
			});
		}).start();
	}
	
	
	private static final String[] projectionPhotos = {
			MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID,
			MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
			MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_TAKEN,
			MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Media.SIZE,
			MediaStore.Video.Media.DISPLAY_NAME,
			MediaStore.Video.Media.DURATION, };
	
	public static class VideoDetails {
		public int imageId;
		int bucketId;
		String bucketName;
		public String path;
		long dateTaken;
		String resolution;
		String size;
		public String displayname;
		String duration;
		Bitmap curThumb;
		
		VideoDetails(int imageId, int bucketId, String bucketName,
					 String path, long dateTaken, String resolution, String size,
					 String displayname, String duration, Bitmap curThumb) {
			this.imageId = imageId;
			this.bucketId = bucketId;
			this.bucketName = bucketName;
			this.path = path;
			this.dateTaken = dateTaken;
			this.resolution = resolution;
			this.size = size;
			this.displayname = displayname;
			this.duration = duration;
			this.curThumb=curThumb;
		}
	}
	

	private static void runOnUIThread(Runnable runnable) {
		runOnUIThread(runnable, 0);
	}

	private static void runOnUIThread(Runnable runnable, long delay) {
		if (delay == 0) {
			FilesActivity.handler.post(runnable);
		} else {
			FilesActivity.handler.postDelayed(runnable,delay);
		}
	}
	
	private static loadAllVideoMediaInterface loadallvideomediainterface;


	public static void setLoadallvideomediainterface(
			loadAllVideoMediaInterface loadallvideomediainterface) {
		PhoneMediaVideoController.loadallvideomediainterface = loadallvideomediainterface;
	}


	public interface loadAllVideoMediaInterface {
		void loadVideo(ArrayList<VideoDetails> arrVideoDetails);
	}
}
