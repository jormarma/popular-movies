package com.android.example.popularmovies.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.example.popularmovies.R;
import com.android.example.popularmovies.listeners.ListItemClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.view.LayoutInflater.from;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private List<String> mMoviePosterUrlList;
    private Context mContext;

    public MovieListAdapter(List<String> moviePosterUrlList, Context context) {
        mMoviePosterUrlList = moviePosterUrlList != null ? moviePosterUrlList : new ArrayList<String>();
        mContext = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageView = from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        RequestCreator requestCreator = Picasso.with(mContext)
                .load(mMoviePosterUrlList.get(position));
        requestCreator = resize(requestCreator);
        requestCreator.placeholder(R.drawable.loading)
                .error(R.drawable.forbidden)
                .into(holder.mMoviePoster);

        holder.mMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListItemClickListener listener = (ListItemClickListener)mContext;
                listener.onItemClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviePosterUrlList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public ImageView mMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mMoviePoster = itemView.findViewById(R.id.iv_movie_item);
        }

    }

    private RequestCreator resize(RequestCreator requestCreator) {
        if (isLandscape()) {
            float realRatio = getRealRatio();

            int targetWidth = (int) (500 * realRatio);
            int targetHeight = (int) (750 * realRatio);

            return requestCreator.resize(targetWidth, targetHeight);
        }

        int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        int targetWidth = widthPixels / 2;
        int targetHeight  = (int) (750 * targetWidth / 500.0);

        return requestCreator.resize(targetWidth, targetHeight);
    }

    private boolean isLandscape() {
        return mContext.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    }

    private float getRealRatio() {
        Resources resources = mContext.getResources();

        int screenHeightDp = resources.getConfiguration().screenHeightDp;
        float density = resources.getDisplayMetrics().density;
        int height = (int) (((AppCompatActivity) mContext).getSupportActionBar().getHeight() / density);
        float indps = 750 / density;
        return (screenHeightDp - height) / indps;
    }

}
