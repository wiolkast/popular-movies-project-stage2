package pl.example.android.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import pl.example.android.popularmovies.database.MovieDatabase;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MovieDatabase database;
    private final int movieId;
    private final String movieDbApiKey;

    public MovieDetailViewModelFactory(MovieDatabase database, int movieId, String movieDbApiKey){
        this.database = database;
        this.movieId = movieId;
        this.movieDbApiKey = movieDbApiKey;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailViewModel(database, movieId, movieDbApiKey);
    }

}