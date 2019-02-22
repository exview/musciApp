package com.leave.music.javabean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by leave on 2018/4/13.
 */

public class Song extends DataSupport implements Parcelable{

    private long _id;
    private String date;//path of File
    private String artist;//author name
    private String songName;
    private String album_id;//专辑id
    private long size;//file size
    private long duration;//song total time

    @Override
    public String toString() {
        return "Song{" +
                "id='" + _id + '\'' +
                ", date='" + date + '\'' +
                ", artist='" + artist + '\'' +
                ", songName='" + songName + '\'' +
                ", album_id='" + album_id + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(date);
        dest.writeString(artist);
        dest.writeString(songName);
        dest.writeString(album_id);
        dest.writeLong(size);
        dest.writeLong(duration);
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){

        @Override
        public Song createFromParcel(Parcel source) {
            Song song = new Song();
            song._id = source.readLong();
            song.date = source.readString();
            song.artist = source.readString();
            song.songName = source.readString();
            song.album_id = source.readString();
            song.size = source.readLong();
            song.duration = source.readLong();
            return song;
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
