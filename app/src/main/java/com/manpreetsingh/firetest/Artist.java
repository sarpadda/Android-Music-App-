package com.manpreetsingh.firetest;

/**
 * Created by msgil on 8/6/2017.
 */

public class Artist {

    private String artistId;
    private String artistName;
    private String artistGenre;

    public Artist(){

    }

    public Artist(String artistId, String artistName, String artistGenre) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistGenre = artistGenre;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistGenre() {
        return artistGenre;
    }
}
