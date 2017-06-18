package com.example.android.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * Created by Victor on 15/06/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>  {

    private ArrayList<Review> reviews;

    public ReviewAdapter(ArrayList<Review> review){
        this.reviews = review;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_item_layout, parent , false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review item = reviews.get(position);
        holder.author.setText(item.getAuthor());
        holder.review.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView review;

        public ReviewHolder(View itemView) {
            super(itemView);
            author = (TextView)itemView.findViewById(R.id.author);
            review =(TextView)itemView.findViewById(R.id.review_text);


        }
    }
}
