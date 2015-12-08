package nhl.containing.managmentinterface;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for the settings
 */
public class SettingsActivityFragment extends PreferenceFragmentCompat {

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
}
