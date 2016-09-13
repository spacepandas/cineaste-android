package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;

public class BodyViewHolder extends RecyclerView.ViewHolder {
    private final TextView movieDescription;
    private final Context context;

    public BodyViewHolder(View v, Context context) {
        super( v );
        this.context = context;
        movieDescription = (TextView) v.findViewById( R.id.movie_description );
    }

    public void assignData( final Movie movie ) {
        Resources resources = context.getResources();
        String description = movie.getDescription();
        movieDescription.setText(
                (description == null || description.isEmpty())
                        ? resources.getString( R.string.noDescription ) : description );
    }
}