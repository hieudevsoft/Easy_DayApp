package com.example.easyday.ENTITY;

public class MusicsFile {
    private String album;
    private String title;
    private String duration;
    private String data;
    private String artist;

    public MusicsFile(String album, String title, String duration, String data, String artist) {
        this.album = album;
        this.title = title;
        this.duration = duration;
        this.data = data;
        this.artist = artist;
    }

    public MusicsFile() {
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
