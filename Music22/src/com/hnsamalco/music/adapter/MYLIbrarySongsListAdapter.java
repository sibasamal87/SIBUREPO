package com.hnsamalco.music.adapter;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hnsamalco.music.data.SongDetails;
import com.hnsamalco.music.provider.MusicProvider;

public class MYLIbrarySongsListAdapter extends CursorAdapter {
	
	MusicProvider provider;
	private Context context;
	private int resource;
	private LayoutInflater inflater;
	private LruCache<String, Bitmap> mMemoryCache;
	final int maxMemory;
	final int cacheSize;
	private Bitmap mPlaceHolderBitmap;
	
	ArrayList<SongDetails> songsAlbumList = new ArrayList<SongDetails>();
	
	public MYLIbrarySongsListAdapter(Context context, Cursor c, int resource) {
		super(context, c, resource);
		provider= new MusicProvider(((Activity)context));
		this.context=context;
		this.resource=resource;
		inflater = ((Activity)context).getLayoutInflater();
		maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    // Use 1/8th of the available memory for this memory cache.
	    cacheSize = maxMemory / 8;
	    
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount();
	        }
	    };
	    
	    try {
			mPlaceHolderBitmap =BitmapFactory.decodeStream(context
					.getAssets().open("spin.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder holder = (ViewHolder)view.getTag();
		
		SongDetails details = new SongDetails();

		int albumId = cursor.getInt(cursor
				.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

		details.setAlbumId(albumId);
//		details.setArtImage(provider.getArtImageByAlbumId(albumId + ""));
		details.setId(cursor.getInt(cursor
				.getColumnIndex(MediaStore.Audio.Media._ID)));
		details.setDisplayName(cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
		details.setTitle(cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.TITLE)));
		details.setDuration(cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media.DURATION)));
		details.setPath(cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.DATA)));
		details.setArtist(cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
		
		holder.songsName.setText(details.getDisplayName());
		holder.songsArtist.setText(details.getArtist());
		
		Bitmap bitmap = getBitmapFromMemCache(String.valueOf(details.getId()));
		if(bitmap==null){
			loadBitmap(details, holder.artImage);
		}
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder  holder = new ViewHolder();
		View convertView = inflater.inflate(resource,parent,false);
		holder.artImage = (ImageView)convertView.findViewById(com.hnsamalco.music.R.id.imageArt);
		holder.songsArtist = (TextView)convertView.findViewById(com.hnsamalco.music.R.id.textSongArtist);
		holder.songsName = (TextView)convertView.findViewById(com.hnsamalco.music.R.id.textSongName);
		 
		convertView.setTag(holder);
		
		return convertView;
	}

	class ViewHolder{
		
		ImageView artImage;
		TextView songsArtist,songsName;
	}
	
	public void loadBitmap(SongDetails details, ImageView imageView) {
	    if (cancelPotentialWork(details.getId(), imageView)) {
	    	ImageLoader task = new ImageLoader(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(details);
	    }
	}
	
	static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<ImageLoader> bitmapWorkerTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap,
	    		ImageLoader bitmapWorkerTask) {
	        super(res, bitmap);
	        bitmapWorkerTaskReference =
	            new WeakReference<ImageLoader>(bitmapWorkerTask);
	    }

	    public ImageLoader getBitmapWorkerTask() {
	        return bitmapWorkerTaskReference.get();
	    }
	}

	private static ImageLoader getBitmapWorkerTask(ImageView imageView) {
	   if (imageView != null) {
	       final Drawable drawable = imageView.getDrawable();
	       if (drawable instanceof AsyncDrawable) {
	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
	           return asyncDrawable.getBitmapWorkerTask();
	       }
	    }
	    return null;
	}
	
	public static boolean cancelPotentialWork(int data, ImageView imageView) {
	    final ImageLoader bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final int bitmapData = bitmapWorkerTask.data;
	        // If bitmapData is not yet set or it differs from the new data
	        if (bitmapData == 0 || bitmapData != data) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	            // The same work is already in progress
	            return false;
	        }
	    }
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	
	class ImageLoader extends AsyncTask<SongDetails, Void, Bitmap>{

		private int data=0;
		
		private final WeakReference<ImageView> imageViewReference;
		
		public ImageLoader(ImageView imageView) {
			
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Bitmap doInBackground(SongDetails... songInst) {
			// TODO Auto-generated method stub
			data=songInst[0].getId();
			Bitmap bitmap = getBitmapFromMemCache(String.valueOf(songInst[0].getId()));
			if(bitmap==null){
				bitmap = getCoverImage(provider.getArtImageByAlbumId(songInst[0].getAlbumId() + ""));
				addBitmapToMemoryCache(String.valueOf(songInst[0].getId()), bitmap);
			}
			
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (imageViewReference != null && result != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(result);
				}
			}
		}
		
	}
	
	private Bitmap getCoverImage(String path){
		
		Bitmap imageBitmap=null;
		
		try {

			File imgFile = new File(path);
			if (imgFile != null && imgFile.exists()) {
				imageBitmap = BitmapFactory.decodeFile(imgFile
						.getAbsolutePath());
			} else {
				imageBitmap = BitmapFactory.decodeStream(context.getAssets()
						.open(path));
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return imageBitmap;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}

}