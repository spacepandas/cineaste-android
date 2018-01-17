package de.cineaste.android.controllFlow.series;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.WatchState;


public class SeriesSnackBarHistory extends BaseSnackBar {

    private SeriesListAdapter adapter;

    SeriesSnackBarHistory(LinearLayoutManager linearLayoutManager, View view, SeriesListAdapter adapter) {
        super(linearLayoutManager, view);
        this.adapter = adapter;
    }

    @Override
    public void getSnackBarLeftSwipe(final int position) {
        final Series seriesToBeDeleted = adapter.getItem(position);
        adapter.removeItem(position);

        final Snackbar mySnackBar = Snackbar.make(view,
                R.string.series_deleted, Snackbar.LENGTH_LONG);
        mySnackBar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        mySnackBar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        adapter.restoreDeletedItem(seriesToBeDeleted, position);
                        int fist = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (fist >= position) {
                            linearLayoutManager.scrollToPosition(position);
                        }
                        break;
                }
            }
        });
        mySnackBar.show();
    }

    //todo  reset to current season end episode after dismiss update current status
    @Override
    public void getSnackBarRightSwipe(final int position, int message) {
        final Series seriesToBeUpdated = adapter.getItem(position);

        int currentSeason = seriesToBeUpdated.getCurrentNumberOfSeason();
        int currentEpisode = seriesToBeUpdated.getCurrentNumberOfEpisode();

        adapter.markEpisodes(seriesToBeUpdated, WatchState.WATCH_STATE);
        adapter.removeSeriesFromList(seriesToBeUpdated);
        adapter.toggleItemOnList(seriesToBeUpdated);
        final Snackbar mySnackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        mySnackBar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        mySnackBar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        adapter.restoreToggleItemOnList(seriesToBeUpdated, position);
                        int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (first >= position) {
                            linearLayoutManager.scrollToPosition(position);
                        }
                        break;
                }
            }

        });
        mySnackBar.show();
    }

}
