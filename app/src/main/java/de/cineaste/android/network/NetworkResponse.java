package de.cineaste.android.network;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public class NetworkResponse {

	private final Reader responseReader;
	private final Map<String, List<String>> header;

	public NetworkResponse(Reader responseReader, Map<String, List<String>> header) {
		this.responseReader = responseReader;
		this.header = header;
	}

	public Reader getResponseReader() {
		return responseReader;
	}

	public Map<String, List<String>> getHeader() {
		return header;
	}
}
