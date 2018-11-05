package com.snindustries.project.udacity.popularmovies.ui.details;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.snindustries.project.udacity.popularmovies.R;
import com.snindustries.project.udacity.popularmovies.databinding.ActivityMovieDetailBinding;
import com.snindustries.project.udacity.popularmovies.repository.Repository;
import com.snindustries.project.udacity.popularmovies.repository.database.ExtraProperties;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbClient;
import com.snindustries.project.udacity.popularmovies.repository.webservice.MovieDetailResponse;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ReviewsResponse;
import com.snindustries.project.udacity.popularmovies.repository.webservice.VideosResponse;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * Displays data about a specific movie.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";

    private ActivityMovieDetailBinding binding;
    private MovieDetailViewModel viewModel;

    private void initializeUi(@Nullable MovieExt item) {
        if (item == null) {
            return;
        }
        setTitle(item.movie.getTitle());
        Picasso.get().load(ImdbClient.get().getPosterURL(item.movie)).into(binding.moviePoster);
        binding.favoriteStar.setAlpha(item.ext.favorite ? 1f : .25f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        setSupportActionBar(binding.toolbar);

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId != -1) {

            viewModel = ViewModelProviders.of(
                    this,
                    new MovieDetailModelFactory(getApplication(), movieId)
            )
                    .get(MovieDetailViewModel.class);

            binding.setItem(viewModel);
            binding.setLifecycleOwner(this);

            viewModel.movieExt.observe(this, new Observer<MovieExt>() {
                @Override
                public void onChanged(@Nullable MovieExt movieExt) {
                    initializeUi(movieExt);
                }
            });
        }

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
        private final LiveData<MovieDetailResponse> details;
        private final LiveData<MovieExt> movieExt;
        private final Repository repository;
        private final LiveData<ReviewsResponse> reviews;
        private final LiveData<VideosResponse> videos;

        public MovieDetailViewModel(@NonNull Application application, int movieId) {
            repository = new Repository(application);
            movieExt = repository.getMovie(movieId);
            details = repository.getMovieDetails(movieId);
            videos = repository.getMovieVideos(movieId);
            reviews = repository.getMovieReviews(movieId);
        }

        public LiveData<MovieDetailResponse> getDetails() {
            return details;
        }

        public LiveData<MovieExt> getMovieExt() {
            return movieExt;
        }

        public LiveData<ReviewsResponse> getReviews() {
            return reviews;
        }

        public LiveData<VideosResponse> getVideos() {
            return videos;
        }

        public void onFavoriteClicked(View view) {
            ExtraProperties ext = Objects.requireNonNull(movieExt.getValue()).ext;
            ext.favorite = !ext.favorite;
            repository.update(ext);
        }
    }

}
