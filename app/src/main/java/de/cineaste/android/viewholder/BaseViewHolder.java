package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.R;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.util.Constants;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    protected final TextView title;
    protected final Resources resources;
    protected final ItemClickListener listener;
    protected final View view;
    protected final ImageView poster;
    private final Context context;

    public BaseViewHolder(View itemView, ItemClickListener listener, Context context) {
        super(itemView);
        this.context = context;
        this.listener = listener;
        this.resources = context.getResources();
        this.title = itemView.findViewById(R.id.title);
        this.poster = itemView.findViewById(R.id.poster_image_view);
        this.view = itemView;
    }

    protected void setPoster(String posterName) {
        String posterUri =
                Constants.POSTER_URI_SMALL
                        .replace("<posterName>", posterName != null ? posterName : "/")
                        .replace("<API_KEY>", context.getString(R.string.movieKey));
        Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(poster);
    }

    protected String convertDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", resources.getConfiguration().locale);
        return simpleDateFormat.format(date);
    }
}
