package com.example.korg.popularmoviesst1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by korg on 31/8/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    Context context;
    ArrayList<String> trailerList;
    private final String youtubeUrl = "http://www.youtube.com/watch";

    public TrailerAdapter(Object listener, ArrayList<String> list) {
        trailerList = list;
        this.context = (Context) listener;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, final int position) {
        holder.trailerView.setText("Trailer " + (position + 1));
        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(youtubeUrl).buildUpon().appendQueryParameter("v", trailerList.get(position)).build();
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView startBtn;
        private TextView trailerView;

        public ViewHolder(View view) {
            super(view);
            startBtn = (ImageView) view.findViewById(R.id.imageBtn);
            trailerView = (TextView) view.findViewById(R.id.trailerTitle);
        }
    }
}
