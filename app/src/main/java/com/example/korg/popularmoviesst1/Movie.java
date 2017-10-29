package com.example.korg.popularmoviesst1;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by korg on 21/5/2017.
 */

public class Movie implements Parcelable {
    private String originalTitle;
    private String posterImage;
    private String plotSynopsis;
    private String userRating;
    private String releaseDate;
    private Integer movieId;
    private final String url = "http://image.tmdb.org/t/p/w185";

    public Movie() {

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            Movie movie = new Movie();
            movie.originalTitle = source.readString();
            movie.posterImage = source.readString();
            movie.plotSynopsis = source.readString();
            movie.userRating = source.readString();
            movie.releaseDate = source.readString();
            movie.movieId = source.readInt();
            return movie;
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setTitle(String title) {
        originalTitle = title;
    }

    public void setPosterImage(String image) {
        posterImage = url + image;
    }

    public void setPosterImageUrl(String image) {
        posterImage = image;
    }


    public void setPlot(String plot) {
        plotSynopsis = plot;
    }

    public void setRating(String rating) {
        userRating = rating;
    }

    public void setReleaseDate(String date) {
        releaseDate = date;
    }

    public void setMovieId(Integer id) {
        movieId = id;
    }

    public String getTitle() {
        return originalTitle;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public String getPlot() {
        return plotSynopsis;
    }

    public String getRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getMovieId() {
        return movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterImage);
        dest.writeString(plotSynopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeInt(movieId);
    }
}
