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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore.Audio.Artists;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hnsamalco.music.data.AlbumDetails;
import com.hnsamalco.music.drawable.ScalingUtilities;
import com.hnsamalco.music.drawable.ScalingUtilities.ScalingLogic;
import com.hnsamalco.music.provider.MusicProvider;

public class MYLIbraryArtistGridAdapter extends CursorAdapter{

	private Context context;
	private int resource;
	private LayoutInflater inflater;
	private int height,width;
	private final int maxMemory;
	private final int cacheSize;
	private MusicProvider provider;
	private Bitmap mPlaceHolderBitmap;
	private LruCache<String, Bitmap> mMemoryCache;
	
	public MYLIbraryArtistGridAdapter(Context context, Cursor c, int resource) {
		super(context, c, resource);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.resource=resource;
		inflater = ((Activity)context).getLayoutInflater();
		
		height=dpToPx(65);
		this.width = dpToPx(130);
		
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
	    
	    provider = new MusicProvider((Activity)context);
	    try {
			mPlaceHolderBitmap =BitmapFactory.decodeStream(context
					.getAssets().open("spin.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHolder holder = (ViewHolder) view.getTag();
		int artist_id = cursor.getInt(cursor
				.getColumnIndex(Artists._ID));
		String artist_name = cursor.getString(cursor
				.getColumnIndex(Artists.ARTIST));
		
		holder.albumName.setText(artist_name);
		
		final Bitmap bitmap = getBitmapFromMemCache(String.valueOf(artist_id));
		
		if(bitmap!=null){
			holder.coverImage.setImageBitmap(bitmap);
		}else{
			loadBitmap(artist_id, holder.coverImage);
		}
		
	}
	
	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		// TODO Auto-generated method stub
			
		ViewHolder holder = new ViewHolder();
		View convertView = inflater.inflate(resource,parent,false);
		holder.coverImage = (ImageView)convertView.findViewById(com.hnsamalco.music.R.id.stackview);
		holder.albumName = (TextView)convertView.findViewById(com.hnsamalco.music.R.id.text);
								
		convertView.setTag(holder);
		return convertView;
	}
	
	public void loadBitmap(int resId, ImageView imageView) {
	    if (cancelPotentialWork(resId, imageView)) {
	    	ImageLoader task = new ImageLoader(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(resId);
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
	
	class ViewHolder{
		
		ImageView coverImage;
		TextView albumName;
		int Id;
	}
	
	class ImageLoader extends AsyncTask<Integer, Void, Bitmap>{

		private final WeakReference<ImageView> imageViewReference;
		private int data=0;
		
		public ImageLoader(ImageView imageView) {
			
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Bitmap doInBackground(Integer... id) {
			// TODO Auto-generated method stub
			data = id[0];
			Bitmap bitmap = getCoverImage(provider.getAlbumsByArtistId(id[0]+""));
			addBitmapToMemoryCache(String.valueOf(id[0]), bitmap);
			
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (isCancelled()) {
				result = null;
	        }

	        if (imageViewReference != null && result != null) {
	            final ImageView imageView = imageViewReference.get();
	            final ImageLoader bitmapWorkerTask =
	                    getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null) {
	                imageView.setImageBitmap(result);
	            }
	        }
			
		}
		
	}
	
	private Bitmap getCoverImage(ArrayList<AlbumDetails> albums) {

		Bitmap imageBitmap = null;

		ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
		int count = 1;
		try {
			System.out.println("path ::" + albums.size());

			for (AlbumDetails details : albums) {

				if (count >= 5) {
					break;
				}

				String path = details.getCoverImagePath();
				int widthSize = (int) width / 2;

				if (path != null) {

					File imgFile = new File(path);
					if (imgFile != null && imgFile.exists()) {
						imageBitmap = BitmapFactory.decodeFile(imgFile
								.getAbsolutePath());

						if (albums.size() != 1) {

							imageBitmap = ScalingUtilities.createScaledBitmap(
									imageBitmap, (int) widthSize, widthSize,
									ScalingLogic.CROP);
						}
						
						count++;
						bitmapList.add(imageBitmap);
						
					} 
					

				}
			}
			
			if(bitmapList.size()==0){
				imageBitmap = BitmapFactory.decodeStream(context
						.getAssets().open(MusicProvider.sAssetsArtImage));
				bitmapList.add(imageBitmap);
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		System.out.println("size of the list bitmap::" + bitmapList.size());
		return getCombineImage(bitmapList);
	}
	

	public Bitmap getCombineImage(ArrayList<Bitmap> listBitmap) {

		Bitmap big = null;

		big = Bitmap.createBitmap(width, (int) height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(big);
		PointF shadowDirection = new PointF(2f, 2f);
		float shadowRadius = 4f;

		Paint mShadow = new Paint();
		mShadow.setAntiAlias(true);
		mShadow.setShadowLayer(shadowRadius, shadowDirection.x,
				shadowDirection.y, Color.BLACK);

		if (listBitmap.size() > 1) {

			if (listBitmap.size() == 2) {

				int totalWidth = listBitmap.get(0).getWidth()
						+ listBitmap.get(1).getWidth();
				big = Bitmap.createBitmap(totalWidth, (int) totalWidth / 2,
						Bitmap.Config.ARGB_8888);
				canvas = new Canvas(big);
			}

			int left = 0;
			int lLeft = big.getWidth() - listBitmap.get(0).getWidth();

			for (Bitmap bitmap : listBitmap) {

				if (listBitmap.size() == 2) {
					canvas.drawBitmap(bitmap, left, 0, null);
					left = left + bitmap.getWidth();

				} else {

					canvas.drawRect(lLeft, 0, lLeft + bitmap.getWidth(),
							bitmap.getHeight(), mShadow);
					canvas.drawBitmap(bitmap, lLeft, 0, null);
					lLeft = lLeft
							- (bitmap.getWidth() / (listBitmap.size() - 1));
				}

			}

		} else {
			if (listBitmap.get(0).getWidth() / 2 > listBitmap.get(0)
					.getHeight()) {
				big = Bitmap.createBitmap(listBitmap.get(0), 0, 0, listBitmap
						.get(0).getWidth(), listBitmap.get(0).getHeight());
			} else {
				big = Bitmap.createBitmap(listBitmap.get(0), 0, 0, listBitmap
						.get(0).getWidth(), listBitmap.get(0).getWidth() / 2);
			}
		}

		return big;
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
