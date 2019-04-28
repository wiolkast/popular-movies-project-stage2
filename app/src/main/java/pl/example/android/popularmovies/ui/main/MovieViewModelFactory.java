package pl.example.android.popularmovies.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import pl.example.android.popularmovies.database.MovieDatabase;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MovieDatabase database;
    private final String orderBy;
    private final String movieDbApiKey;

     public MovieViewModelFactory(MovieDatabase database, String orderBy, String movieDbApiKey){
         this.database = database;
         this.orderBy = orderBy;
         this.movieDbApiKey = movieDbApiKey;
     }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieViewModel(database, orderBy, movieDbApiKey);
    }

}
