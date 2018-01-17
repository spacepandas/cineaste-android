package de.cineaste.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.cineaste.android.R;

import static de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.EPISODES_COUNT;
import static de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.MOVIE_COUNT;
import static de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.SERIES_COUNT;

public class ImportFinishedDialogFragment extends DialogFragment {

    public interface BundleKeyWords {
        String MOVIE_COUNT = "movieCount";
        String SERIES_COUNT = "seriesCount";
        String EPISODES_COUNT = "episodeCount";
    }

    private TextView movies;
    private TextView series;
    private TextView episodes;

    private int movieCount;
    private int seriesCount;
    private int episodeCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            movieCount = bundle.getInt(MOVIE_COUNT, -1);
            seriesCount = bundle.getInt(SERIES_COUNT, -1);
            episodeCount = bundle.getInt(EPISODES_COUNT, -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_finised_dialog, container, false);

        Button button = view.findViewById(R.id.ok);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        movies = view.findViewById(R.id.movie);
        series = view.findViewById(R.id.series);
        episodes = view.findViewById(R.id.episodes);

        fillTextViews();

        getDialog().setCancelable(false);

        return view;
    }

    private void fillTextViews() {
        if (movieCount < 0) {
            movies.setText(R.string.importedMoviesFailed);
        } else {
            movies.setText(getString(R.string.importedMovies, String.valueOf(movieCount)));
        }
        if (seriesCount < 0) {
            series.setText(R.string.importedSeriesFailed);
        } else {
            series.setText(getString(R.string.importedSeries, String.valueOf(seriesCount)));
        }
        if (episodeCount < 0) {
            episodes.setText(R.string.importedEpisodesFailed);
        } else {
            episodes.setText(getString(R.string.importedEpisodes, String.valueOf(episodeCount)));
        }
    }
}
