package com.hnsamalco.music;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.hnsamalco.music.adapter.AlbumListAdapter;
import com.hnsamalco.music.communication.SongsListner;
import com.hnsamalco.music.data.AlbumDetails;
import com.hnsamalco.music.data.SongDetails;
import com.hnsamalco.music.provider.MusicProvider;

public class MusicListFragment extends Fragment{
    String TAG="MusicListFragment";
    private View view;
    private GridView gridAlbum;
    private SongsListner songsListner;
    private MusicProvider provider;
    private Cursor albumsCursor;
    private static final int LOADALBUM=101;
    private AlbumDetails albumDetailUpadtae;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_list_fragment, container,false);
        gridAlbum =(GridView) view.findViewById(R.id.gridAlbum);
        gridAlbum.setOnItemClickListener(new GridAlbumItemClick());
        songsListner = (SongsListner)getActivity();
        provider = new MusicProvider(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Thread thrdAlbums = new Thread(){
            @Override
            public void run() {
                albumsCursor=provider.getAllAlbum();
                Message msg = new Message();
                msg.what=LOADALBUM;
                handler.sendMessage(msg);
           }
        };
        thrdAlbums.start();
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
             case LOADALBUM:
                gridAlbum.setAdapter(new AlbumListAdapter(getActivity(),albumsCursor,
                        R.layout.album_grid_item));
                getAllSongByAlbumId(0, getAlbum(0).getId());
                songsListner.onSongsSelected(albumDetailUpadtae, 0);
                break;
            }
       };

    };

    private AlbumDetails getAlbum(int position){
        albumDetailUpadtae = new AlbumDetails();
        System.out.println("albumsCursoralbumsCursoralbumsCursoralbumsCursoralbumsCursor::"+albumsCursor.getCount());
        if (albumsCursor != null) {
            if (albumsCursor.moveToPosition(position)) {
                    System.out.println("insideeeeee valueeee");
                    String album_id = albumsCursor.getString(albumsCursor
                          .getColumnIndex(Albums._ID));

                    String album_name = albumsCursor.getString(albumsCursor
                          .getColumnIndex(Albums.ALBUM));

                    String album_artist = albumsCursor.getString(albumsCursor
                          .getColumnIndex(Albums.ARTIST));

                    String album_cover_image = albumsCursor
                          .getString(albumsCursor
                                     .getColumnIndex(Albums.ALBUM_ART));
//                  int first_year = albumsCursor
//                          .getInt(albumsCursor
//                                   .getColumnIndex(Albums.FIRST_YEAR));
//
//                  int last_year = albumsCursor
//                          .getInt(albumsCursor
//                                    .getColumnIndex(Albums.LAST_YEAR));

                    int album_no_of_songs = albumsCursor.getInt(albumsCursor
                             .getColumnIndex(Albums.NUMBER_OF_SONGS));

                    if (album_cover_image == null) {
                        album_cover_image = MusicProvider.sAssetsArtImage;
                    }
                    albumDetailUpadtae.setId(Integer.parseInt(album_id));
                    albumDetailUpadtae.setAlbumArtist(album_artist);
                    albumDetailUpadtae.setAlbumName(album_name);
                    albumDetailUpadtae.setCoverImagePath(album_cover_image);
                    albumDetailUpadtae.setNoOfSong(album_no_of_songs);
//                  albumDetailUpadtae.setFirstYear(first_year+"");
//                  albumDetailUpadtae.setLastYear(last_year+"");
            }

        }

        albumsCursor.moveToFirst();
        return albumDetailUpadtae;
    }

    private class GridAlbumItemClick implements OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
            getAlbum(position);
            getAllSongByAlbumId(position,albumDetailUpadtae.getId());
            songsListner.onSongsSelected(albumDetailUpadtae, position);
            showDialog(albumDetailUpadtae);
        }
    }

    void showDialog(AlbumDetails albumDetailUpadtae) {

	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    Bundle albumBundle = new Bundle();
	    albumBundle.putSerializable("album", albumDetailUpadtae);

	    // Create and show the dialog.
	    SongsListDialogFragment newFragment = new SongsListDialogFragment();
	    newFragment.setArguments(albumBundle);
	    
	    newFragment.show(ft, "dialog");
    }

    private void getAllSongByAlbumId(int position,int albumId){

        ArrayList<SongDetails> songsAlbumList = new ArrayList<SongDetails>();

        String[] column = { MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.ARTIST };

        String where = android.provider.MediaStore.Audio.Media.ALBUM_ID + "=?";
        String album_id=""+albumId;
        String whereVal[] = { album_id };

        String orderBy = android.provider.MediaStore.Audio.Media._ID;

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                column, where, whereVal, orderBy);

        if (cursor.moveToFirst()) {
            do {
            	SongDetails details = new SongDetails();
            	
                Log.v(TAG,
                        cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DURATION)));

                details.setAlbumId(albumId);
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

//                details.setYear(cursor.getString(cursor
//                        .getColumnIndex(MediaStore.Audio.Media.YEAR)));

                songsAlbumList.add(details);
                
            } while (cursor.moveToNext());
        }

        albumDetailUpadtae.setSongs(songsAlbumList);

    }

}
