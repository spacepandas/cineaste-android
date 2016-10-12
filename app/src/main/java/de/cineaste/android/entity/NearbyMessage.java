package de.cineaste.android.entity;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.List;

public class NearbyMessage {

	private static final Gson GSON = new Gson();

	private String userName;
	private String deviceId;
	private List<MovieDto> movies;

	private NearbyMessage() {
	}

	public NearbyMessage(String userName, String deviceId, List<MovieDto> movies) {
		this.userName = userName;
		this.deviceId = deviceId;
		this.movies = movies;
	}

	public String getUserName() {
		return userName;
	}

	private void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public List<MovieDto> getMovies() {
		return movies;
	}

	private void setMovies(List<MovieDto> movies) {
		this.movies = movies;
	}

	public static NearbyMessage fromMessage(Message message) {
		String nearbyMessageString = new String(message.getContent()).trim();

		return GSON.fromJson(
				(new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
				NearbyMessage.class);
	}

	public static Message newNearbyMessage(NearbyMessage nearbyMessage) {
		return new Message(
				GSON.toJson(nearbyMessage).getBytes(Charset.forName("UTF-8")));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NearbyMessage that = (NearbyMessage) o;

		return !(deviceId != null ? !deviceId.equals(that.deviceId) : that.deviceId != null);
	}

	@Override
	public int hashCode() {

		return deviceId != null ? deviceId.hashCode() : 0;
	}
}
