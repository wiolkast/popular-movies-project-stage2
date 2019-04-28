package pl.example.android.popularmovies.ui.main;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.example.android.popularmovies.R;
import pl.example.android.popularmovies.SettingsActivity;
import pl.example.android.popularmovies.database.MovieDatabase;
import pl.example.android.popularmovies.model.Movie;
import pl.example.android.popularmovies.ui.detail.DetailActivity;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PICTURE_WIDTH_PIXELS = 342;
    private RecyclerView recyclerView;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;
    private MovieAdapter adapter;
    private List<Movie> moviesList;
    private MovieViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorMessageTextView = findViewById(R.id.tv_error_message);
        progressBar = findViewById(R.id.progress_bar);

        int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        int spanCount = (int) Math.floor((double) screenWidthPixels / (double) PICTURE_WIDTH_PIXELS);
        recyclerView = findViewById(R.id.rv_movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
        recyclerView.setAdapter(adapter);

        MovieDatabase database = MovieDatabase.getInstance(getApplicationContext());

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String orderBy = sharedPreferences.getString(getString(R.string.order_by_key), getString(R.string.order_by_default));
        String movieDbApiKey = sharedPreferences.getString(getString(R.string.movie_db_api_key), "");

        final MovieViewModelFactory factory = new MovieViewModelFactory(database, orderBy, movieDbApiKey);
        model = ViewModelProviders.of(this, factory).get(MovieViewModel.class);
        model.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movies) {
                Log.d(LOG_TAG, "Updating list of movies from LiveData in ViewModel");
                moviesList = movies;
                adapter.updateData(movies);
            }
        });
        model.getStatus().observe(this, new Observer<MovieViewModel.Status>() {
            @Override
            public void onChanged(@Nullable MovieViewModel.Status status) {
                Log.d(LOG_TAG, "Updating status from LiveData in ViewModel");
                if (status == null) {
                    return;
                }
                switch (status) {
                    case LOADING:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case NETWORK_ERROR:
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        errorMessageTextView.setText(getString(R.string.network_error_message));
                        errorMessageTextView.setVisibility(View.VISIBLE);
                        break;
                    case NETWORK_UNAUTHORIZED:
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        errorMessageTextView.setText(getString(R.string.network_unauthorized_message));
                        errorMessageTextView.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        errorMessageTextView.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_about:
                showCredits();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieClick(int itemId) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra("MovieDetails", moviesList.get(itemId));
        startActivity(detailActivity);
    }

    private void showCredits() {
        int themeId = 2;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, themeId);
        builder.setView(this.getLayoutInflater().inflate(R.layout.credits_dialog, null));
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog creditsDialog = builder.create();
        creditsDialog.setCancelable(true);
        creditsDialog.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.order_by_key)) || key.equals(getString(R.string.movie_db_api_key))) {
            model.loadMovies(sharedPreferences.getString(getApplication().getString(R.string.order_by_key), getApplication().getString(R.string.order_by_default)),
                    sharedPreferences.getString(getApplication().getString(R.string.movie_db_api_key),"")) ;
        }
    }
}
