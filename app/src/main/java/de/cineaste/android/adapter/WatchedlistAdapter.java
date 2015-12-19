package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.persistence.MovieDbHelper;

public class WatchedlistAdapter extends RecyclerView.Adapter<WatchedlistAdapter.ViewHolder> implements Observer {

    private List<Movie> mDataset;
    private MovieDbHelper mDb;

    public WatchedlistAdapter(Context context) {
        this.mDb = MovieDbHelper.getInstance( context );
        mDb.addObserver(this);
        mDataset = mDb.readMoviesByWatchStatus(true);
    }

    @Override
    public void update(Observable observable, Object data) {
        mDataset = mDb.readMoviesByWatchStatus(true);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mMovieTitle;
        public ImageButton mRemoveMovie;
        public Movie mCurrentMovie;

        public ViewHolder(View v){
            super(v);
            mMovieTitle = (TextView)v.findViewById( R.id.movie_title);
            mRemoveMovie = (ImageButton)v.findViewById(R.id.remove_button);
        }
    }

    public WatchedlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchedlist_cardview,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WatchedlistAdapter.ViewHolder holder, final int position) {
        String movieTitle = mDataset.get(position).getTitle();
        holder.mCurrentMovie = mDataset.get(position);
        holder.mMovieTitle.setText(movieTitle);

        holder.mRemoveMovie.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int index = mDataset.indexOf(holder.mCurrentMovie);
                removeMovie(index, holder.mCurrentMovie.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void removeMovie(int index, long dbId){
        mDb.deleteMovieFromWatchlist(dbId);
        mDataset.remove(index);
        notifyItemRemoved(index);
    }
}
