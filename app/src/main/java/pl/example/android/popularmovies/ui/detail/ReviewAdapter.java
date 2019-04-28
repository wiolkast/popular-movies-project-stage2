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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<List<String>> reviewList;

    public ReviewAdapter(List<List<String>> reviewList) {
        this.reviewList = reviewList;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView reviewAuthorTv;
        final TextView reviewTv;

        private ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewAuthorTv = itemView.findViewById(R.id.review_author_tv);
            reviewTv = itemView.findViewById(R.id.review_tv);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder reviewViewHolder, int i) {
        List<String> currentReview = reviewList.get(i);
        reviewViewHolder.reviewAuthorTv.setText(currentReview.get(0));
        reviewViewHolder.reviewTv.setText(currentReview.get(1));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void updateData(List<List<String>> reviewList) {
        this.reviewList.clear();
        if (reviewList != null) {
            this.reviewList.addAll(reviewList);
        }
        notifyDataSetChanged();
    }
}