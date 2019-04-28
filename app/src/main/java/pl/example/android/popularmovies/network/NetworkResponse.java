package pl.example.android.popularmovies.network;

import java.util.List;

import pl.example.android.popularmovies.model.Movie;

public class NetworkResponse {
    private final List<Movie> movieList;
    private final Boolean isConnectionUnauthorized;

    NetworkResponse(List<Movie> movieList, Boolean isConnectionUnauthorized){
        this.movieList = movieList;
        this.isConnectionUnauthorized = isConnectionUnauthorized;
    }

    public List<Movie> getMovieList(){
        return movieList;
    }

    public Boolean getIsConnectionUnauthorized(){
        return isConnectionUnauthorized;
    }
}
