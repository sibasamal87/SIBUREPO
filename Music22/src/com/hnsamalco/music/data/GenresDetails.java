package com.hnsamalco.music.data;

import java.util.ArrayList;

public class GenresDetails {
	
	private int id;
	private String name;
	private ArrayList<AlbumDetails> albums;
	private String genCoverImage;
	
	public String getGenCoverImage() {
		return genCoverImage;
	}
	public void setGenCoverImage(String genCoverImage) {
		this.genCoverImage = genCoverImage;
	}
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
	public ArrayList<AlbumDetails> getAlbums() {
		return albums;
	}
	public void setAlbums(ArrayList<AlbumDetails> albums) {
		this.albums = albums;
	}
	
}
