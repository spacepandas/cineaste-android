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

    private final List<NearbyMessage> nearbyMessages;
    private final int rowLayout;
    private final Context context;

    public NearbyUserAdapter(
            List<NearbyMessage> nearbyMessages,
            Context context ) {
        this.nearbyMessages = nearbyMessages;
        this.rowLayout = R.layout.card_nearby_user;
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
        private final TextView userName, movieCounter;

        public ViewHolder( final View itemView ) {
            super( itemView );
            userName = itemView.findViewById( R.id.userName_tv );
            movieCounter = itemView.findViewById( R.id.movie_counter_tv );
        }
    }
}
