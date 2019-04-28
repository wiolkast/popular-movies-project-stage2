package pl.example.android.popularmovies.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.example.android.popularmovies.R;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{
    private final TrailerClickListener trailerClickListener;
    private final List<List<String>> trailerList;

    public TrailerAdapter(List<List<String>> trailerList, TrailerClickListener trailerClickListener){
        this.trailerList = trailerList;
        this.trailerClickListener = trailerClickListener;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView trailerTv;

        private TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerTv = itemView.findViewById(R.id.trailer_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            trailerClickListener.onTrailerClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int i) {
        String currentTrailer = trailerList.get(i).get(1);
        trailerViewHolder.trailerTv.setText(currentTrailer);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public void updateData(List<List<String>> trailerList) {
        this.trailerList.clear();
        if (trailerList != null) {
            this.trailerList.addAll(trailerList);
        }
        notifyDataSetChanged();
    }

    public interface TrailerClickListener{
        void onTrailerClick(int itemId);
    }
}
