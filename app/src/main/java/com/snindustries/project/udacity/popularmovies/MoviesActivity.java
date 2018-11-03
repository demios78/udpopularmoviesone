package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.snindustries.project.udacity.popularmovies.databinding.ActivityMoviesBinding;
import com.snindustries.project.udacity.popularmovies.repository.Movie;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;

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
    private static final int POSTER_WIDTH = 520;
    private static final int TOP_RATED = 1;
    private MoviesPagingAdapter adapter;
    private ActivityMoviesBinding binding;
    private int currentPage;
    private int listSort = MOST_POPULAR;
    private MoviesViewModel movieViewModel;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetworkAvailability();
        }
    };
    private Observer<PagedList<MovieExt>> observer;
    private Observable.OnPropertyChangedCallback propCallback;

    private void checkNetworkAvailability() {
        if (!isNetworkConnected()) {
            findViewById(R.id.network_disconnected).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.network_disconnected).setVisibility(View.GONE);
//            initializeMovieList();
            movieViewModel.getNextPopularMovies();
        }
    }

    private void getNextPageOfResults() {
        if (isNetworkConnected()) {
            //new GetMoviesTask(adapter, listSort).execute(++currentPage);
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
        movieViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);

        adapter = new MoviesPagingAdapter(getWidth() / getNumberOfPosters(), new DefaultSelectionListener());
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new GridLayoutManager(this, getNumberOfPosters()));
        binding.recycler.addOnScrollListener(new NextResultsPageScrollListener(adapter, binding.recycler.getLayoutManager()));
        movieViewModel.getMovies().observe(this, observer = new Observer<PagedList<MovieExt>>() {
            @Override
            public void onChanged(@Nullable PagedList<MovieExt> movieExts) {
                adapter.submitList(movieExts);
            }
        });
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
                resetList(MOST_POPULAR);
                return true;
            case R.id.top_rated:
                resetList(TOP_RATED);
                return true;
            case R.id.addMovies:
                movieViewModel.getNextPopularMovies();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        movieViewModel.getOrder().removeOnPropertyChangedCallback(propCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
        movieViewModel.getOrder().addOnPropertyChangedCallback(propCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setTitle(movieViewModel.getOrder().get() == MOST_POPULAR ? R.string.most_popular : R.string.top_rated);
            }
        });
    }

    private void resetList(int sortConstant) {
        setTitle(sortConstant == MOST_POPULAR ? R.string.most_popular : R.string.top_rated);
        listSort = sortConstant;
        currentPage = 0;
//        if (adapter != null) {
//            adapter.reset();
//            getNextPageOfResults();
//        } else {
//            checkNetworkAvailability();
//        }
    }

    private static class DefaultSelectionListener implements MoviesPagingAdapter.MovieSelectionListener {
        @Override
        public void onItemClicked(View view, Movie movie) {
            Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_PARCEL, movie);
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onItemLongClicked(View view, Movie movie) {
            Toast.makeText(view.getContext(), "Long Clicked " + movie.getId() + "  " + movie.getOriginalTitle(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    /**
     *  When a movie is selected, display its details.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
//    private static class MovieSelectionListener implements MoviesAdapter.SelectionListener<Movie> {
//        @Override
//        public void onItemClicked(View view, Movie item) {
//            Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
//            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_PARCEL, item);
//            view.getContext().startActivity(intent);
//        }
//    }


    /**
     * Listen for the user to scroll near the bottom of the results.  Then load more.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
    private class NextResultsPageScrollListener extends RecyclerView.OnScrollListener {
        private final MoviesPagingAdapter finalAdapter;
        private final GridLayoutManager layoutManager;

        public NextResultsPageScrollListener(MoviesPagingAdapter finalAdapter, RecyclerView.LayoutManager layoutManager) {
            this.finalAdapter = finalAdapter;
            this.layoutManager = (GridLayoutManager) layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            if (totalItemCount <= lastVisibleItem + layoutManager.getSpanCount()) {
                movieViewModel.getNextPopularMovies();
            }
        }
    }
}
