package de.cineaste.android.network;

public interface NetworkCallback {
	void onFailure();
	void onSuccess(NetworkResponse response);
}