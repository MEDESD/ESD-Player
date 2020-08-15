package com.medesd.esdplayer.Files.component;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.medesd.esdplayer.Files.activity.FilesActivity;
import com.medesd.esdplayer.R;

public class VideoThumbleLoader {

    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<>());
    private Map<String, Bitmap> videoBitmap=Collections.synchronizedMap(new WeakHashMap<>());
    
    
    public VideoThumbleLoader(){
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
    }
    
    
    private final int stub_id = R.drawable.nophotos;
    public void DisplayImage(String url, ImageView imageView, ProgressBar progressBar)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=videoBitmap.get(url);
        
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			if (progressBar != null)
				progressBar.setVisibility(View.INVISIBLE);
		} else {
			queuePhoto(url, imageView, progressBar);
			imageView.setImageResource(stub_id);
		}
    }
        
    private void queuePhoto(String url, ImageView imageView, ProgressBar progressBar)
    {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
        photosQueue.Clean(imageView);
        PhotoToLoad p=new PhotoToLoad(url, imageView, progressBar);
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }
        
        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW)
            photoLoaderThread.start();
    }
    
    private Bitmap getBitmap(String url) 
    {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
        return MediaStore.Video.Thumbnails.getThumbnail(
                FilesActivity.context.getContentResolver(),
                Long.parseLong(url), MediaStore.Video.Thumbnails.MINI_KIND, options);
    }

    //Task for the queue
    private class PhotoToLoad
    {
        String url;
        ImageView imageView;
        ProgressBar progressBar;
        
        PhotoToLoad(String u, ImageView i, ProgressBar p){
            url=u; 
            imageView=i;
            progressBar=p;
        }
    }
    
    private PhotosQueue photosQueue=new PhotosQueue();

    //stores list of photos to download
    class PhotosQueue
    {
        private final Stack<PhotoToLoad> photosToLoad= new Stack<>();
        
        //removes all instances of this ImageView
        void Clean(ImageView image)
        {
            for(int j=0 ;j<photosToLoad.size();){
                if(photosToLoad.get(j).imageView==image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }
    
    class PhotosLoader extends Thread {
        public void run() {
            try {
                do {
                    //thread waits until there are any images to load in the queue
                    if (photosQueue.photosToLoad.size() == 0)
                        synchronized (photosQueue.photosToLoad) {
                            photosQueue.photosToLoad.wait();
                        }
                    if (photosQueue.photosToLoad.size() != 0) {
                        PhotoToLoad photoToLoad;
                        synchronized (photosQueue.photosToLoad) {
                            photoToLoad = photosQueue.photosToLoad.pop();
                        }
                        Bitmap bmp = getBitmap(photoToLoad.url);
                        videoBitmap.put(photoToLoad.url, bmp);
                        String tag = imageViews.get(photoToLoad.imageView);
                        if (tag != null && tag.equals(photoToLoad.url)) {
                            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView, photoToLoad.progressBar);
                            AppCompatActivity a=(AppCompatActivity) photoToLoad.imageView.getContext();
                            a.runOnUiThread(bd);
                        }
                    }
                } while (!Thread.interrupted());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private PhotosLoader photoLoaderThread = new PhotosLoader();
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView imageView;
        ProgressBar progressBar;
        
        BitmapDisplayer(Bitmap b, ImageView i, ProgressBar p){bitmap=b;imageView=i;progressBar=p;}
        public void run()
        {
            if(bitmap!=null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(stub_id);
            
            if (progressBar!=null) progressBar.setVisibility(View.INVISIBLE);
            
        }
    }

}
