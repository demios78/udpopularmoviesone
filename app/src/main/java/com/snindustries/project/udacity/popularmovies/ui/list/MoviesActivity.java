package com.snindustries.project.udacity.popularmovies.ui.list;

import android.arch.lifecycle.MutableLiveData;
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

import com.snindustries.project.udacity.popularmovies.R;
import com.snindustries.project.udacity.popularmovies.databinding.ActivityMoviesBinding;
import com.snindustries.project.udacity.popularmovies.repository.NetworkState;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;
import com.snindustries.project.udacity.popularmovies.ui.details.MovieDetailActivity;

import java.util.Objects;

import static com.snindustries.project.udacity.popularmovies.repository.Repository.FAVORITE;
import static com.snindustries.project.udacity.popularmovies.repository.Repository.HIGHEST_RATED;
import static com.snindustries.project.udacity.popularmovies.repository.Repository.MOST_POPULAR;

/**
 * Displays a list of movies that are sorted by popular or top rated.
 * Scrolls to several pages of responses.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public class MoviesActivity extends AppCompatActivity {


    private static final int POSTER_WIDTH = 520;

    private MoviesPagingAdapter adapter;
    private MoviesViewModel movieViewModel;
    private MutableLiveData<NetworkState> networkState;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetworkAvailability();
        }
    };
    private Observable.OnPropertyChangedCallback propCallback;

    private void checkNetworkAvailability() {
        if (!isNetworkConnected()) {
            if (networkState.getValue() != NetworkState.DISCONNECTED) {
                networkState.postValue(NetworkState.DISCONNECTED);
            }
        } else {
            if (networkState.getValue() != NetworkState.IDLE && networkState.getValue() != NetworkState.LOADED) {
                networkState.postValue(NetworkState.IDLE);
            }
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
        ActivityMoviesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movies);
        movieViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);

        adapter = new MoviesPagingAdapter(getWidth() / getNumberOfPosters(), new DefaultSelectionListener());
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new GridLayoutManager(this, getNumberOfPosters()));
        binding.recycler.addOnScrollListener(new NextResultsPageScrollListener(binding.recycler.getLayoutManager()));
        movieViewModel.getMovies().observe(this, new Observer<PagedList<MovieExt>>() {
            @Override
            public void onChanged(@Nullable PagedList<MovieExt> movieExts) {
                adapter.submitList(movieExts);
            }
        });
        networkState = movieViewModel.getNetworkState();
        networkState.observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    findViewById(R.id.network_disconnected).setVisibility(networkState == NetworkState.DISCONNECTED ? View.VISIBLE : View.GONE);
                    if (networkState == NetworkState.IDLE) {
                        movieViewModel.getNextMovies();
                    }
                }
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
                movieViewModel.setOrder(MOST_POPULAR);
                return true;
            case R.id.top_rated:
                movieViewModel.setOrder(HIGHEST_RATED);
                return true;
            case R.id.favorites:
                movieViewModel.setOrder(FAVORITE);
                return true;
            case R.id.addMovies:
                movieViewModel.getNextMovies();
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
                setActivityTitle(movieViewModel.getOrder().get());
            }
        });
        setActivityTitle(movieViewModel.getOrder().get());
    }

    private void setActivityTitle(int sortConstant) {
        setTitle(sortConstant == MOST_POPULAR ? R.string.most_popular : sortConstant == HIGHEST_RATED ? R.string.top_rated : R.string.favorites);
    }

    private class DefaultSelectionListener implements MoviesPagingAdapter.MovieSelectionListener {
        @Override
        public void onItemClicked(View view, MovieExt movie) {
            Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.movie.getId());
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onItemLongClicked(View view, MovieExt movie) {
            movieViewModel.toggleFavorite(movie.ext);
            return true;
        }
    }


    /**
     * Listen for the user to scroll near the bottom of the results.  Then load more.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
    private class NextResultsPageScrollListener extends RecyclerView.OnScrollListener {
        private final GridLayoutManager layoutManager;

        public NextResultsPageScrollListener(RecyclerView.LayoutManager layoutManager) {
            this.layoutManager = (GridLayoutManager) layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            if (totalItemCount <= lastVisibleItem + layoutManager.getSpanCount()) {
                movieViewModel.getNextMovies();
            }
        }
    }
}
