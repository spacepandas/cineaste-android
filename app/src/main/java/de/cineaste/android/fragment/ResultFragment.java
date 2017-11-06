package de.cineaste.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.MultiList;
import de.cineaste.android.R;
import de.cineaste.android.adapter.ResultAdapter;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.database.NearbyMessageHandler;
import de.cineaste.android.entity.MatchingResult;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.entity.MovieDto;
import de.cineaste.android.entity.NearbyMessage;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

public class ResultFragment extends Fragment implements ResultAdapter.OnMovieSelectListener {

	private List<NearbyMessage> nearbyMessages;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_result, container, false);

		NearbyMessageHandler handler = NearbyMessageHandler.getInstance();
		nearbyMessages = handler.getMessages();

		RecyclerView result = view.findViewById(R.id.result_list);

		final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		result.setLayoutManager(llm);
		result.setItemAnimator(new DefaultItemAnimator());

		ResultAdapter resultAdapter = new ResultAdapter(
				getResult(),
				this);
		result.setAdapter(resultAdapter);

		return view;
	}

	@Override
	public void onMovieSelectListener(int position) {

		NetworkClient client = new NetworkClient(new NetworkRequest().get(getResult().get(position).getId()));
		client.sendRequest(new NetworkCallback() {
			@Override
			public void onFailure() {

			}

			@Override
			public void onSuccess(NetworkResponse response) {
				Gson gson = new Gson();
				final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MovieDbHelper db = MovieDbHelper.getInstance(getActivity());
						movie.setWatched(true);
						if (db.readMovie(movie.getId()) != null) {
							db.update(movie);
						} else {
							db.createOrUpdate(movie);
						}
					}
				});
			}
		});

		getFragmentManager().popBackStack();
	}

	private ArrayList<MovieDto> getMovies() {
		ArrayList<MovieDto> movies = new ArrayList<>();

		for (NearbyMessage current : nearbyMessages) {
			movies.addAll(current.getMovies());
		}

		return movies;
	}

	private ArrayList<MatchingResult> getResult() {
		ArrayList<MatchingResult> results = new ArrayList<>();
		MultiList multiList = new MultiList();
		multiList.addAll(getMovies());

		for (MultiList.MultiListEntry multiListEntry : multiList.getMovieList()) {
			results.add(new MatchingResult(multiListEntry.getMovieDto(), multiListEntry.getCounter()));
		}

		return results;
	}
}

