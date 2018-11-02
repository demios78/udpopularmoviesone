package com.snindustries.project.udacity.popularmovies;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;
import com.squareup.picasso.Picasso;

/**
 * Loads data some at a time.
 *
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
            return oldItem.getId().equals(newItem.getId());
        }
    };
    private final int targetWidth;

    public MoviesPagingAdapter(int targetWidth) {
        super(DIFF_CALLBACK);
        this.targetWidth = targetWidth;
    }

    @Nullable
    @Override
    public Movie getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesActivity.MovieViewHolder viewHolder, int position) {
        Movie item = getItem(position);

        //Need to set a size or else the view collapses without an image loaded.
        viewHolder.moviePoster.setMinimumWidth(targetWidth - 1);
        viewHolder.moviePoster.setMinimumHeight((int) (targetWidth * 1.5 - 2));

        Picasso.get()
                .load(ImdbClient.get().getPosterURL(item))
                .resize(targetWidth, (int) (targetWidth * 1.5))
                .centerCrop()
                .into(viewHolder.moviePoster);

        boolean isFav = item.getFavorite() != null ? item.getFavorite() : false;
        viewHolder.favoriteIcon.setVisibility(isFav ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public MoviesActivity.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoviesActivity.MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false));
    }
}
