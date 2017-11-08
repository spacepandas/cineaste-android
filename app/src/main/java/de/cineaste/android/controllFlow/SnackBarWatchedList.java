package de.cineaste.android.controllFlow;

import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.adapter.WatchedlistAdapter;
import de.cineaste.android.entity.Movie;

public class SnackBarWatchedList extends BaseSnackBar {

    public SnackBarWatchedList(LinearLayoutManager linearLayoutManager, WatchedlistAdapter adapter, View view) {
        super(linearLayoutManager, adapter, view);
    }

    @Override
    public void getSnackBarLeftSwipe(final int position, final int message) {
        final Movie movieToBeDeleted = adapter.getItem(position);
        adapter.removeItem(position);

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
