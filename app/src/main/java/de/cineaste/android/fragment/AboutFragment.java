package de.cineaste.android.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.cineaste.android.R;

public class AboutFragment extends Fragment implements View.OnClickListener {


	private static final String GITHUB_URL = "https://github.com/marcelgross90/Cineaste";
	private static final String MOVIE_DB_URL = "https://www.themoviedb.org/";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);

		ImageView githubLogo = (ImageView) view.findViewById(R.id.github_logo);
		ImageView movieDbLogo = (ImageView) view.findViewById(R.id.themoviedb_logo);

		githubLogo.setOnClickListener(this);
		movieDbLogo.setOnClickListener(this);
		return view;
	}

	private void openWebsite(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		getActivity().startActivity(intent);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.github_logo:
				openWebsite(GITHUB_URL);
				break;
			case R.id.themoviedb_logo:
				openWebsite(MOVIE_DB_URL);
				break;
		}
	}
}
