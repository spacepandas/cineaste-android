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

	public NetworkRequest getMovie(long movieID) {
		this.requestBuilder.url(baseUrl + "/movie/" + movieID + "?" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	public NetworkRequest searchMovie(String query) {
		this.requestBuilder.url(baseUrl + "/search/movie?query=" + query + "&" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	public NetworkRequest getUpcomingMovies() {
		this.requestBuilder.url(baseUrl + "/movie/upcoming?" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	public NetworkRequest getSeries(long seriesId) {
		this.requestBuilder.url(baseUrl + "/tv/" + seriesId + "?" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	public NetworkRequest searchSeries(String query) {
		this.requestBuilder.url(baseUrl + "/search/tv?query=" + query + "&" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	public NetworkRequest getPopularSeries() {
		this.requestBuilder.url(baseUrl + "/tv/popular?" + staticQueryParams);
		this.requestBuilder.header("accept", "application/json");
		return this;
	}

	Request buildRequest() {
		return this.requestBuilder.build();
	}
}