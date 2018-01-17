package de.cineaste.android.controllFlow.series;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.WatchState;


public class SeriesSnackBarWatchList extends BaseSnackBar {

    private SeriesListAdapter adapter;
    private Context context;

    SeriesSnackBarWatchList(LinearLayoutManager linearLayoutManager, View view, SeriesListAdapter adapter, Context context) {
        super(linearLayoutManager, view);
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public void getSnackBarLeftSwipe(final int position) {
        final Series seriesToBeDeleted = adapter.getItem(position);
        adapter.removeItem(position);

        final Snackbar mySnackbar = Snackbar.make(view,
                R.string.series_deleted, Snackbar.LENGTH_LONG);
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
                        adapter.restoreDeletedItem(seriesToBeDeleted, position);
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
        final Series seriesToBeUpdated = adapter.getItem(position);

        showDialogIfNeeded(seriesToBeUpdated, position, message);

    }

    private void showDialogIfNeeded(final Series series, final int position, final int message) {

        adapter.removeSeriesFromList(series);
        if (series.isInProduction()) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle(context.getString(R.string.seriesSeenHeadline, series.getName()));
            alertBuilder.setMessage(R.string.seriesStillInProduction);
            alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateSeriesAndCreateSnackbar(position, message, series);
                }
            });
            alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.addSeriesToList(series, position);
                }
            });

            alertBuilder.create().show();
        } else {
            updateSeriesAndCreateSnackbar(position, message, series);
        }
    }
    //todo  reset to current season end episode after dismiss update current status
    private void updateSeriesAndCreateSnackbar(final int position, int message, final Series seriesToBeUpdated) {

        int currentSeason = seriesToBeUpdated.getCurrentNumberOfSeason();
        int currentEpisode = seriesToBeUpdated.getCurrentNumberOfEpisode();

        adapter.markEpisodes(seriesToBeUpdated, WatchState.WATCHED_STATE);

        adapter.toggleItemOnList(seriesToBeUpdated);
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
                        adapter.restoreToggleItemOnList(seriesToBeUpdated, position);
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
