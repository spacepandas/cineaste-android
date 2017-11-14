package de.cineaste.android.network;

import android.content.res.Resources;

import java.util.Locale;

import de.cineaste.android.R;
import okhttp3.Request;

public class NetworkRequest {

	private final String baseUrl = "https://api.themoviedb.org/3";

	private final String staticQueryParams;

	private final Request.Builder requestBuilder;

	public NetworkRequest(Resources resources) {
		this.requestBuilder = new Request.Builder();
		staticQueryParams = "language=" + Locale.getDefault().getLanguage() + "&api_key=" + resources.getString(R.string.movieKey);

	}

	public NetworkRequest get(long movieID) {
		this.requestBuilder.url(baseUrl + "/movie/" + movieID + "?" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	public NetworkRequest search(String query) {
		this.requestBuilder.url(baseUrl + "/search/movie?query=" + query + "&" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	Request buildRequest() {
		return this.requestBuilder.build();
	}
}