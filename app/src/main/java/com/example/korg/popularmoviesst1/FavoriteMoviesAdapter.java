package com.example.korg.popularmoviesst1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by korg on 6/9/2017.
 */

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.MoviesViewHolder> {

    private Context context;
    private Cursor cursor;

    public final static String PAR_KEY = "MovieParcelableKey";


    public FavoriteMoviesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {

        cursor.moveToPosition(position);

        int titleIndex = cursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_TITLE);
        int plotIndex = cursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_PLOT);
        int ratingIndex = cursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_TABLE_RATING);
        int releseIndex = cursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int movieIdIndex = cursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_MOVIE_ID);
        int posterUrlIndex = cursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_POSTER_URL);


        final String title = cursor.getString(titleIndex);
        final String plot = cursor.getString(plotIndex);
        final String rating = cursor.getString(ratingIndex);
        final String release = cursor.getString(releseIndex);
        final Integer movieId = cursor.getInt(movieIdIndex);
        final String posterUrl = cursor.getString(posterUrlIndex);

        Picasso.with(context).load(posterUrl).into(holder.movieImg);

        holder.movieImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setPlot(plot);
                movie.setRating(rating);
                movie.setReleaseDate(release);
                movie.setMovieId(movieId);
                movie.setPosterImageUrl(posterUrl);

                Intent intent = new Intent(context, MovieDetails.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(PAR_KEY, movie);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        private ImageView movieImg;


        public MoviesViewHolder(View itemView) {
            super(itemView);
            movieImg = (ImageView) itemView.findViewById(R.id.img_android);

        }
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (cursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
