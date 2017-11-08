package de.cineaste.android.controllFlow;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.adapter.BaseWatchlistAdapter;

/**
 * Created by marcelgross on 08.11.17.
 */

public abstract class BaseSnackBar {

    final LinearLayoutManager linearLayoutManager;
    final BaseWatchlistAdapter adapter;
    final View view;

    public BaseSnackBar(LinearLayoutManager linearLayoutManager, BaseWatchlistAdapter adapter, View view) {
        this.linearLayoutManager = linearLayoutManager;
        this.adapter = adapter;
        this.view = view;
    }

    public abstract void getSnackBarLeftSwipe(final int position, final int message);
    public abstract void getSnackBarRightSwipe(final int position, final int message);
}
