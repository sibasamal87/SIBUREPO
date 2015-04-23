package com.hnsamalco.music;

import java.net.URI;

import com.hnsamalco.music.adapter.ViewPagerAdapter;
import com.hnsamalco.music.animations.DepthPageTransformer;
import com.hnsamalco.music.customview.BottomBarDrawerLayout;
import com.hnsamalco.music.data.AlbumDetails;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SongsListDialogFragment extends DialogFragment {

	private View view;
	private Bundle albumBundel;
	private ImageView coverImage;
	private TextView name,artist,noOfSong;
	private AlbumDetails albumDetails;
	private ViewPager playPager;
	private  BottomBarDrawerLayout slideInLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		albumBundel = getArguments();
		view = inflater.inflate(R.layout.songs_list_dialog_fragment, container,false);
		coverImage = (ImageView)view.findViewById(R.id.imageDrag);
		albumDetails = (AlbumDetails)albumBundel.getSerializable("album");
		coverImage.setImageURI(Uri.parse(albumDetails.getCoverImagePath()));
		
		name = (TextView)view.findViewById(R.id.name);
		artist = (TextView)view.findViewById(R.id.artist);
		noOfSong = (TextView)view.findViewById(R.id.noofSongs);
		
		name.setText(albumDetails.getAlbumName());
		artist.setText(albumDetails.getAlbumArtist());
		noOfSong.setText(albumDetails.getNoOfSong()+" Songs");
		
		ViewPagerAdapter playAdapter = new ViewPagerAdapter(getActivity(),albumDetails);
		playPager = (ViewPager)view.findViewById(R.id.myViewPager);
		playPager.setPageTransformer(true, new DepthPageTransformer());
		playPager.setAdapter(playAdapter);
		
		slideInLayout = (BottomBarDrawerLayout) view.findViewById(R.id.drawerLayout);
		slideInLayout.setDrawerShadow(R.drawable.bottom_top_slide);
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setStyle(STYLE_NO_TITLE, getActivity().getApplicationInfo().theme);
	}
	
	@Override
	public void onSaveInstanceState(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(arg0);
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        
        return dialog;
    }                                                             

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
