package de.cineaste.android.controllFlow.movie;

import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.adapter.movie.MovieListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;
import de.cineaste.android.entity.movie.Movie;

public class MovieSnackBarHistory extends BaseSnackBar {

    private MovieListAdapter adapter;

    MovieSnackBarHistory(LinearLayoutManager linearLayoutManager, MovieListAdapter adapter, View view) {
        super(linearLayoutManager, view);
        this.adapter = adapter;
    }

    @Override
    public void getSnackBarLeftSwipe(final int position) {
        final Movie movieToBeDeleted = adapter.getItem(position);
        adapter.removeItem(position);

        final Snackbar mySnackbar = Snackbar.make(view,
                R.string.movie_deleted, Snackbar.LENGTH_LONG);
        mySnackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        mySnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        adapter.restoreDeletedItem(movieToBeDeleted, position);
                        int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (first >= position) {
                            linearLayoutManager.scrollToPosition(position);
                        }
                        break;
                }
            }

        });
        mySnackbar.show();
    }

    @Override
    public void getSnackBarRightSwipe(final int position, final int message) {
        final Movie movieToBeUpdated = adapter.getItem(position);
        adapter.toggleItemOnList(movieToBeUpdated);

        final Snackbar mySnackbar = Snackbar.make(view,
                message, Snackbar.LENGTH_LONG);
        mySnackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        mySnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        adapter.restoreToggleItemOnList(movieToBeUpdated, position);
                        int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (first >= position) {
                            linearLayoutManager.scrollToPosition(position);
                        }
                        break;
                }
            }

        });
        mySnackbar.show();
    }
}
