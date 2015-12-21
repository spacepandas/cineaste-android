package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.MatchingResult;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;
import de.cineaste.android.persistence.MovieDbHelper;
import de.cineaste.android.persistence.NearbyMessageHandler;

/**
 * Created by marcelgross on 21.12.15.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private NearbyMessageHandler handler;

    private List<MatchingResult> results;
    private int rowLayout;
    private Context context;

    public ResultAdapter(List<MatchingResult> results, int rowLayout, Context context) {
        this.results = results;
        this.rowLayout = rowLayout;
        this.context = context;
        handler = NearbyMessageHandler.getInstance();
    }

    @Override
    public int getItemCount() {
        return results == null ? 0 : results.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from( parent.getContext() ).inflate(rowLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MatchingResult result = results.get(position);
        holder.title.setText(result.getTitle());
        holder.counter.setText( String.format( "%d/%d", result.getCounter(), handler.getSize() ) );

        holder.watchedButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                TheMovieDb theMovieDb = new TheMovieDb( context );

                theMovieDb.fetchMovie( result.getId(), new TheMovieDb.OnFetchMovieResultListener() {
                    @Override
                    public void onFetchMovieResultListener( Movie movie ) {
                        MovieDbHelper db = MovieDbHelper.getInstance( context );
                        db.createOrUpdate( movie );
                        int pos = results.indexOf( result );
                        results.remove( pos );
                        notifyItemRemoved( pos );
                    }
                } );
            }
        } );

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, counter;
        public ImageButton watchedButton;

        public ViewHolder(final View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById( R.id.movie_title_tv);
            counter = (TextView) itemView.findViewById(R.id.movie_counter_tv);
            watchedButton = (ImageButton) itemView.findViewById( R.id.watched_button );

        }
    }
}
