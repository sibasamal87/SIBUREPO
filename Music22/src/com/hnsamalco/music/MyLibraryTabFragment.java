package com.hnsamalco.music;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.hnsamalco.music.adapter.AlbumListAdapter;
import com.hnsamalco.music.adapter.MYLIbraryArtistGridAdapter;
import com.hnsamalco.music.adapter.MYLIbraryGenrGridAdapter;
import com.hnsamalco.music.adapter.MYLIbrarySongsListAdapter;
import com.hnsamalco.music.provider.MusicProvider;

public class MyLibraryTabFragment extends Fragment {
	
	private static final String ARG_POSITION = "position";
	private int position;
	private LayoutInflater inflater;
	private ViewGroup container;
	private MYLIbraryGenrGridAdapter adapterGenr;
	private MYLIbraryArtistGridAdapter adapterArtist;
	private AlbumListAdapter adapterAlbum;
	private MYLIbrarySongsListAdapter adapterSong;
	
	private GridView gridViewGenr,gridViewArtist,gridViewAlbums;
	private ListView songsListView;
	
	private static final int LOADGENR=101;
	private static final int LOADARTIST=102;
	private static final int LOADALBUM=103;
	private static final int LOADSONG=104;
	
	private MusicProvider provider;
	
	private Cursor songsCursor,genrsCursor,artistCursor,albumCursor;

	public static MyLibraryTabFragment newInstance(int position){
		
		MyLibraryTabFragment f = new MyLibraryTabFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		position = getArguments().getInt(ARG_POSITION);
		provider = new MusicProvider(getActivity());
		System.out.println("inside fragment tab");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container=container;
		
		View view = LoadViewPositionWise(position);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		if(position==0){
			
			Thread genThrd = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					genrsCursor = provider.getAllGeneres();
					Message msg = new Message();
					msg.what=LOADGENR;
					handler.sendMessage(msg);
				}
			};
			
			genThrd.start();
			
		}else if(position==1){
			
			
			Thread artistThrd = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					artistCursor = provider.getAllArtist();
					Message msg = new Message();
					msg.what=LOADARTIST;
					handler.sendMessage(msg);
				}
			};
			
			artistThrd.start();
			
			
		}else if(position==2){
			
			
			Thread albumThrd = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					albumCursor = provider.getAllAlbum();
					Message msg = new Message();
					msg.what=LOADALBUM;
					handler.sendMessage(msg);
				}
			};
			
			albumThrd.start();
			
		}else if(position==3){
			
			
			Thread songsThrd = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					songsCursor = provider.getAllSong();
					Message msg = new Message();
					msg.what=LOADSONG;
					handler.sendMessage(msg);
				}
			};
			
			songsThrd.start();
			
		}
	}
	
	private View LoadViewPositionWise(int position){
		
		View view = null;
		
		if(position==0){
			
			view = loadGenrs();
			
		}else if(position==1){
			
			view = loadArtist();
			
		}else if(position==2){
			
			view = loadAlbums();
			
		}else if(position==3){
			
			view = loadSongs();
		}
		
		return view;
		
	}
	
	Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			
			case LOADGENR:
				adapterGenr=new MYLIbraryGenrGridAdapter(getActivity(),genrsCursor, R.layout.grid_stack_view_item);
				gridViewGenr.setAdapter(adapterGenr);
				break;
			case LOADARTIST:
				adapterArtist = new MYLIbraryArtistGridAdapter(getActivity(),artistCursor, R.layout.grid_stack_view_item);
				gridViewArtist.setAdapter(adapterArtist);
				break;
			case LOADALBUM:
				adapterAlbum = new AlbumListAdapter(getActivity(),albumCursor, R.layout.mylibrary_album_grid_item);
				gridViewAlbums.setAdapter(adapterAlbum);
				break;
			case LOADSONG:
				adapterSong = new MYLIbrarySongsListAdapter(getActivity(),songsCursor, R.layout.my_library_songs_list_item);
				songsListView.setAdapter(adapterSong);
				break;
				
			
			}
		}
	};
	
	private View loadGenrs(){
		
		View view = inflater.inflate(R.layout.my_library_grid, container,false);
		gridViewGenr = (GridView)view.findViewById(R.id.gridList);
		
		return view;
	}
	
	private View loadArtist(){
		
		View view = inflater.inflate(R.layout.my_library_grid, container,false);
		gridViewArtist = (GridView)view.findViewById(R.id.gridList);
		
		return view;
	}
	
	private View loadAlbums(){
		
		View view = inflater.inflate(R.layout.my_library_album_grid, container,false);
		gridViewAlbums =(GridView) view.findViewById(R.id.gridList);
		
		
		return view;
	}
	
	private View loadSongs() {

		View view = inflater.inflate(R.layout.my_library_songs_list_view, container,
				false);
		songsListView = (ListView) view.findViewById(R.id.listSongs);

		return view;
	}	
	
}
