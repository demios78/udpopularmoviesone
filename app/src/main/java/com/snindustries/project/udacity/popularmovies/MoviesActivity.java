package com.snindustries.project.udacity.popularmovies;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity {

    private MoviesAdapter adapter;

    private int getHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    private int getNumberOfPosters() {
        return (int) Math.floor(getWidth() / 520);
    }

    private int getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Log.d("width", getWidth() + " h " + getHeight());
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, getNumberOfPosters()));

        adapter = new MoviesAdapter(getWidth() / getNumberOfPosters());
        recycler.setAdapter(adapter);
        adapter.setSelectionListener(new MoviesAdapter.SelectionListener<Movie>() {
            @Override
            public void onItemClicked(View view, Movie item) {
                //TODO call next activity
                Toast.makeText(view.getContext(), "Item Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        new GetMoviesTask(adapter).execute(1);//get first page
    }

    public static class GetMoviesTask extends AsyncTask<Integer, Void, MovieSearchResponse> {
        private final MoviesAdapter adapter;

        public GetMoviesTask(MoviesAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected MovieSearchResponse doInBackground(Integer... integers) {
            try {
                return ImdbClient.get().getMoviesPopular(integers[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieSearchResponse response) {
            if (response != null && response.results != null) {
                for (Movie result : response.results) {
                    adapter.add(result);
                }
            }
        }
    }

    public static class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

        private final int targetWidth;
        private SelectionListener<Movie> listener;
        private List<Movie> moviesList = new ArrayList<>();

        public MoviesAdapter(int targetWidth) {
            this.targetWidth = targetWidth;
        }


        public void add(Movie movie) {
            moviesList.add(movie);
            notifyItemInserted(moviesList.size() - 1);
        }

        private Movie getItem(int position) {
            return moviesList.get(position);
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        protected int getLayoutId() {
            return R.layout.movie_list_item;
        }

        protected String getPosterURL(int position) {
            return ImdbClient.get().getPosterURL(getItem(position));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
            Log.d("asdfa", getPosterURL(position));

            Picasso.get()
                    .load(getPosterURL(position))
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

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
            return new MovieViewHolder(view);
        }

        public void setSelectionListener(SelectionListener<Movie> listener) {
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

    }
}
