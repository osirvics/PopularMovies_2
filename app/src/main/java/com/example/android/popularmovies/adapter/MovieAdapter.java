package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.api.OnItemClickListener;
import com.example.android.popularmovies.api.PaginationAdapterCallback;
import com.example.android.popularmovies.model.Results;

import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RequestManager glide;
    OnItemClickListener clickLister;
    private Context context;
    public static final int ITEM = 0;
    public static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private String errorMsg;
    private ArrayList<Results> movies;


    public MovieAdapter(ArrayList<Results> movies, RequestManager mGlide, Context context,
                        PaginationAdapterCallback mCallback, OnItemClickListener clickLister
                        ){
        this.glide = mGlide;
        this.movies  = movies;
        this.clickLister = clickLister;
        this.context = context;
        this.mCallback = mCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View view = layoutInflater
                        .inflate(R.layout.photo_item, parent, false);
                viewHolder = new MovieAdapterHolder(view);
                break;
            case LOADING:
                View view2 = layoutInflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(view2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieAdapterHolder userHolder = (MovieAdapterHolder) holder;
                Results currentMovie = movies.get(position);
                String posterPath = currentMovie.getPosterPath();
                //Log.e("Mainactivity", "poster url: " + "http://image.tmdb.org/t/p/w185/" + posterPath);
                glide.load("http://image.tmdb.org/t/p/w185/" + posterPath)
                        .placeholder(R.color.placeholder_grey_20)
                        //.override(480,400)
                        .centerCrop()
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(userHolder.thumbnail);
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.progressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.progressBar.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public int getItemViewType(int position) {
        return (position == movies.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        //super.onViewRecycled(holder);
        if(holder instanceof MovieAdapterHolder){
            final MovieAdapterHolder userholder = (MovieAdapterHolder) holder;
            Glide.clear(userholder.thumbnail);
        }
    }

    public Results getItem(int position) {
        return movies.get(position);
    }

    public void add(Results mc) {
        movies.add(mc);
        notifyItemInserted(movies.size() - 1);
    }

    public void addAll(ArrayList<Results> mcList) {
        for (Results mc : mcList) {
            add(mc);
        }
    }

    public void remove(Results city) {
        int position = movies.indexOf(city);
        if (position > -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Results());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = movies.size() - 1;
        Results item = getItem(position);
        if (item != null) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }


    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show flag indicating when wheather to show or not
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(movies.size() - 1);
        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    class MovieAdapterHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public ImageView thumbnail;

        public MovieAdapterHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.photo);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickLister.OnItemClick(getLayoutPosition());
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ProgressBar progressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loadmore_retry:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }
}
