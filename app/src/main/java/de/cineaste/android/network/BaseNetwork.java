package de.cineaste.android.network;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

class BaseNetwork {
	interface OnResultListener {
		void onResultListener(Response response);
	}

	private static final String METHOD_GET = "GET";

	final String host;
	final Gson gson = new Gson();

	private static final int MAXIMUM_RESPONSE_SIZE = 1048576;

	BaseNetwork(String host) {
		this.host = host;
	}

	void requestAsync(final Request request, final OnResultListener listener) {
		new AsyncTask<Request, Void, Response>() {
			@Override
			protected Response doInBackground(Request... params) {
				HttpURLConnection connection = null;

				try {
					connection = openConnection(params[0]);

					return new Response(
							connection.getResponseCode(),
							readResponse(connection.getInputStream())
					);
				} catch (IOException e) {
					// fall through
				} finally {
					if (connection != null)
						connection.disconnect();
				}

				return new Response(HttpURLConnection.HTTP_INTERNAL_ERROR, new byte[]{});
			}

			@Override
			protected void onPostExecute(Response response) {
				super.onPostExecute(response);
				listener.onResultListener(response);
			}
		}.execute(request);
	}

	boolean successfulRequest(int statusCode) {
		return statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE;
	}

	private HttpURLConnection openConnection(Request request) throws IOException {
		HttpURLConnection connection =
				(HttpURLConnection) new URL(request.getUrl()).openConnection();
		connection.setRequestMethod(request.getMethod());

		setHeaders(request, connection);

		return connection;
	}


	private void setHeaders(Request request, HttpURLConnection connection) {
		if (request.getHeaders() != null) {
			for (String header : request.getHeaders()) {
				String tokens[] = header.split(":");

				if (tokens.length != 2)
					continue;

				connection.setRequestProperty(tokens[0], tokens[1]);
			}
		}
	}

	private byte[] readResponse(InputStream in) throws IOException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];

		int length = 0;
		int bytes = in.read(buffer);

		while (bytes > -1) {
			if (bytes > 0) {
				data.write(buffer, 0, bytes);
				length += bytes;

				if (length > MAXIMUM_RESPONSE_SIZE)
					return null;
			}
			bytes = in.read(buffer);
		}

		return data.toByteArray();
	}

	class Response {
		private final int code;
		private final byte data[];

		Response(int code, byte data[]) {
			this.code = code;
			this.data = data;
		}

		int getCode() {
			return code;
		}

		public String getString() {
			try {
				return new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
	}

	class Request {
		private final String url;
		private final String method;
		private final String[] headers;

		Request(String url, String[] headers) {
			this.url = url;
			this.method = METHOD_GET;
			this.headers = headers;
		}

		String getUrl() {
			return url;
		}

		String getMethod() {
			return method;
		}

		String[] getHeaders() {
			return headers;
		}
	}
}