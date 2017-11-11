package de.cineaste.android.network;

import java.util.Locale;

import de.cineaste.android.Constants;
import okhttp3.Request;

public class NetworkRequest {

	private final String baseUrl = "https://api.themoviedb.org/3";

	private final String staticQueryParams = "language=" + Locale.getDefault().getLanguage() + "&api_key=" + Constants.API_KEY;

	private final Request.Builder requestBuilder;

	public NetworkRequest() {
		this.requestBuilder = new Request.Builder();
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