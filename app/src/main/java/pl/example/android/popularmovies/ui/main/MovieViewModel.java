package pl.example.android.popularmovies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import pl.example.android.popularmovies.database.MovieDao;
import pl.example.android.popularmovies.database.MovieDatabase;
import pl.example.android.popularmovies.model.Movie;
import pl.example.android.popularmovies.network.NetworkResponse;
import pl.example.android.popularmovies.network.NetworkUtils;
import pl.example.android.popularmovies.utilities.AppExecutors;

public class MovieViewModel extends ViewModel {
    private static final String LOG_TAG = MovieViewModel.class.getSimpleName();
    public enum Status {SUCCESS, NETWORK_ERROR, NETWORK_UNAUTHORIZED, LOADING}
    private final String FAVORITE_KEY = "favorite";
    private final MovieDao movieDao;
    private final String orderBy;
    private static LiveData<List<Movie>> favorites;
    private static MutableLiveData<List<Movie>> movies;
    private final static MutableLiveData<MovieViewModel.Status> status = new MutableLiveData<>();

    public MovieViewModel(MovieDatabase database, String orderBy, String movieDbApiKey){
        this.movieDao = database.movieDao();
        this.orderBy = orderBy;

        if(favorites == null){
            favorites = new MutableLiveData<>();
            favorites = movieDao.loadAllMovies();
        }
        if(movies == null){
            movies = new MutableLiveData<>();
            loadMovies(orderBy, movieDbApiKey);
        }
    }

    public LiveData<List<Movie>> getMovies() {
        if(orderBy.equals(FAVORITE_KEY)){
            return favorites;
        } else {
            return movies;
        }
    }

    public LiveData<Status> getStatus(){
        return status;
    }

    public void loadMovies(final String orderBy, final String movieDbApiKey) {
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