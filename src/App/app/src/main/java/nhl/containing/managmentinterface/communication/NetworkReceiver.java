package nhl.containing.managmentinterface.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.prefs.Preferences;

import nhl.containing.managmentinterface.MainActivity;

/**
 * Listens to connectivity changes
 */
public class NetworkReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;

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
        connectivityManager =  (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
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
