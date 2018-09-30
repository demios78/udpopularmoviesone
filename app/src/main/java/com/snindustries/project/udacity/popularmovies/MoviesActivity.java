package com.snindustries.project.udacity.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
    }

    public static class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {



        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
           Picasso.get().load("").into( holder.moviePoster );
        }

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return null;
        }

        public static class MovieViewHolder extends RecyclerView.ViewHolder {
            public final ImageView moviePoster;


            public MovieViewHolder(View itemView) {
                super(itemView);
                moviePoster = itemView.findViewById(R.id.movie_poster);
            }
        }
    }
}
