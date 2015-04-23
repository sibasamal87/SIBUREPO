package com.hnsamalco.music.communication;

import com.hnsamalco.music.data.AlbumDetails;

public interface SongsListner {
	
	public void onSongsSelected(AlbumDetails details,int position);

}
