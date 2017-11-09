package de.cineaste.android.controllFlow;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.adapter.MovieListAdapter;

/**
 * Created by marcelgross on 08.11.17.
 */

public abstract class BaseSnackBar {

    final LinearLayoutManager linearLayoutManager;
    final MovieListAdapter adapter;
    final View view;

    public BaseSnackBar(LinearLayoutManager linearLayoutManager, MovieListAdapter adapter, View view) {
        this.linearLayoutManager = linearLayoutManager;
        this.adapter = adapter;
        this.view = view;
    }

    public abstract void getSnackBarLeftSwipe(final int position, final int message);
    public abstract void getSnackBarRightSwipe(final int position, final int message);
}
