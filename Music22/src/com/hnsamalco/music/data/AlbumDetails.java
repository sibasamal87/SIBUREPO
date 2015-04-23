package com.hnsamalco.music.data;

import java.io.Serializable;
import java.util.ArrayList;

public class AlbumDetails implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String albumName;
	private String albumArtist;
	private String coverImagePath;
	private int noOfSong;
	private ArrayList<SongDetails> songs;
	private String firstYear;
	private String lastYear;
	
	public ArrayList<SongDetails> getSongs() {
		return songs;
	}
	public void setSongs(ArrayList<SongDetails> songs) {
		this.songs = songs;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getAlbumArtist() {
		return albumArtist;
	}
	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
	public String getCoverImagePath() {
		return coverImagePath;
	}
	public void setCoverImagePath(String coverImagePath) {
		this.coverImagePath = coverImagePath;
	}
	public int getNoOfSong() {
		return noOfSong;
	}
	public void setNoOfSong(int noOfSong) {
		this.noOfSong = noOfSong;
	}
	public String getFirstYear() {
		return firstYear;
	}
	public void setFirstYear(String firstYear) {
		this.firstYear = firstYear;
	}
	public String getLastYear() {
		return lastYear;
	}
	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}
	
}
