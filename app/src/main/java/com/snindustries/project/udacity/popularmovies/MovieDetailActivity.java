package com.snindustries.project.udacity.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.snindustries.project.udacity.popularmovies.repository.Movie;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbClient;
import com.squareup.picasso.Picasso;

/**
 *
 * Displays data about a specific movie.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_PARCEL = "EXTRA_MOVIE_PARCEL";

    private void initializeUi(Movie item) {

        setTitle(item.getTitle());

        ImageView imageView = findViewById(R.id.movie_poster);
        Picasso.get().load(ImdbClient.get().getPosterURL(item)).into(imageView);

        setTextView(R.id.release_date, item.getReleaseDate());
        setTextView(R.id.plot_overview, item.getOverview());
        setTextView(R.id.user_rating, getString(R.string.vote_format, item.getVoteAverage()));
    }

    private void setTextView(int id, String text) {
        TextView textView = findViewById(id);
        textView.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Movie item = getIntent().getParcelableExtra(EXTRA_MOVIE_PARCEL);
        if (item != null) {
            initializeUi(item);
        }
    }
}
