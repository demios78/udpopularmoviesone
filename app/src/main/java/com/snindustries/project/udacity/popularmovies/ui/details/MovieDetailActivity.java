package com.snindustries.project.udacity.popularmovies.ui.details;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.snindustries.project.udacity.popularmovies.R;
import com.snindustries.project.udacity.popularmovies.databinding.ActivityMovieDetailBinding;
import com.snindustries.project.udacity.popularmovies.repository.Repository;
import com.snindustries.project.udacity.popularmovies.repository.database.ExtraProperties;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ReviewsResponse;
import com.snindustries.project.udacity.popularmovies.repository.webservice.VideosResponse;

/**
 * Displays data about a specific movie.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(".");

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);

        MovieDetailViewModel viewModel = ViewModelProviders.of(this,
                new MovieDetailModelFactory(getApplication(), movieId))
                .get(MovieDetailViewModel.class);

        binding.setItem(viewModel);
        binding.setLifecycleOwner(this);

        final DetailAdapter adapter = new DetailAdapter(viewModel);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        viewModel.getMovie().observe(this, movieExt -> adapter.notifyDataSetChanged());
        viewModel.getReviews().observe(this, reviewsResponse -> adapter.setReviews(reviewsResponse.results));
        viewModel.getVideos().observe(this, videosResponse -> adapter.setVideos(videosResponse.results));
    }

    public static class MovieDetailModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final Application application;
        private final int id;

        public MovieDetailModelFactory(Application application, int id) {
            this.application = application;
            this.id = id;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MovieDetailViewModel(application, id);
        }
    }

    public static class MovieDetailViewModel extends ViewModel {
        //private final LiveData<MovieDetailResponse> details;
        private final ObservableBoolean favorite = new ObservableBoolean(false);
        private final ObservableField<String> imageUrl = new ObservableField<>();
        private final LiveData<MovieExt> movie;
        private final ObservableField<String> overview = new ObservableField<>();
        private final Repository repository;
        private final LiveData<ReviewsResponse> reviews;
        private final ObservableField<String> title = new ObservableField<>();
        private final LiveData<VideosResponse> videos;
        private final ObservableDouble voteAverage = new ObservableDouble(0);

        public MovieDetailViewModel(@NonNull Application application, int movieId) {
            repository = new Repository(application);
            movie = repository.getMovie(movieId);//From DB
            //details = repository.getMovieDetails(movieId);//From network
            videos = repository.getMovieVideos(movieId);//From network
            reviews = repository.getMovieReviews(movieId);//From network
            movie.observeForever(new Observer<MovieExt>() {
                @Override
                public void onChanged(@Nullable MovieExt movieExt) {
                    if (movieExt != null) {
                        title.set(movieExt.movie.getTitle());
                        imageUrl.set(movieExt.movie.getPosterPath());
                        favorite.set(movieExt.ext.favorite);
                        voteAverage.set(movieExt.movie.getVoteAverage());
                        overview.set(movieExt.movie.getOverview());
                    }
                }
            });

        }

        public ObservableBoolean getFavorite() {
            return favorite;
        }

        public ObservableField<String> getImageUrl() {
            return imageUrl;
        }

        public LiveData<MovieExt> getMovie() {
            return movie;
        }

        public ObservableField<String> getOverview() {
            return overview;
        }

        public LiveData<ReviewsResponse> getReviews() {
            return reviews;
        }

        public ObservableField<String> getTitle() {
            return title;
        }

        public LiveData<VideosResponse> getVideos() {
            return videos;
        }

        public ObservableDouble getVoteAverage() {
            return voteAverage;
        }

        public void onFavoriteClicked(View view) {
            ExtraProperties ext = movie.getValue().ext;
            ext.favorite = !ext.favorite;
            repository.update(ext);
        }
    }


}
