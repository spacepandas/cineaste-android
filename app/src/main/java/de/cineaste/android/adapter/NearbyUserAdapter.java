package de.cineaste.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.NearbyMessage;

public class NearbyUserAdapter extends RecyclerView.Adapter<NearbyUserAdapter.ViewHolder> {

    private List<NearbyMessage> nearbyMessages;
    private int rowLayout;
    private Context context;

    public NearbyUserAdapter(
            List<NearbyMessage> nearbyMessages,
            int rowLayout,
            Context context ) {
        this.nearbyMessages = nearbyMessages;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return nearbyMessages == null ? 0 : nearbyMessages.size();
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( rowLayout, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        final NearbyMessage nearbyMessage = nearbyMessages.get( position );
        holder.userName.setText( nearbyMessage.getUserName() );
        Resources resources = context.getResources();
        int count = nearbyMessage.getMovies().size();
        holder.movieCounter.setText( resources.getQuantityString( R.plurals.movieCounter, count, count ) );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, movieCounter;

        public ViewHolder( final View itemView ) {
            super( itemView );
            userName = (TextView) itemView.findViewById( R.id.userName_tv );
            movieCounter = (TextView) itemView.findViewById( R.id.movie_counter_tv );
        }
    }
}
