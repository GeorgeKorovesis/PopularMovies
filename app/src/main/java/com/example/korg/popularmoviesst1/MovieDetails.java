package com.example.korg.popularmoviesst1;

import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.korg.popularmoviesst1.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.korg.popularmoviesst1.FavMoviesContract.MovieEntry.COLUMN_MOVIE_ID;
import static com.example.korg.popularmoviesst1.FavMoviesContract.MovieEntry.COLUMN_PLOT;
import static com.example.korg.popularmoviesst1.FavMoviesContract.MovieEntry.COLUMN_POSTER_URL;
import static com.example.korg.popularmoviesst1.FavMoviesContract.MovieEntry.COLUMN_RELEASE_DATE;
import static com.example.korg.popularmoviesst1.FavMoviesContract.MovieEntry.COLUMN_TABLE_RATING;
import static com.example.korg.popularmoviesst1.FavMoviesContract.MovieEntry.COLUMN_TITLE;

public class MovieDetails extends AppCompatActivity {

    private final String movieTrailersURL = "https://api.themoviedb.org/3/movie/id/videos";
    private final String movieReviewsURL = "https://api.themoviedb.org/3/movie/id/reviews";
    ArrayList<String> trailerList;
    TrailerAdapter trailerAdapter;
    ReviewsAdapter reviewAdapter;

    ActivityMovieDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trailerList = new ArrayList<>();

        Movie movie = getIntent().getParcelableExtra(DataAdapter.PAR_KEY);

        final String title = movie.getTitle();
        final String posterUrl = movie.getPosterImage();
        final String plot = movie.getPlot();
        final String rating = movie.getRating();
        final String year = (movie.getReleaseDate()).substring(0, 4);
        final Integer id = movie.getMovieId();

        ApplicationInfo ai;
        String apiKey = "";
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("API_KEY");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        fetchMovieTrailers(id,apiKey);
        fetchMovieReviews(id,apiKey);

        binding.title.setText(title);
        binding.year.setText(year);
        binding.plot.setText(plot);
        binding.rating.setText(rating + "/10");
        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor mCursor = getContentResolver().query(
                        FavMoviesContract.MovieEntry.CONTENT_URI,   // The content URI of the words table
                        null,                        // The columns to return for each row
                        "movie_id=?",                    // Selection criteria
                        new String[]{id.toString()},    // Selection criteria
                        null);                       // The sort order for the returned rows

                if (mCursor.getCount() == 0) {

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TITLE, title);
                    values.put(COLUMN_PLOT, plot);
                    values.put(COLUMN_TABLE_RATING, rating);
                    values.put(COLUMN_RELEASE_DATE, year);
                    values.put(COLUMN_MOVIE_ID, id);
                    values.put(COLUMN_POSTER_URL, posterUrl);

                    getContentResolver().insert(FavMoviesContract.MovieEntry.CONTENT_URI, values);

                    binding.favorite.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.yellow_star, null));
                } else {
                    getContentResolver().delete(
                            FavMoviesContract.MovieEntry.CONTENT_URI,
                            "movie_id=?",
                            new String[]{"" + id});
                    binding.favorite.setBackground(getResources().getDrawable(R.mipmap.gray_star));

                }


            }
        });

        if (isFavorite(id.toString()))
            binding.favorite.setBackground(getResources().getDrawable(R.mipmap.yellow_star));
        else
            binding.favorite.setBackground(getResources().getDrawable(R.mipmap.gray_star));

        Picasso.with(MovieDetails.this).load(posterUrl).into(binding.moviePoster);
    }

    private Boolean isFavorite(String id) {
        Cursor mCursor = getContentResolver().query(
                FavMoviesContract.MovieEntry.CONTENT_URI,   // The content URI of the words table
                null,                                       // The columns to return for each row
                "movie_id=?",                               // Selection criteria
                new String[]{id},                           // Selection criteria
                null);                                      // The sort order for the returned rows

        return (mCursor.getCount() != 0);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchMovieTrailers(Integer id, String apiKey) {

        RequestQueue queue = Volley.newRequestQueue(this);

        String movieTrailersURLID = movieTrailersURL.replaceFirst("id", id.toString());

        Uri buildUri = Uri.parse(movieTrailersURLID)
                .buildUpon()
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("language", "en-US")
                .build();

        String urlStr = buildUri.toString();

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONObject json;
                        Log.d("Response", response.toString());
                        JSONArray results;

                        try {
                            results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                json = results.getJSONObject(i);
                                if (json.getString("type").equals("Trailer"))
                                    trailerList.add(json.getString("key"));
                            }
                            trailerAdapter = new TrailerAdapter(MovieDetails.this, trailerList);
                            trailerAdapter.notifyDataSetChanged();
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
                            binding.TrailerItems.setLayoutManager(layoutManager);
                            binding.TrailerItems.setAdapter(trailerAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "" + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-length", "0");

                return params;
            }
        };

// add it to the RequestQueue
        queue.add(getRequest);
    }

    private void fetchMovieReviews(Integer id, String apiKey) {

        RequestQueue queue = Volley.newRequestQueue(this);

        String movieTrailersURLID = movieReviewsURL.replaceFirst("id", id.toString());

        Uri buildUri = Uri.parse(movieTrailersURLID)
                .buildUpon()
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("page", "1")
                .build();

        String urlStr = buildUri.toString();

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONObject json;
                        Log.d("Response", response.toString());
                        JSONArray results;
                        ArrayList<String> authorsList = new ArrayList<>();
                        ArrayList<String> reviewList = new ArrayList<>();
                        try {
                            results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                json = results.getJSONObject(i);
                                    authorsList.add(json.getString("author"));
                                    reviewList.add(json.getString("content"));
                            }
                            reviewAdapter = new ReviewsAdapter(authorsList, reviewList);
                            reviewAdapter.notifyDataSetChanged();
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
                            binding.ReviewItems.setLayoutManager(layoutManager);
                            binding.ReviewItems.setAdapter(reviewAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "" + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-length", "0");

                return params;
            }
        };

// add it to the RequestQueue
        queue.add(getRequest);
    }
}
