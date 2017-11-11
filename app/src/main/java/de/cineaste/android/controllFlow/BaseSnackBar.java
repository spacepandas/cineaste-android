package de.cineaste.android.controllFlow;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.adapter.MovieListAdapter;


abstract class BaseSnackBar {

    final LinearLayoutManager linearLayoutManager;
    final MovieListAdapter adapter;
    final View view;

    BaseSnackBar(LinearLayoutManager linearLayoutManager, MovieListAdapter adapter, View view) {
        this.linearLayoutManager = linearLayoutManager;
        this.adapter = adapter;
        this.view = view;
    }

    public abstract void getSnackBarLeftSwipe(final int position);
    public abstract void getSnackBarRightSwipe(final int position, final int message);
}
