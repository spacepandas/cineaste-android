package de.cineaste.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public boolean isConnected;

    private static NetworkChangeReceiver instance;

    public static NetworkChangeReceiver getInstance() {
        return instance == null ? instance = new NetworkChangeReceiver() : instance;
    }

    @Override
    public void onReceive( Context context, Intent intent ) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if( activeNetwork != null ) {
            isConnected = true;
        } else {
            isConnected = false;
        }
    }
}