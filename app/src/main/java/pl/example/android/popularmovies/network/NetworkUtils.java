package pl.example.android.popularmovies.network;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pl.example.android.popularmovies.model.Movie;

public class NetworkUtils {
    final private static String LOG_TAG = NetworkUtils.class.getSimpleName();
    final private static String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final private static String TRAILERS_PATH = "videos";
    final private static String REVIEWS_PATH = "reviews";
    final private static String PARAM_API_KEY = "api_key";
    final private static String JSON_RESULTS = "results";
    final private static String JSON_ID = "id";
    final private static String JSON_TITLE = "title";
    final private static String JSON_RELEASE_DATE = "release_date";
    final private static String JSON_VOTE_AVERAGE = "vote_average";
    final private static String JSON_PLOT_SYNOPSIS = "overview";
    final private static String JSON_POSTER_PATH = "poster_path";
    final private static String JSON_TRAILER_KEY = "key";
    final private static String JSON_TRAILER_NAME = "name";
    final private static String JSON_REVIEW_AUTHOR = "author";
    final private static String JSON_REVIEW_CONTENT = "content";
    final private static String POSTER_PATH_BASE = "https://image.tmdb.org/t/p/";
    final private static String POSTER_SIZE_SMALL = "w185";
    final private static String POSTER_SIZE_LARGE = "w342";
    private static Boolean isConnectionUnauthorized = false;

    private static URL buildMoviesUrl(String sortBy, String movieDbApiKey) {
        Uri buildUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, movieDbApiKey)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the movies URL ", e);
        }
        return url;
    }

    private static URL buildTrailersAndReviewUrl(int movieId, String movieDbApiKey, String path) {
        Uri buildUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(path)
                .appendQueryParameter(PARAM_API_KEY, movieDbApiKey)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the trailers or reviews URL ", e);
        }
        return url;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        String response = null;
        if (url == null) {
            return null;
        }
        InputStream inputStream = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {
            inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                response = scanner.next();
            }
        } else if (responseCode == 401) {
            Log.e(LOG_TAG, "Unauthorized connection: check your movie database API key");
            isConnectionUnauthorized = true;
        } else {
            Log.e(LOG_TAG, "Error response code: " + responseCode);
        }
        urlConnection.disconnect();
        if (inputStream != null) {
            inputStream.close();
        }
        return response;
    }

    private static List<Movie> extractMoviesListFromJson(String moviesJSON) {
        if (moviesJSON == null) {
            return null;
        }
        List<Movie> moviesList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);
            JSONArray moviesArray = baseJsonResponse.getJSONArray(JSON_RESULTS);
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject currentMovie = moviesArray.getJSONObject(i);
                int movieId = currentMovie.getInt(JSON_ID);
                String movieTitle = currentMovie.getString(JSON_TITLE);
                String movieReleaseDate = currentMovie.getString(JSON_RELEASE_DATE);
                Double voteAverage = currentMovie.getDouble(JSON_VOTE_AVERAGE);
                String plotSynopsis = currentMovie.getString(JSON_PLOT_SYNOPSIS);
                String moviePosterSmall = POSTER_PATH_BASE + POSTER_SIZE_SMALL + currentMovie.getString(JSON_POSTER_PATH);
                String moviePosterLarge = POSTER_PATH_BASE + POSTER_SIZE_LARGE + currentMovie.getString(JSON_POSTER_PATH);
                Movie movie = new Movie(movieId, movieTitle, movieReleaseDate, voteAverage,
                        plotSynopsis, moviePosterSmall, moviePosterLarge);
                moviesList.add(movie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing movies JSON. ", e);
        }
        return moviesList;
    }

    private static List<List<String>> extractTrailerListFromJson(String trailersJSON) {
        if (trailersJSON == null) {
            return null;
        }
        List<List<String>> trailerList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(trailersJSON);
            JSONArray trailersArray = baseJsonResponse.getJSONArray(JSON_RESULTS);
            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject currentTrailer = trailersArray.getJSONObject(i);
                List<String> trailer = new ArrayList<>();
                trailer.add(currentTrailer.getString(JSON_TRAILER_KEY));
                trailer.add(currentTrailer.getString(JSON_TRAILER_NAME));
                trailerList.add(trailer);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing trailer JSON. ", e);
        }
        return trailerList;
    }

    private static List<List<String>> extractReviewListFromJson(String reviewsJSON) {
        if (reviewsJSON == null) {
            return null;
        }
        List<List<String>> reviewList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(reviewsJSON);
            JSONArray reviewsArray = baseJsonResponse.getJSONArray(JSON_RESULTS);
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject currentReview = reviewsArray.getJSONObject(i);
                List<String> review = new ArrayList<>();
                review.add(currentReview.getString(JSON_REVIEW_AUTHOR));
                review.add(currentReview.getString(JSON_REVIEW_CONTENT));
                reviewList.add(review);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing review JSON. ", e);
        }
        return reviewList;
    }

    public static NetworkResponse getMovies(String orderBy, String movieDbApiKey) {
        URL url = buildMoviesUrl(orderBy, movieDbApiKey);
        List<Movie> moviesList = null;
        try {
            moviesList = extractMoviesListFromJson(getResponseFromHttpUrl(url));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving movies JSON. ", e);
            return new NetworkResponse(null, false);
        }
        return new NetworkResponse(moviesList, isConnectionUnauthorized);
    }

    public static List<List<String>> getTrailers(int movieId, String movieDbApiKey) {
        URL url = buildTrailersAndReviewUrl(movieId, movieDbApiKey, TRAILERS_PATH);
        List<List<String>> trailerList = null;
        try {
            trailerList = extractTrailerListFromJson(getResponseFromHttpUrl(url));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving trailer JSON. ", e);
        }
        return trailerList;
    }

    public static List<List<String>> getReviews(int movieId, String movieDbApiKey) {
        URL url = buildTrailersAndReviewUrl(movieId, movieDbApiKey, REVIEWS_PATH);
        List<List<String>> reviewList = null;
        try {
            reviewList = extractReviewListFromJson(getResponseFromHttpUrl(url));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving review JSON. ", e);
        }
        return reviewList;
    }
}
