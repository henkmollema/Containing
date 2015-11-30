package nhl.containing.managmentinterface;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragmentCompat {

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ViewGroup group = (ViewGroup)view.findViewById(R.id.settings_list);
        View view1 = super.onCreateView(inflater,container,savedInstanceState);
        group.addView(view1);
        return group;
    }
}
