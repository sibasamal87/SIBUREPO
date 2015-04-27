package com.hnsamalco.music.provider;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Artists;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.Playlists;
import android.util.Log;

import com.hnsamalco.music.data.AlbumDetails;
import com.hnsamalco.music.data.ArtistDetails;
import com.hnsamalco.music.data.GenresDetails;
import com.hnsamalco.music.data.SongDetails;

public class MusicProvider {

	private Activity activity;

	public static String sAssetsArtImage = "no_album_art.png";

	public MusicProvider(Activity activity) {
		this.activity = activity;
	}

	@SuppressWarnings("unused")
	public Cursor getAllArtist() {

		ArrayList<ArtistDetails> artistList = new ArrayList<ArtistDetails>();
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = Artists.ARTIST + " ASC";
		Cursor cursor = activity.getContentResolver().query(
				Artists.EXTERNAL_CONTENT_URI, null, selection, selectionArgs,
				sortOrder);

		return cursor;
	}
	
	@SuppressWarnings("unused")
	public ArrayList<AlbumDetails> getAlbumsByArtistId(String artistId){
		
		ArrayList<AlbumDetails> listAlbums = new ArrayList<AlbumDetails>();
		Uri album_cover_image_uri = Artists.Albums.getContentUri(
				"external", Long.parseLong(artistId));
		String[] coverImageprojection = new String[] { Albums._ID,
				Albums.ALBUM, Albums.ALBUM_ART, Albums.ARTIST,
				Albums.NUMBER_OF_SONGS };
		
		Cursor cursorAlbum = activity.getContentResolver().query(
				album_cover_image_uri, coverImageprojection,
				null, null, null);
		
		if (cursorAlbum != null) {

			if (cursorAlbum.moveToFirst()) {
				do {

					AlbumDetails album = new AlbumDetails();

					String album_id = cursorAlbum
							.getString(cursorAlbum
									.getColumnIndex("_id"));
					String album_name = cursorAlbum
							.getString(cursorAlbum
									.getColumnIndex(Albums.ALBUM));
					String album_artist = cursorAlbum
							.getString(cursorAlbum
									.getColumnIndex(Albums.ARTIST));
					String album_cover_image = cursorAlbum
							.getString(cursorAlbum
									.getColumnIndex(Albums.ALBUM_ART));
					int album_no_of_songs = cursorAlbum
							.getInt(cursorAlbum
									.getColumnIndex(Albums.NUMBER_OF_SONGS));
					if (album_cover_image == null) {
						album_cover_image = sAssetsArtImage;
					}

					album.setId(Integer.parseInt(album_id));
					album.setAlbumArtist(album_artist);

					album.setAlbumName(album_name);
					album.setCoverImagePath(album_cover_image);
					album.setNoOfSong(album_no_of_songs);

					listAlbums.add(album);

				} while (cursorAlbum.moveToNext());
			}

		}

		cursorAlbum.close();
		
		return listAlbums;
	}

	@SuppressWarnings("unused")
	public Cursor getAllGeneres() {

		ArrayList<GenresDetails> listGenDetails = new ArrayList<GenresDetails>();
		ArrayList<AlbumDetails> listAlbum = null;
		String[] projection = new String[] { Genres._ID, Genres.NAME };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = Genres._ID + " ASC";
		Cursor cursor = activity.getContentResolver().query(
				Genres.EXTERNAL_CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);

		return cursor;
	}

	@SuppressWarnings("unused")
	public ArrayList<AlbumDetails> getAlbumsByGenreId(int generId) {

		ArrayList<AlbumDetails> ListAlbums = new ArrayList<AlbumDetails>();
		Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		Log.i("XXX", "uri = " + uri.toString());
		Cursor cursor = null;
		try {

			String selection = "album_info._id IN "
					+ "(SELECT (audio_meta.album_id) album_id FROM audio_meta, audio_genres_map "
					+ "WHERE audio_genres_map.audio_id=audio_meta._id AND audio_genres_map.genre_id=?)";
			String[] selectionArgs = new String[] { String
					.valueOf(generId + "") };
			String[] proj = { AlbumColumns.ALBUM_ID, AlbumColumns.ALBUM,
					AlbumColumns.ALBUM_ART, AlbumColumns.ARTIST,
					AlbumColumns.NUMBER_OF_SONGS };
			cursor = activity.getContentResolver().query(uri, null, selection,
					selectionArgs, null);
			if (null != cursor) {
				Log.i("XXX", "cursor rows count = " + cursor.getCount());
				while (cursor.moveToNext()) {
					AlbumDetails album = new AlbumDetails();
					Log.i("XXX", "  Album: " + cursor.getColumnNames());

					String album_id = cursor.getString(cursor
							.getColumnIndex("_id"));
					//
					String album_name = cursor.getString(cursor
							.getColumnIndex(AlbumColumns.ALBUM));
					//
					String album_artist = cursor.getString(cursor
							.getColumnIndex(AlbumColumns.ARTIST));
					//
					String album_cover_image = cursor.getString(cursor
							.getColumnIndex(AlbumColumns.ALBUM_ART));
					//
					int album_no_of_songs = cursor.getInt(cursor
							.getColumnIndex(AlbumColumns.NUMBER_OF_SONGS));

					if (album_cover_image == null) {
						album_cover_image = sAssetsArtImage;
					}
					//
					album.setId(Integer.parseInt(album_id));
					album.setAlbumArtist(album_artist);

					album.setAlbumName(album_name);
					album.setCoverImagePath(album_cover_image);
					album.setNoOfSong(album_no_of_songs);

					ListAlbums.add(album);
				}

				cursor.close();
				Log.i("XXX", "cursor closed");
			}
		} catch (Exception ex) {
			Log.e("XXX", "Error Querying Database");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return ListAlbums;
	}

	@SuppressWarnings("unused")
	public Cursor getAllAlbum() {

		String[] projection = new String[] { Albums._ID, Albums.ALBUM,
				Albums.ARTIST, Albums.ALBUM_ART, Albums.NUMBER_OF_SONGS };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = Media.ALBUM + " ASC";
		Cursor cursor = activity.getContentResolver().query(
				Albums.EXTERNAL_CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);

		return cursor;
	}

	@SuppressWarnings("unused")
	public ArrayList<SongDetails> getAllSongByAlbumId(int albumId) {

		ArrayList<SongDetails> songsAlbumList = new ArrayList<SongDetails>();

		String[] column = { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.MIME_TYPE,
				MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST };

		String where = android.provider.MediaStore.Audio.Media.ALBUM_ID + "=?";
		String album_id = "" + albumId;
		String whereVal[] = { album_id };

		String orderBy = android.provider.MediaStore.Audio.Media._ID;

		Cursor cursor = activity.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, column, where,
				whereVal, orderBy);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					SongDetails details = new SongDetails();

					details.setAlbumId(albumId);
					details.setArtImage(getArtImageByAlbumId(albumId + ""));
					details.setId(cursor.getInt(cursor
							.getColumnIndex(MediaStore.Audio.Media._ID)));
					details.setDisplayName(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
					details.setTitle(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE)));
					details.setDuration(Long.valueOf(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DURATION))));
					details.setPath(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA)));
					details.setArtist(cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST)));

					songsAlbumList.add(details);

				} while (cursor.moveToNext());
			}
			cursor.close();
		}

		return songsAlbumList;

	}

	@SuppressWarnings("unused")
	public Cursor getAllSong() {

        String[] column = { MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST };

        String orderBy = android.provider.MediaStore.Audio.Media._ID;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = activity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, column, selection,
                null, orderBy);
        return cursor;

	}

	public String getArtImageByAlbumId(String albumId) {

		String[] projection = new String[] { Albums.ALBUM_ART };
		String selection = Albums._ID + "=?";
		String artImage = null;
		String[] selectionArgs = { albumId };
		String sortOrder = Media.ALBUM + " ASC";
		Cursor cursor = activity.getContentResolver().query(
				Albums.EXTERNAL_CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					artImage = cursor.getString(cursor
							.getColumnIndex(Albums.ALBUM_ART));
					

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		System.out.println("artImage artImage::artImage::"+artImage);
		
		if (artImage == null) {
			artImage = sAssetsArtImage;
		}
		
		return artImage;
	}
	
	@SuppressWarnings("unused")
	public void getAllPlayList(){
		
		String playlistName=null;
		String[] projection = new String[] { Playlists.NAME };
		String selection = null;
		String[] selectionArgs = {  };
		String sortOrder =Playlists._ID + " ASC";
		Cursor cursor = activity.getContentResolver().query(
				Playlists.EXTERNAL_CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					playlistName = cursor.getString(cursor
							.getColumnIndex(Playlists.NAME));
					System.out.println("print the name inside::"+playlistName);

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
	}
}
