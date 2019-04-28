package pl.example.android.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Movie implements Parcelable {
    @PrimaryKey
    private final int movieId;
    private final String movieTitle;
    private final String movieReleaseDate;
    private final Double voteAverage;
    private final String plotSynopsis;
    private final String moviePosterSmall;
    private final String moviePosterLarge;

    public Movie(int movieId, String movieTitle, String movieReleaseDate, Double voteAverage,
                 String plotSynopsis, String moviePosterSmall, String moviePosterLarge){
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        this.moviePosterSmall = moviePosterSmall;
        this.moviePosterLarge = moviePosterLarge;
    }

    @Ignore
    private Movie(Parcel parcel){
        movieId = parcel.readInt();
        movieTitle = parcel.readString();
        movieReleaseDate = parcel.readString();
        voteAverage = parcel.readDouble();
        plotSynopsis = parcel.readString();
        moviePosterSmall = parcel.readString();
        moviePosterLarge = parcel.readString();
    }

    public int getMovieId(){
        return this.movieId;
    }

    public String getMovieTitle() {
        return this.movieTitle;
    }

    public String getMovieReleaseDate() {
        return this.movieReleaseDate;
    }

    public Double getVoteAverage(){
        return this.voteAverage;
    }

    public String getPlotSynopsis(){
        return this.plotSynopsis;
    }

    public String getMoviePosterSmall() {
        return this.moviePosterSmall;
    }

    public String getMoviePosterLarge() {
        return this.moviePosterLarge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(movieId);
        parcel.writeString(movieTitle);
        parcel.writeString(movieReleaseDate);
        parcel.writeDouble(voteAverage);
        parcel.writeString(plotSynopsis);
        parcel.writeString(moviePosterSmall);
        parcel.writeString(moviePosterLarge);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };
}