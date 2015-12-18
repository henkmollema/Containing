package nhl.containing.managmentinterface.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nhl.containing.managmentinterface.R;

/**
 * Fragment for the settings
 */
public class SettingsActivityFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Required empty constructor
     */
    public SettingsActivityFragment() {
    }

    /**
     * Creates the preferences
     * @param bundle bundle
     * @param s string
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    /**
     * Creates the view of the fragment
     * @param inflater layout inflater
     * @param container container
     * @param savedInstanceState saved instance
     * @return the view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ViewGroup group = (ViewGroup)view.findViewById(R.id.settings_list);
        View superView = super.onCreateView(inflater,container,savedInstanceState);
        group.addView(superView);
        return group;
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("Refresh_Network")){
            if(MainActivity.getInstance() != null)
                MainActivity.getInstance().checkNetwork();
        }
    }
}
