package de.cineaste.android.network;

import java.io.Reader;

public class NetworkResponse {

    private final Reader responseReader;

    public NetworkResponse(Reader responseReader) {
        this.responseReader = responseReader;
    }

    public Reader getResponseReader() {
        return responseReader;
    }
}
