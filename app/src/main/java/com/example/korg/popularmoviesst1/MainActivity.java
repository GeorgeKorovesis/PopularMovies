package com.example.korg.popularmoviesst1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.korg.popularmoviesst1.databinding.ActivityMainBinding;
import com.example.korg.popularmoviesst1.databinding.ActivityMovieDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final String popularMoviesURL = "http://api.themoviedb.org/3/movie/popular";
    final String topRatedMoviesUrl = "http://api.themoviedb.org/3/movie/top_rated";
    final String menuItemSelection = "menuItemSelection";

    final int popularMovies = 0;
    final int ratedMovies = 1;
    final int favoriteMovies = 2;

    private String apiKey;
    ArrayList<Movie> movieList;
    DataAdapter adapter;
    FavoriteMoviesAdapter favAdapter;
    ActivityMainBinding binding;
    int choice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            choice = savedInstanceState.getInt(menuItemSelection, 0);
        System.out.println("Choice = " + choice);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportLoaderManager().initLoader(0, null, this);
        initViews();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(menuItemSelection, choice);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {

        int cols;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            cols = 4;
        else
            cols = 2;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), cols);
        binding.posterRecyclerView.setLayoutManager(layoutManager);


        ApplicationInfo ai;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("API_KEY");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(binding.linLayout, R.string.Invalid_Api_Key, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }


        switch (choice) {
            case popularMovies:
                if (isOnline()) {
                    Uri builtUri = Uri.parse(popularMoviesURL)
                            .buildUpon()
                            .appendQueryParameter("api_key", apiKey)
                            .build();
                    String myUrl = builtUri.toString();
                    makeRequest(myUrl);

                    binding.progressBar1.setVisibility(View.VISIBLE);
                } else {
                    Snackbar snackbar = Snackbar.make(binding.linLayout, R.string.connection_Not_Available, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    binding.progressBar1.setVisibility(View.GONE);
                }
                break;
            case ratedMovies:
                if (isOnline()) {
                    Uri builtUri = Uri.parse(topRatedMoviesUrl)
                            .buildUpon()
                            .appendQueryParameter("api_key", apiKey)
                            .build();
                    String myUrl = builtUri.toString();
                    makeRequest(myUrl);

                    binding.progressBar1.setVisibility(View.VISIBLE);
                } else {
                    Snackbar snackbar = Snackbar.make(binding.linLayout, R.string.connection_Not_Available, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    binding.progressBar1.setVisibility(View.GONE);
                }
                break;
            case favoriteMovies:
                getSupportLoaderManager().restartLoader(0, null, this);
                binding.progressBar1.setVisibility(View.GONE);
                break;
        }
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        MenuItem selected = menu.getItem(choice);
        selected.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortbypopularity:
                choice = popularMovies;
                if (isOnline()) {
                    adapter = new DataAdapter(this, new ArrayList<Movie>());
                    binding.posterRecyclerView.setAdapter(adapter);

                    Uri builtUri = Uri.parse(popularMoviesURL)
                            .buildUpon()
                            .appendQueryParameter("api_key", apiKey)
                            .build();
                    String myUrl = builtUri.toString();
                    makeRequest(myUrl);
                } else {
                    Snackbar snackbar = Snackbar.make(binding.linLayout, R.string.connection_Not_Available, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    binding.progressBar1.setVisibility(View.GONE);
                }
                return true;
            case R.id.sortbyrating:
                choice = ratedMovies;
                if (isOnline()) {
                    Uri builtUri = Uri.parse(topRatedMoviesUrl)
                            .buildUpon()
                            .appendQueryParameter("api_key", apiKey)
                            .build();
                    String myUrl = builtUri.toString();
                    makeRequest(myUrl);
                } else {
                    Snackbar snackbar = Snackbar.make(binding.linLayout, R.string.connection_Not_Available, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    binding.progressBar1.setVisibility(View.GONE);
                }
                return true;
            case R.id.favourites:
                choice = favoriteMovies;
                if (isOnline())
                    getSupportLoaderManager().restartLoader(0, null, this);
                else {
                    Snackbar snackbar = Snackbar.make(binding.linLayout, R.string.connection_Not_Available, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    binding.progressBar1.setVisibility(View.GONE);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void makeRequest(String urlStr) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONObject json = null;
                        Log.d("Response", response.toString());
                        JSONArray results = null;
                        movieList = new ArrayList<>();

                        try {
                            results = response.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {

                                json = results.getJSONObject(i);

                                String title = json.getString("title");
                                String posterPath = json.getString("poster_path");
                                String synopsis = json.getString("overview");
                                Object userRating = json.get("vote_average");
                                String releaseDate = json.getString("release_date");
                                Integer movieId = json.getInt("id");

                                Movie mv = new Movie();
                                mv.setPlot(synopsis);
                                mv.setPosterImage(posterPath);
                                mv.setRating(userRating.toString());
                                mv.setReleaseDate(releaseDate);
                                mv.setTitle(title);
                                mv.setMovieId(movieId);

                                movieList.add(mv);
                            }
                            adapter = new DataAdapter(getApplicationContext(), movieList);
                            adapter.notifyDataSetChanged();
                            binding.posterRecyclerView.setAdapter(adapter);
                            binding.progressBar1.setVisibility(View.GONE);

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-length", "0");

                return params;
            }
        };

// add it to the RequestQueue
        queue.add(getRequest);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FavMoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null) {
            System.out.println("LoadFinished data size=" + data.getCount());

            favAdapter = new FavoriteMoviesAdapter(getApplicationContext());
            favAdapter.notifyDataSetChanged();
            if (choice == favoriteMovies)
                binding.posterRecyclerView.setAdapter(favAdapter);
            favAdapter.swapCursor(data); //swap the new cursor in.
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (favAdapter != null)
            favAdapter.swapCursor(null);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (choice == favoriteMovies)
            getSupportLoaderManager().restartLoader(0, null, this);
    }
}
