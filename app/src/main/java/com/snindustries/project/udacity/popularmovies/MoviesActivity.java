package com.snindustries.project.udacity.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snindustries.project.udacity.popularmovies.databinding.ActivityMoviesBinding;
import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private MoviesAdapter adapter;
    private int currentPage;
    private int listSort = MOST_POPULAR;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetworkAvailability();
        }
    };

    private void checkNetworkAvailability() {
        binding.networkDisconnected.setVisibility(!isNetworkConnected() ? View.VISIBLE : View.GONE);
        if (isNetworkConnected()) {
            initializeMovieList();
        }
    }

    private void getNextPageOfResults() {
        if (isNetworkConnected()) {
            new GetMoviesTask(adapter, listSort).execute(++currentPage);
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

    private void initializeMovieList() {
        RecyclerView recycler = findViewById(R.id.recycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, getNumberOfPosters());
        if (adapter == null) {
            adapter = new MoviesAdapter(getWidth() / getNumberOfPosters());
        }
        recycler.setAdapter(adapter);
        final MoviesAdapter finalAdapter = adapter;

        recycler.setLayoutManager(layoutManager);
        recycler.addOnScrollListener(new NextResultsPageScrollListener(finalAdapter, layoutManager));

        adapter.setSelectionListener(new MovieSelectionListener());
        if (!adapter.isLoading()) {
            resetList(MOST_POPULAR);
        }
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

    private void resetList(int sortConstant) {
        setTitle(sortConstant == MOST_POPULAR ? R.string.most_popular : R.string.top_rated);
        listSort = sortConstant;
        currentPage = 0;
        if (adapter != null) {
            adapter.reset();
            getNextPageOfResults();
        } else {
            checkNetworkAvailability();
        }
    }

    /**
     * Gets the next page of movie results.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
    static class GetMoviesTask extends AsyncTask<Integer, Void, MovieSearchResponse> {
        private final MoviesAdapter adapter;
        private final int listSort;

        GetMoviesTask(MoviesAdapter adapter, int listSort) {
            this.adapter = adapter;
            this.listSort = listSort;
        }

        @Override
        protected MovieSearchResponse doInBackground(Integer... integers) {
            try {
                switch (listSort) {
                    case MOST_POPULAR:
                        return ImdbClient.get().getMoviesPopular(integers[0]);
                    case TOP_RATED:
                        return ImdbClient.get().getMoviesTopRated(integers[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieSearchResponse response) {
            adapter.setLoading(false);
            if (response != null && response.results != null) {
                for (Movie result : response.results) {
                    adapter.add(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            adapter.setLoading(true);
        }
    }

    /**
     * When a movie is selected, display its details.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
    private static class MovieSelectionListener implements MoviesAdapter.SelectionListener<Movie> {
        @Override
        public void onItemClicked(View view, Movie item) {
            Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_PARCEL, item);
            view.getContext().startActivity(intent);
        }
    }

    public static class MoviesActivityViewModel {

    }

    /**
     * Display movies or loading icon in a list.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
    static class MoviesAdapter extends RecyclerView.Adapter {
        private final int MOVIE_ITEM = 0;
        private final int PROGRESS_ITEM = 1;
        private final List<Movie> moviesList = new ArrayList<>();
        private final int targetWidth;
        private SelectionListener<Movie> listener;
        private boolean loading;

        MoviesAdapter(int targetWidth) {
            this.targetWidth = targetWidth;
        }


        void add(Movie movie) {
            if (moviesList.size() > 0 && moviesList.get(lastItem()) == null && movie != null) {
                moviesList.remove(lastItem());
                moviesList.add(movie);
                notifyItemChanged(lastItem());
                moviesList.add(null);
                notifyItemInserted(lastItem());
            } else {
                moviesList.add(movie);
                notifyItemInserted(lastItem());
            }
        }

        private Movie getItem(int position) {
            return moviesList.get(position);
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return moviesList.get(position) != null ? MOVIE_ITEM : PROGRESS_ITEM;
        }

        boolean isLoading() {
            return loading;
        }

        void setLoading(boolean loading) {
            this.loading = loading;
            if (this.loading) {
                if (moviesList.size() > 0 && moviesList.get(lastItem()) != null) {
                    add(null);
                }
            } else {
                if (moviesList.size() > 0 && moviesList.get(lastItem()) == null) {
                    moviesList.remove(lastItem());
                    notifyItemRemoved(moviesList.size());
                }
            }
        }

        private int lastItem() {
            return moviesList.size() - 1;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MovieViewHolder) {
                MovieViewHolder holder = (MovieViewHolder) viewHolder;
                //Need to set a size or else the view collapses without an image loaded.
                holder.moviePoster.setMinimumWidth(targetWidth - 1);
                holder.moviePoster.setMinimumHeight((int) (targetWidth * 1.5 - 2));
                Picasso.get()
                        .load(ImdbClient.get().getPosterURL(getItem(position)))
                        .resize(targetWidth, (int) (targetWidth * 1.5))
                        .centerCrop()
                        .into(holder.moviePoster);
                final Movie item = getItem(position);
                holder.moviePoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onItemClicked(view, item);
                        }
                    }
                });
            }
            //else do nothing since progressviewholder is already set.
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case MOVIE_ITEM:
                    return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false));
                case PROGRESS_ITEM:
                default:
                    return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false));
            }

        }

        void reset() {
            moviesList.clear();
            notifyDataSetChanged();
        }

        void setSelectionListener(SelectionListener<Movie> listener) {
            this.listener = listener;
        }

        interface SelectionListener<T> {
            void onItemClicked(View view, T item);
        }

        static class MovieViewHolder extends RecyclerView.ViewHolder {
            final ImageView moviePoster;

            MovieViewHolder(View itemView) {
                super(itemView);
                moviePoster = itemView.findViewById(R.id.movie_poster);
            }
        }

        static class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressViewHolder(View itemView) {
                super(itemView);
            }
        }

    }

    /**
     * Listen for the user to scroll near the bottom of the results.  Then load more.
     *
     * @author shaaz noormohammad
     * October 1, 2018
     */
    private class NextResultsPageScrollListener extends RecyclerView.OnScrollListener {
        final Handler osHandler;
        private final MoviesAdapter finalAdapter;
        private final GridLayoutManager layoutManager;

        public NextResultsPageScrollListener(MoviesAdapter finalAdapter, GridLayoutManager layoutManager) {
            this.finalAdapter = finalAdapter;
            this.layoutManager = layoutManager;
            osHandler = new Handler();
        }

        private void loadMore() {
            //Recycler view cannot be updated from onScrolled method.
            //So update it from UI thread.
            osHandler.post(new Runnable() {
                @Override
                public void run() {
                    new GetMoviesTask(finalAdapter, listSort).execute(++currentPage);
                }
            });
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            synchronized (finalAdapter) {
                if (!finalAdapter.isLoading()) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if (totalItemCount <= lastVisibleItem + layoutManager.getSpanCount()) {
                        loadMore();
                    }
                }
            }
        }
    }
}
