package com.example.korg.popularmoviesst1;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by korg on 3/9/2017.
 */

public class FavMoviesContract {


    public static final String AUTHORITY = "com.example.korg.popularmoviesst1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_TABLE_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_URL = "poster_url";

    }


}
