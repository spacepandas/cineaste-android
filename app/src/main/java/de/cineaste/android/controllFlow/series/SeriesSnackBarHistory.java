package de.cineaste.android.controllFlow.series;

import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;
import de.cineaste.android.entity.series.Series;


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

        final int currentSeason = seriesToBeDeleted.getCurrentNumberOfSeason();
        final int currentEpisode = seriesToBeDeleted.getCurrentNumberOfEpisode();

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
                        adapter.addDeletedItemToHistoryAgain(seriesToBeDeleted, position, currentSeason, currentEpisode);
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

    @Override
    public void getSnackBarRightSwipe(final int position, int message) {
        final Series seriesToBeUpdated = adapter.getItem(position);

        final int currentSeason = seriesToBeUpdated.getCurrentNumberOfSeason();
        final int currentEpisode = seriesToBeUpdated.getCurrentNumberOfEpisode();

        adapter.moveToWatchList(seriesToBeUpdated);
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
                        adapter.moveBackToHistory(seriesToBeUpdated, position, currentSeason, currentEpisode);

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
