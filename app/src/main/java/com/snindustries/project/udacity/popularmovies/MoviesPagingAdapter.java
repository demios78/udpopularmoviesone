package com.snindustries.project.udacity.popularmovies;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;
import com.squareup.picasso.Picasso;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MoviesPagingAdapter extends PagedListAdapter<Movie, MoviesActivity.MovieViewHolder> {
    static private DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };
    private final int targetWidth;

    protected MoviesPagingAdapter(@NonNull Context context, int targetWidth) {
        super(DIFF_CALLBACK);
        this.targetWidth = targetWidth;
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesActivity.MovieViewHolder viewHolder, int position) {
        if (viewHolder instanceof MoviesActivity.MovieViewHolder) {
            MoviesActivity.MovieViewHolder holder = (MoviesActivity.MovieViewHolder) viewHolder;
            //Need to set a size or else the view collapses without an image loaded.
            holder.moviePoster.setMinimumWidth(targetWidth - 1);
            holder.moviePoster.setMinimumHeight((int) (targetWidth * 1.5 - 2));
            Picasso.get()
                    .load(ImdbClient.get().getPosterURL(getItem(position)))
                    .resize(targetWidth, (int) (targetWidth * 1.5))
                    .centerCrop()
                    .into(holder.moviePoster);
        }
    }

    @NonNull
    @Override
    public MoviesActivity.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoviesActivity.MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false));
    }
}
