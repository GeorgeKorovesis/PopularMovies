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

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    ArrayList<String> reviewAuthorList;
    ArrayList<String> reviewTextList;

    public ReviewsAdapter(ArrayList<String> authorList, ArrayList<String> reviewList) {
        reviewAuthorList = authorList;
        reviewTextList = reviewList;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, final int position) {
        holder.reviewAuthor.setText("@"+reviewAuthorList.get(position));
        holder.reviewText.setText(reviewTextList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewAuthorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView reviewAuthor,reviewText;

        public ViewHolder(View view) {
            super(view);
            reviewAuthor = (TextView) view.findViewById(R.id.reviewTitle);
            reviewText = (TextView) view.findViewById(R.id.reviewText);

        }
    }
}
