package pl.example.android.popularmovies.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import pl.example.android.popularmovies.database.MovieDao;
import pl.example.android.popularmovies.database.MovieDatabase;
import pl.example.android.popularmovies.model.Movie;
import pl.example.android.popularmovies.network.NetworkUtils;
import pl.example.android.popularmovies.utilities.AppExecutors;

public class MovieDetailViewModel extends ViewModel {
    private final MovieDao movieDao;
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private MutableLiveData<List<List<String>>> trailers;
    private MutableLiveData<List<List<String>>> reviews;

    public MovieDetailViewModel(MovieDatabase database, final int movieId, final String movieDbApiKey) {
        this.movieDao = database.movieDao();
        checkIsFavorite(movieId);
        if (trailers == null) {
            trailers = new MutableLiveData<>();
            loadTrailers(movieId, movieDbApiKey);
        }
        if (reviews == null){
            reviews = new MutableLiveData<>();
            loadReviews(movieId, movieDbApiKey);
        }
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    private void checkIsFavorite(final int movieId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                isFavorite.postValue((movieDao.checkMovie(movieId) != null));
            }
        });
    }

    public LiveData<List<List<String>>> getTrailers() {
        return trailers;
    }

    public LiveData<List<List<String>>> getReviews() {
        return reviews;
    }

    private void loadTrailers(final int movieId, final String movieDbApiKey) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                List<List<String>> trailerList = NetworkUtils.getTrailers(movieId, movieDbApiKey);
                trailers.postValue(trailerList);
            }
        });
    }

    private void loadReviews(final int movieId, final String movieDbApiKey) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                List<List<String>> reviewList = NetworkUtils.getReviews(movieId, movieDbApiKey);
                reviews.postValue(reviewList);
            }
        });
    }
    public void insertMovie(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.insertMovie(movie);
                isFavorite.postValue(true);
            }
        });
    }

    public void deleteMovie(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.deleteMovie(movie);
                isFavorite.postValue(false);
            }
        });
    }
}
