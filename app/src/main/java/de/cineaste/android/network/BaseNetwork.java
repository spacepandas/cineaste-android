package de.cineaste.android.network;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class BaseNetwork {
    public interface OnResultListener {
        void onResultListener( Response response );
    }

    static final String METHOD_DELETE = "DELETE";
    static final String METHOD_GET = "GET";
    static final String METHOD_POST = "POST";
    static final String METHOD_PUT = "PUT";

    final String host;
    final Gson gson = new Gson();

    private static final int MAXIMUM_RESPONSE_SIZE = 1048576;

    protected BaseNetwork( String host ) {
        this.host = host;
    }

    protected void requestAsync( final Request request, final OnResultListener listener ) {
        new AsyncTask<Request, Void, Response>() {
            @Override
            protected Response doInBackground( Request... params ) {
                HttpURLConnection connection = null;

                try {
                    connection = openConnection( params[0] );

                    return new Response(
                            connection.getResponseCode(),
                            readResponse( connection.getInputStream() ),
                            connection.getHeaderFields()
                    );
                } catch ( IOException e ) {
                    // fall through
                } finally {
                    if( connection != null )
                        connection.disconnect();
                }

                return new Response( HttpURLConnection.HTTP_INTERNAL_ERROR, new byte[]{}, null );
            }

            @Override
            protected void onPostExecute( Response response ) {
                super.onPostExecute( response );
                listener.onResultListener( response );
            }
        }.execute( request );
    }

    protected boolean successfulRequest( int statusCode ) {
        return statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE;
    }

    private HttpURLConnection openConnection( Request request ) throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) new URL( request.getUrl() ).openConnection();
        connection.setRequestMethod( request.getMethod() );

        setHeaders( request, connection );
        addData( request, connection );

        return connection;
    }


    private void setHeaders( Request request, HttpURLConnection connection ) {
        if( request.getHeaders() != null ) {
            for ( String header : request.getHeaders() ) {
                String tokens[] = header.split( ":" );

                if( tokens.length != 2 )
                    continue;

                connection.setRequestProperty( tokens[0], tokens[1] );
            }
        }
    }

    private void addData( Request request, HttpURLConnection connection ) throws IOException {
        if( request.getData() != null ) {
            connection.setDoOutput( true );

            OutputStreamWriter writer =
                    new OutputStreamWriter( connection.getOutputStream() );
            writer.write( request.getData() );
            writer.flush();
        }
    }

    private byte[] readResponse( InputStream in ) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        int length = 0;
        int bytes = in.read( buffer );

        while ( bytes > -1 ) {
            if( bytes > 0 ) {
                data.write( buffer, 0, bytes );
                length += bytes;

                if( length > MAXIMUM_RESPONSE_SIZE )
                    return null;
            }
            bytes = in.read( buffer );
        }

        return data.toByteArray();
    }


    protected class Response {
        private final int code;
        private final byte data[];
        private final Map<String, List<String>> headers;

        public Response( int code, byte data[], Map<String, List<String>> headers ) {
            this.code = code;
            this.data = data;
            this.headers = headers;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public int getCode() {
            return code;
        }

        public byte[] getData() {
            return data;
        }

        public String getString() {
            try {
                return new String( data, "UTF-8" );
            } catch ( UnsupportedEncodingException e ) {
                return null;
            }
        }
    }

    protected class Request {
        private String url;
        private String method;
        private String[] headers;
        private String data;

        public Request( String url, String method, String[] headers, String data ) {
            this.url = url;
            this.method = method;
            this.headers = headers;
            this.data = data;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod( String method ) {
            this.method = method;
        }

        public String[] getHeaders() {
            return headers;
        }

        public void setHeaders( String[] headers ) {
            this.headers = headers;
        }

        public String getData() {
            return data;
        }

        public void setData( String data ) {
            this.data = data;
        }
    }
}