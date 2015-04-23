package com.hnsamalco.music.data;

import java.util.ArrayList;

public class ArtistDetails {
	
	private int id;
	private String name;
	private int noOfSongs;
	private ArrayList<AlbumDetails> albums;
	
	private String coverImage;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNoOfSongs() {
		return noOfSongs;
	}
	public void setNoOfSongs(int noOfSongs) {
		this.noOfSongs = noOfSongs;
	}
	
	public String getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}
	public ArrayList<AlbumDetails> getAlbums() {
		return albums;
	}
	public void setAlbums(ArrayList<AlbumDetails> albums) {
		this.albums = albums;
	}

}
