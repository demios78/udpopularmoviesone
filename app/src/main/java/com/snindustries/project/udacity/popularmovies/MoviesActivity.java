package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.snindustries.project.udacity.popularmovies.databinding.ActivityMoviesBinding;
import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.util.Objects;

/**
 * Displays a list of movies that are sorted by popular or top rated.
 * Scrolls to several pages of responses.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public class MoviesActivity extends AppCompatActivity {

    private static final int MOST_POPULAR = 0;
    private static final int POSTER_WIDTH = 520;//TODO configure this in DP
    private static final int TOP_RATED = 1;
    ActivityMoviesBinding binding;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetworkAvailability();
        }
    };
    private MoviesPagingAdapter adapter;
    private int listSort = MOST_POPULAR;
    private MovieViewModel movieViewModel;
    private Observer<PagedList<Movie>> observer;

    private void checkNetworkAvailability() {
        binding.networkDisconnected.setVisibility(!isNetworkConnected() ? View.VISIBLE : View.GONE);
        if (isNetworkConnected()) {
            //initializeMovieList();//TODO
        }
    }

    /**
     * Gets number of posters that can fit on this screen width.
     *
     * @return
     */
    private int getNumberOfPosters() {
        return (int) Math.floor(getWidth() / POSTER_WIDTH);
    }

    /**
     * Gets the available width for a poster.
     *
     * @param numberOfPosters
     * @return
     */
    private int getTargetWidth(int numberOfPosters) {
        return getWidth() / numberOfPosters;
    }

    /**
     * Width of the screen.
     *
     * @return
     */
    private int getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(manager).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movies);
        binding.recycler.setLayoutManager(new GridLayoutManager(this, getNumberOfPosters()));


        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        adapter = new MoviesPagingAdapter(getTargetWidth(getNumberOfPosters()));
        observer = new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> movies) {
                adapter.submitList(movies);
            }
        };
        binding.recycler.setAdapter(adapter);
        binding.recycler.addOnItemTouchListener(new RecyclerTouchListener(this, binding.recycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie item = adapter.getItem(position);
                Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_PARCEL, item);
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Movie item = adapter.getItem(position);
                item.setFavorite(item.getFavorite() == null || !item.getFavorite());
                movieViewModel.update(item, new MovieRepository.UpdateCallback() {
                    @Override
                    public void updated(int rows) {
                        adapter.notifyItemChanged(position);
                    }
                });
            }
        }));
        resetList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                movieViewModel.setSortOrder(MovieViewModel.MOST_POPULAR);
                resetList();
                return true;
            case R.id.top_rated:
                movieViewModel.setSortOrder(MovieViewModel.HIGHEST_RATED);
                resetList();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    private void resetList() {
        int sortConstant = movieViewModel.getSortOrder().get();
        setTitle(sortConstant == MOST_POPULAR ? R.string.most_popular : R.string.top_rated);
        listSort = sortConstant;
        if (adapter != null) {
            movieViewModel.getMoviePagedListRated().observe(this, observer);
        } else {
            checkNetworkAvailability();
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycler, final ClickListener clickListener) {
            this.clickListener = clickListener;
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycler.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recycler.getChildAdapterPosition(child));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
    }
}
