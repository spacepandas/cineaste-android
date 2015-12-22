package de.cineaste.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.MatchingResult;
import de.cineaste.android.persistence.NearbyMessageHandler;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final NearbyMessageHandler handler;

    private final List<MatchingResult> results;
    private final int rowLayout;
    private final OnMovieSelectListener listener;

    public interface OnMovieSelectListener {
        void onMovieSelectListener( int position );
    }

    public ResultAdapter(
            List<MatchingResult> results,
            int rowLayout,
            OnMovieSelectListener listener ) {
        this.results = results;
        this.rowLayout = rowLayout;
        this.listener = listener;
        handler = NearbyMessageHandler.getInstance();
    }

    @Override
    public int getItemCount() {
        return results == null ? 0 : results.size();
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( rowLayout, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position ) {
        holder.assignData( results.get( position ), handler.getSize() );
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView title, counter;
        public final ImageButton watchedButton;

        public ViewHolder( final View itemView ) {
            super( itemView );
            title = (TextView) itemView.findViewById( R.id.movie_title_tv );
            counter = (TextView) itemView.findViewById( R.id.movie_counter_tv );
            watchedButton = (ImageButton) itemView.findViewById( R.id.watched_button );
        }

        public void assignData( MatchingResult matchingResult, int resultCounter ) {
            watchedButton.setOnClickListener( this );
            title.setText( matchingResult.getTitle() );
            counter.setText(
                    String.format( "%d/%d", matchingResult.getCounter(), resultCounter )
            );
        }

        @Override
        public void onClick( View v ) {
            int position = getAdapterPosition();
            if( listener != null )
                listener.onMovieSelectListener( position );
            results.remove( position );
            notifyItemRemoved( position );
        }
    }
}
