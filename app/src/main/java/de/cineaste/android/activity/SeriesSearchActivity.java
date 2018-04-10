package de.cineaste.android.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesSearchQueryAdapter;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.SeriesCallback;
import de.cineaste.android.network.SeriesLoader;

public class SeriesSearchActivity extends AbstractSearchActivity implements SeriesSearchQueryAdapter.OnSeriesStateChange {

    private SeriesSearchQueryAdapter seriesQueryAdapter;

    @Override
    @NonNull
    protected Intent getIntentForDetailActivity(long itemId) {
        Intent intent = new Intent(this, SeriesDetailActivity.class);
        intent.putExtra(BaseDao.SeriesEntry.ID, itemId);
        intent.putExtra(this.getString(R.string.state), R.string.searchState);
        return intent;
    }

    @Override
    public void onSeriesStateChangeListener(final Series series, int viewId, final int index) {
        final SeriesDbHelper dbHelper = SeriesDbHelper.Companion.getInstance(this);
        SeriesCallback seriesCallback;
        switch (viewId) {
            case R.id.to_watchlist_button:
                seriesCallback = new SeriesCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seriesAddError(series, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(Series series) {
                        dbHelper.addToWatchList(series);
                    }
                };
                break;
            case R.id.history_button:

                seriesCallback = new SeriesCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seriesAddError(series, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(Series series) {
                        dbHelper.addToHistory(series);

                    }
                };
                break;
                default:
                    seriesCallback = null;
                    break;
        }
        if (seriesCallback != null) {
            seriesQueryAdapter.removeSerie(index);

            new SeriesLoader(this).loadCompleteSeries(series.getId(), seriesCallback);
        }
    }

    private void seriesAddError(Series series, int index) {
        Snackbar snackbar = Snackbar
                .make(recyclerView, R.string.could_not_add_movie, Snackbar.LENGTH_LONG);
        snackbar.show();
        seriesQueryAdapter.addSerie(series, index);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_series_search;
    }

    @Override
    protected void initAdapter() {
        seriesQueryAdapter = new SeriesSearchQueryAdapter(this, this);
    }

    @Override
    protected RecyclerView.Adapter getListAdapter() {
        return seriesQueryAdapter;
    }

    @Override
    protected void getSuggestions() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getPopularSeries());
        client.sendRequest(getNetworkCallback());
    }

    @Override
    protected void searchRequest(String searchQuery) {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).searchSeries(searchQuery));
        client.sendRequest(getNetworkCallback());
    }

    @Override
    protected Type getListType() {
        return new TypeToken<List<Series>>() {
        }.getType();
    }

    @Override
    @NonNull
    protected Runnable getRunnable(final String json, final Type listType) {
        return new Runnable() {
            @Override
            public void run() {
                final List<Series> series = gson.fromJson(json, listType);
                seriesQueryAdapter.addSeries(series);
                progressBar.setVisibility(View.GONE);
            }
        };
    }
}
