package pl.example.android.popularmovies.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pl.example.android.popularmovies.R;
import pl.example.android.popularmovies.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movieList;
    private final Context context;
    private final MovieClickListener movieClickListener;

    public MovieAdapter(Context context, List<Movie> movieList, MovieClickListener movieClickListener){
        this.context = context;
        this.movieList = movieList;
        this.movieClickListener = movieClickListener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView posterIv;
        private MovieViewHolder(View itemView){
            super(itemView);
            posterIv = itemView.findViewById(R.id.poster_iv);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            movieClickListener.onMovieClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        Movie currentMovie = movieList.get(i);
        Picasso.with(context).load(currentMovie.getMoviePosterLarge())
                .into(movieViewHolder.posterIv);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateData(List<Movie> movieList) {
        this.movieList.clear();
        if (movieList != null) {
            this.movieList.addAll(movieList);
        }
        notifyDataSetChanged();
    }

    public interface MovieClickListener{
        void onMovieClick(int itemId);
    }

}
