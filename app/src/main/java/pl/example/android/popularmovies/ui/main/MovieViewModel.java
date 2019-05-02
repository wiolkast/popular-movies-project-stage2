package pl.example.android.popularmovies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import pl.example.android.popularmovies.database.MovieDatabase;
import pl.example.android.popularmovies.model.Movie;
import pl.example.android.popularmovies.network.NetworkResponse;
import pl.example.android.popularmovies.network.NetworkUtils;
import pl.example.android.popularmovies.utilities.AppExecutors;

public class MovieViewModel extends ViewModel {
    private static final String LOG_TAG = MovieViewModel.class.getSimpleName();

    public enum Status {SUCCESS, NETWORK_ERROR, NETWORK_UNAUTHORIZED, LOADING}

    private final String FAVORITE_KEY = "favorite";
    private static LiveData<List<Movie>> favorites;
    private static MutableLiveData<List<Movie>> movies;
    private final static MutableLiveData<MovieViewModel.Status> status = new MutableLiveData<>();
    private static MediatorLiveData<List<Movie>> mediatorLiveData;
    private static String orderByPrevious;

    public MovieViewModel(MovieDatabase database, String orderBy, String movieDbApiKey) {
        if (mediatorLiveData == null) {
            mediatorLiveData = new MediatorLiveData<>();
        }
        if (favorites == null) {
            favorites = new MutableLiveData<>();
            favorites = database.movieDao().loadAllMovies();
        }
        if (movies == null) {
            movies = new MutableLiveData<>();
            loadMovies(orderBy, movieDbApiKey);
        }
    }

    private Boolean isSwitchDataSourceNeeded(String orderBy){
        return (orderByPrevious == null || (!orderBy.equals(orderByPrevious)) &&
                (orderBy.equals(FAVORITE_KEY) || orderByPrevious.equals(FAVORITE_KEY)));
    }

    private void switchDataSource(String orderBy){
        if(orderBy.equals(FAVORITE_KEY)){
            mediatorLiveData.removeSource(movies);
            mediatorLiveData.addSource(favorites, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    mediatorLiveData.setValue(movies);
                }
            });
        } else {
            mediatorLiveData.removeSource(favorites);
            mediatorLiveData.addSource(movies, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    mediatorLiveData.setValue(movies);
                }
            });
        }
        orderByPrevious = orderBy;
    }

    public LiveData<List<Movie>> getMovies() {
        return mediatorLiveData;
    }

    public LiveData<Status> getStatus() {
        return status;
    }

    public void loadMovies(final String orderBy, final String movieDbApiKey) {
        if(isSwitchDataSourceNeeded(orderBy)){
            switchDataSource(orderBy);
        }
        if (!orderBy.equals(FAVORITE_KEY)) {
            status.setValue(Status.LOADING);
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    NetworkResponse networkResponse;
                    networkResponse = NetworkUtils.getMovies(orderBy, movieDbApiKey);
                    if (networkResponse.getMovieList() != null) {
                        status.postValue(Status.SUCCESS);
                        Log.d(LOG_TAG, "Loaded data from the movie database");
                        movies.postValue(networkResponse.getMovieList());
                    } else if (networkResponse.getIsConnectionUnauthorized()) {
                        status.postValue(Status.NETWORK_UNAUTHORIZED);
                        Log.d(LOG_TAG, "Network connection is unauthorized");
                    } else {
                        status.postValue(Status.NETWORK_ERROR);
                        Log.d(LOG_TAG, "Network error");
                    }
                }
            });
        } else {
            status.setValue(Status.SUCCESS);
        }
    }
}