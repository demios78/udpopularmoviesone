package com.snindustries.project.udacity.popularmovies;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snindustries.project.udacity.popularmovies.repository.Movie;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbClient;
import com.squareup.picasso.Picasso;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class MoviesPagingAdapter extends PagedListAdapter<MovieExt, MoviesPagingAdapter.MovieViewHolder> {
    static private DiffUtil.ItemCallback<MovieExt> DIFF_CALLBACK = new DiffUtil.ItemCallback<MovieExt>() {
        @Override
        public boolean areContentsTheSame(MovieExt oldItem, MovieExt newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(MovieExt oldItem, MovieExt newItem) {
            return oldItem.movie.getId().equals(newItem.movie.getId());
        }
    };
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListner;
    private final int targetWidth;

    public MoviesPagingAdapter(int targetWidth, MovieSelectionListener selectionListener) {
        super(DIFF_CALLBACK);
        this.targetWidth = targetWidth;
        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.onItemClicked(v, (Movie) v.getTag());
            }
        };
        this.onLongClickListner = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return selectionListener.onItemLongClicked(v, (Movie) v.getTag());
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder viewHolder, int position) {
        MovieExt item = getItem(position);

        //Need to set a size or else the view collapses without an image loaded.
        viewHolder.moviePoster.setMinimumWidth(targetWidth - 1);
        viewHolder.moviePoster.setMinimumHeight((int) (targetWidth * 1.5 - 2));

        Picasso.get()
                .load(ImdbClient.get().getPosterURL(item.movie))
                .resize(targetWidth, (int) (targetWidth * 1.5))
                .centerCrop()
                .into(viewHolder.moviePoster);

        viewHolder.moviePoster.setTag(item);

        viewHolder.moviePoster.setOnClickListener(onClickListener);
        viewHolder.moviePoster.setOnLongClickListener(onLongClickListner);

        viewHolder.favoriteIcon.setVisibility(item.ext.favorite ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false));
    }

    public interface MovieSelectionListener {
        void onItemClicked(View view, Movie movie);

        boolean onItemLongClicked(View view, Movie movie);
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final ImageView favoriteIcon;
        final ImageView moviePoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_poster);
            favoriteIcon = itemView.findViewById(R.id.favorite_star);
        }
    }

}
