package nhl.containing.managmentinterface.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import nhl.containing.managmentinterface.R;

/**
 * Activity that holds a PreferencesFragment
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Creates the activity
     * @param savedInstanceState saved instace of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new SettingsActivityFragment()).commit();
    }

    /**
    * Called when clicked on an option item
    * @param item clicked item
    * @return returns true when clicked home, else super.onOptionsItemSelected(item)
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
