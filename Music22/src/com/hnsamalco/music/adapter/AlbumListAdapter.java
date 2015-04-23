package com.hnsamalco.music.adapter;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore.Audio.Albums;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hnsamalco.music.R;
import com.hnsamalco.music.data.AlbumDetails;
import com.hnsamalco.music.provider.MusicProvider;

public class AlbumListAdapter extends CursorAdapter {
	
	private Context context;
	private int resource;
	private LayoutInflater inflater;
	private LruCache<String, Bitmap> mMemoryCache;
	final int maxMemory;
	final int cacheSize;
	private Bitmap mPlaceHolderBitmap;
	
	public AlbumListAdapter(Context context, Cursor c, int resource) {
		super(context, c, resource);
		// TODO Auto-generated constructor stub
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
	}
	
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder holder = (ViewHolder) view.getTag();
		
		String album_id = cursor.getString(cursor
				.getColumnIndex(Albums._ID));

		String album_name = cursor.getString(cursor
				.getColumnIndex(Albums.ALBUM));

		String album_artist = cursor.getString(cursor
				.getColumnIndex(Albums.ARTIST));

		String album_cover_image = cursor.getString(cursor
				.getColumnIndex(Albums.ALBUM_ART));

		int album_no_of_songs = cursor.getInt(cursor
				.getColumnIndex(Albums.NUMBER_OF_SONGS));

		if (album_cover_image == null) {
			album_cover_image = MusicProvider.sAssetsArtImage;
		}

		AlbumDetails albumDetail = new AlbumDetails();
		albumDetail.setId(Integer.parseInt(album_id));
		albumDetail.setAlbumArtist(album_artist);
		albumDetail.setAlbumName(album_name);
		albumDetail.setCoverImagePath(album_cover_image);
		albumDetail.setNoOfSong(album_no_of_songs);
		
		Bitmap bitmap = getBitmapFromMemCache(String.valueOf(albumDetail.getId()));

		holder.albumName.setText(albumDetail.getAlbumName());
		
		if(bitmap!=null){
			holder.coverImage.setImageBitmap(bitmap);
		}else{
			
			System.out.println("insideeee bitmap nulllllll");
			loadBitmap(albumDetail, holder.coverImage);
		}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder = new ViewHolder();
		View convertView = inflater.inflate(resource,parent,false);
		holder.coverImage = (ImageView)convertView.findViewById(com.hnsamalco.music.R.id.albumCover);
		holder.albumName = (TextView)convertView.findViewById(com.hnsamalco.music.R.id.albumName);
								
		convertView.setTag(holder);
		
		return convertView;
	}
	
	public void loadBitmap(AlbumDetails album, ImageView imageView) {
	    if (cancelPotentialWork(album.getId(), imageView)) {
	    	ImageLoader task = new ImageLoader(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(album);
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
	
	static class ViewHolder{
		
		ImageView coverImage;
		TextView albumName;
	}
	
	class ImageLoader extends AsyncTask<AlbumDetails, Void, Bitmap>{
		
		private int data=0;
		
		private final WeakReference<ImageView> imageViewReference;
		
		public ImageLoader(ImageView imageView) {
			
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Bitmap doInBackground(AlbumDetails... albumInst) {
			// TODO Auto-generated method stub
			data=albumInst[0].getId();
			Bitmap bitmap = getCoverImage(albumInst[0].getCoverImagePath());
			addBitmapToMemoryCache(String.valueOf(albumInst[0].getId()), bitmap);
			
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
		
		System.out.println("path ::"+path);
		Bitmap imageBitmap=null;
		
		if(path!=null){
		
			File imgFile = new  File(path);
			if(imgFile!=null && imgFile.exists()){
				imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//				imageBitmap = Bitmap.createScaledBitmap(imageBitmap, value, value, false);
			}else{
				imageBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.no_album_art);
//				imageBitmap = Bitmap.createScaledBitmap(imageBitmap, value, value, false);
			}
			
		}else{
			
			imageBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.no_album_art);
//			imageBitmap = Bitmap.createScaledBitmap(imageBitmap, value, value, false);
		}
		
		return imageBitmap;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		
		System.out.println("key key key::"+key);
		
		
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}

}
