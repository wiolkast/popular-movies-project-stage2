package pl.example.android.popularmovies.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pl.example.android.popularmovies.R;
import pl.example.android.popularmovies.database.MovieDatabase;
import pl.example.android.popularmovies.model.Movie;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener {

    private MovieDetailViewModel model;
    private Boolean isFavorite = false;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private List<List<String>> trailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        final Movie movie = intent.getParcelableExtra("MovieDetails");

        TextView title = findViewById(R.id.title);
        TextView releaseDate = findViewById(R.id.release_date);
        final ImageView poster = findViewById(R.id.poster);
        TextView voteAverage = findViewById(R.id.vote_average);
        TextView plotSynopsis = findViewById(R.id.plot_synopsis);
        final ImageView star = findViewById(R.id.favorite_star);
        final TextView trailersTitle = findViewById(R.id.trailers_title);
        final TextView reviewsTitle = findViewById(R.id.reviews_title);

        RecyclerView trailerRecyclerView = findViewById(R.id.rv_trailer_list);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerAdapter = new TrailerAdapter(new ArrayList<List<String>>(), this);
        trailerRecyclerView.setNestedScrollingEnabled(false);
        trailerRecyclerView.setAdapter(trailerAdapter);

        RecyclerView reviewRecyclerView = findViewById(R.id.rv_review_list);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<List<String>>());
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewRecyclerView.setAdapter(reviewAdapter);

        MovieDatabase database = MovieDatabase.getInstance(getApplicationContext());

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String movieDbApiKey = sharedPreferences.getString(getString(R.string.movie_db_api_key), "");

        MovieDetailViewModelFactory factory = new MovieDetailViewModelFactory(database, movie.getMovieId(), movieDbApiKey);
        model = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);
        model.getIsFavorite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                isFavorite = aBoolean;
                if (isFavorite) {
                    star.setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                } else {
                    star.setColorFilter(Color.GRAY);
                }
            }
        });
        model.getTrailers().observe(this, new Observer<List<List<String>>>() {
            @Override
            public void onChanged(@Nullable List<List<String>> trailers) {
                if(trailers == null || trailers.isEmpty()){
                    trailersTitle.setVisibility(View.GONE);
                } else {
                    trailersTitle.setVisibility(View.VISIBLE);
                }
                trailerAdapter.updateData(trailers);
                trailerList = trailers;
            }
        });
        model.getReviews().observe(this, new Observer<List<List<String>>>() {
            @Override
            public void onChanged(@Nullable List<List<String>> reviews) {
                if(reviews == null || reviews.isEmpty()){
                    reviewsTitle.setVisibility(View.GONE);
                } else {
                    reviewsTitle.setVisibility(View.VISIBLE);
                }
                reviewAdapter.updateData(reviews);
            }
        });

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    model.deleteMovie(movie);
                } else {
                    model.insertMovie(movie);
                }
            }
        });

        title.setText(movie.getMovieTitle());
        releaseDate.setText(movie.getMovieReleaseDate());
        Picasso.with(this).load(movie.getMoviePosterSmall())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(poster);
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        plotSynopsis.setText(movie.getPlotSynopsis());
    }

    @Override
    public void onTrailerClick(int itemId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailerList.get(itemId).get(0)));
        startActivity(intent);
    }
}
