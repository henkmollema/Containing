package nhl.containing.managmentinterface.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import nhl.containing.managmentinterface.activity.MainActivity;

/**
 * Listens to connectivity changes
 */
public class NetworkReceiver extends BroadcastReceiver {

    public NetworkReceiver() {
    }

    /**
     * Checks if there is a connection and sends the connection type to the mainactivity
     * @param context the context
     * @param intent intent to listen to
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(MainActivity.getInstance() == null)
            return;
         ConnectivityManager connectivityManager =  (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork != null)
        {
            MainActivity.getInstance().setNetwork(activeNetwork.getType());
        }
        else
        {
            MainActivity.getInstance().setNetwork(-1);
        }
    }
}
