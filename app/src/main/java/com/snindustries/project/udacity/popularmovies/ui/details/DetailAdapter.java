package com.snindustries.project.udacity.popularmovies.ui.details;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.snindustries.project.udacity.popularmovies.BR;
import com.snindustries.project.udacity.popularmovies.R;
import com.snindustries.project.udacity.popularmovies.repository.webservice.Review;
import com.snindustries.project.udacity.popularmovies.repository.webservice.Video;

import java.util.Collections;
import java.util.List;

/**
 * @author Shaaz Noormohammad
 * (c) 11/5/18
 */
class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.MyViewHolder> {


    private final MovieDetailActivity.MovieDetailViewModel model;
    private ReviewHandler defaultReviewHandler = new ReviewHandler();
    private VideoHandler defaultVideoHandler = new VideoHandler();
    private DetailHandler handler = new DetailHandler();
    private LayoutInflater layoutInflater;
    private List<Review> reviews = Collections.emptyList();
    private List<Video> videos = Collections.emptyList();

    public DetailAdapter(MovieDetailActivity.MovieDetailViewModel model) {
        this.model = model;
    }

    private Object getHandler(int position) {
        if (position == 0) {
            return handler;
        }
        position = position - 1;
        if (position <= videos.size() - 1) {
            return defaultVideoHandler;
        }
        return defaultReviewHandler;
    }

    private Object getItem(int position) {
        if (position == 0) {
            return model;
        }
        position = position - 1;
        if (position <= videos.size() - 1) {
            return videos.get(position);
        }
        position = position - videos.size();
        return reviews.get(position);
    }

    @Override
    public int getItemCount() {
        return 1 + videos.size() + reviews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.movie_detail_overview;
        }
        position = position - 1;
        if (position <= videos.size() - 1) {
            return R.layout.detail_video_item;
        }
        return R.layout.detail_review_item;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setVariable(BR.item, getItem(position));
        holder.binding.setVariable(BR.handler, getHandler(position));
        holder.binding.executePendingBindings();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.layoutInflater = (layoutInflater == null ? LayoutInflater.from(parent.getContext()) : layoutInflater);
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
        return new MyViewHolder(binding);
    }

    public void setReviews(@Nullable List<Review> results) {
        this.reviews = results != null ? results : Collections.emptyList();
        notifyDataSetChanged();
    }

    public void setVideos(@Nullable List<Video> results) {
        this.videos = results != null ? results : Collections.emptyList();
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding binding;

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
