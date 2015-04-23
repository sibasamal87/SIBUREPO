package com.hnsamalco.music.adapter;
import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hnsamalco.music.R;
import com.hnsamalco.music.data.AlbumDetails;
import com.hnsamalco.music.data.SongDetails;
import com.hnsamalco.music.drawable.ScalingUtilities;
import com.hnsamalco.music.drawable.ScalingUtilities.ScalingLogic;


public class ViewPagerAdapter extends PagerAdapter
{
	private ArrayList<SongDetails> songsList;
	private Context context;
	private LayoutInflater inflater;
	private AlbumDetails albumDetails;
	
	public ViewPagerAdapter(Context context,AlbumDetails albumDetails) {
		// TODO Auto-generated constructor stub
		songsList = albumDetails.getSongs();
		this.context=context;
		this.albumDetails=albumDetails;
	}
    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param pager    The ViewPager that this view will eventually be attached to.
     *
     * @return A View corresponding to the data at the specified position.
     */

    /**
     * Determines whether a page View is associated with a specific key object as
     * returned by instantiateItem(ViewGroup, int).
     *
     * @param view   Page View to check for association with object
     * @param object Object to check for association with view
     *
     * @return true if view is associated with the key object object.
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     *
     * @return Returns an Object representing the new page. This does not need
     *         to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	
    	ViewHolder holder = new ViewHolder();
    	
    	inflater = ((Activity)context).getLayoutInflater();
    	View view = inflater.inflate(R.layout.music_player, container,false);
    	
    	holder.coverImage = (ImageView)view.findViewById(R.id.playlistCover);
    	ImageLoader imageLoader = new ImageLoader(holder);
		imageLoader.execute(albumDetails);
    	
		holder. songDisplayName = (TextView)view.findViewById(R.id.displayText);
		holder. songDisplayName.setText(songsList.get(position).getDisplayName());
		
		holder. artist = (TextView)view.findViewById(R.id.artistText);
		holder. artist.setText(songsList.get(position).getArtist());
		
		holder.bigcoverImage = (ImageView)view.findViewById(R.id.albumImage);
    	CoverImageLoader bigImageLoader = new CoverImageLoader(holder);
    	bigImageLoader.execute(albumDetails);
		
		view.setTag(holder);
    	
    	((ViewPager) container).addView(view, 0);

        return view;
    }
    
    class ViewHolder{
		
		ImageView coverImage;
		ImageView bigcoverImage;
		TextView songDisplayName;
		TextView artist;
	}
	
	class ImageLoader extends AsyncTask<AlbumDetails, Void, Bitmap>{

		private ViewHolder holder;
		
		public ImageLoader(ViewHolder holder) {
			
			this.holder=holder;
		}
		
		@Override
		protected Bitmap doInBackground(AlbumDetails... albumInst) {
			// TODO Auto-generated method stub
			return getCoverImage(albumInst[0].getCoverImagePath(),false);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			holder.coverImage.setImageBitmap(result);
		}
		
	}
	
	class CoverImageLoader extends AsyncTask<AlbumDetails, Void, Bitmap>{

		private ViewHolder holder;
		
		public CoverImageLoader(ViewHolder holder) {
			
			this.holder=holder;
		}
		
		@Override
		protected Bitmap doInBackground(AlbumDetails... albumInst) {
			// TODO Auto-generated method stub
			return getCoverImage(albumInst[0].getCoverImagePath(),true);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			holder.bigcoverImage.setImageBitmap(result);
		}
		
	}
	
	
	private Bitmap getCoverImage(String path,boolean isCover){
		
		System.out.println("path ::"+path);
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95, r.getDisplayMetrics());
		int value=(int)px;
		Bitmap imageBitmap=null;
		
		if(path!=null){
		
			File imgFile = new  File(path);
			if(imgFile!=null && imgFile.exists()){
				imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				if(!isCover)
				imageBitmap = ScalingUtilities.createScaledBitmap(imageBitmap, value, value, ScalingLogic.CROP);
			}else{
				imageBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.no_album_art);
				if(!isCover)
				imageBitmap = ScalingUtilities.createScaledBitmap(imageBitmap, value, value, ScalingLogic.CROP);
			}
			
		}else{
			
			imageBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.no_album_art);
			if(!isCover)
			imageBitmap = ScalingUtilities.createScaledBitmap(imageBitmap, value, value, ScalingLogic.CROP);
		}
		
		return imageBitmap;
	}
    
    /**
     * Remove a page for the given position.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param view      The same object that was returned by instantiateItem(View, int).
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        ((ViewPager) container).removeView((View) view);
    }
    
    @Override
    public int getCount() {
        return songsList.size();
    }
    
}