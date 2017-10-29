package com.example.korg.popularmoviesst1;

/**
 * Created by korg on 19/5/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<Movie> movieList;
    private Context context;
    public final static String PAR_KEY = "MovieParcelableKey";

    public DataAdapter(Object listener, ArrayList<Movie> list) {
        movieList = list;
        this.context = (Context) listener;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, final int i) {


        Picasso.with(context).load(movieList.get(i).getPosterImage()).into(viewHolder.movieImg);
        System.out.println("Data Adapter - Poster URL = " + movieList.get(i).getPosterImage());


        viewHolder.movieImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*get details of video-trailer,etc*/
                Intent intent = new Intent(context, MovieDetails.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(PAR_KEY, movieList.get(i));
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView movieImg;

        public ViewHolder(View view) {
            super(view);
            movieImg = (ImageView) view.findViewById(R.id.img_android);
        }
    }

}
