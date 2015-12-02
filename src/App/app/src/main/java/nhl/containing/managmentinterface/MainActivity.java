package nhl.containing.managmentinterface;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nhl.containing.managmentinterface.communication.Communicator_new;
import nhl.containing.managmentinterface.data.ClassBridge;
import nhl.containing.managmentinterface.navigationdrawer.*;

/**
 * Main activity for the app
 */
public class MainActivity extends AppCompatActivity implements ContainersFragment.OnFragmentInteractionListener
{
    //navigation drawer
    public ListView mDrawerList;
    public RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public  ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    //end navigation drawer

    public Menu menu;
    public SharedPreferences preferences;
    private ConnectivityManager connManager;
    public Communicator_new communicator;
    public volatile Fragment fragment;
    public volatile int refreshTime = 0;
    private AutoRefreshRunnable autorefreshRunnable;
    private volatile boolean isRefreshing = false;
    private ExecutorService executer = Executors.newSingleThreadExecutor();

    /**
     * Creates the Activity
     * @param savedInstanceState used for saved data (on resume)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if(toolbar == null)
            this.finishAffinity();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        checkNetwork();
        setSupportActionBar(toolbar);
        setupNavDrawer(toolbar);
        setupHomeFragment();
    }

    @Override
    protected void onRestart() {
        checkNetwork();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        communicator.stop();
        super.onDestroy();
    }

    /**
     * Gives pop-up when using backbutton in mainactivity
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new  AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        dialog.setTitle("Leaving");
        dialog.setMessage("Are you sure you want to leave?");
        dialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("Stay", null);
        dialog.show();
    }

    /**
     * Setup the nav drawer
     * @param toolbar toolbar
     */
    private void setupNavDrawer(Toolbar toolbar)
    {
        mNavItems.add(new NavItem("Current Numbers", "Current numbers per category", R.drawable.ic_home_black));
        mNavItems.add(new NavItem("In", "Ingoing numbers", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Out", "Outgoing numbers", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Graph4", "Unknown", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Containers","List with actual container stats",R.drawable.ic_list_black));
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerPane = (RelativeLayout)findViewById(R.id.drawerPane);
        mDrawerList = (ListView)findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this,mNavItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                completeRefresh.run();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Setup the home fragment
     */
    private void setupHomeFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        GraphFragment gf = new GraphFragment();
        fragment = gf;
        ft.replace(R.id.frame, gf);
        ft.commit();
    }

    /**
     * Check the network state
     */
    private void checkNetwork()
    {
        String network = preferences.getString("Refresh_Network", "1");
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if(activeNetwork == null)
        {
            Toast.makeText(this,"There is no internet connection",Toast.LENGTH_SHORT).show();
            return;
        }
        if(network.equals("1") && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)
        {
            Toast.makeText(this,"Please connect to WI-FI or change your settings before refreshing",Toast.LENGTH_SHORT).show();
            return;
        }
        checkCommunucator();
        checkAutoRefresh();
    }

    /**
     * Check the autorefresh settings
     */
    private void checkAutoRefresh()
    {
        if(preferences.getBoolean("Refresh_Always",false))
        {
            try{
                refreshTime = Integer.parseInt(preferences.getString("Refresh_Auto_Time","30"));;
                autoRefresh(true);
            }
            catch (Exception e){}
        }
    }

    /**
     * Checks if the communcator class is running
     */
    private void checkCommunucator()
    {
        if(communicator != null && communicator.isRunning())
            return;
        try
        {
            communicator = new Communicator_new(this);
            new Thread(communicator).start();
            ClassBridge.communicator = communicator;
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates the Menu
     * @param menu menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    /**
     * Prepares the Menu
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        menu.findItem(R.id.action_refresh_time).setVisible(!drawerOpen);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Checks and starts/stops the runnable for autorefresh
     * @param on true when starting, false when stopping
     */
    private void autoRefresh(boolean on)
    {
        if(on)
        {
            if(autorefreshRunnable == null)
            {
                autorefreshRunnable = new AutoRefreshRunnable();
                executer = Executors.newSingleThreadExecutor();
                executer.submit(autorefreshRunnable);
            }
            return;
        }
        if(autorefreshRunnable != null)
            autorefreshRunnable.stop();
        if(!executer.isShutdown())
            executer.shutdown();
        autorefreshRunnable = null;
        isRefreshing = false;
    }

    /**
     * Triggered when an item from the menu is selected
     * @param item the item that is selected
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.menu_refresh_0:
                refreshTime = 0;
                autoRefresh(false);
                break;
            case R.id.menu_refresh_5:
                refreshTime = 5;
                autoRefresh(true);
                break;
            case R.id.menu_refresh_10:
                refreshTime = 10;
                autoRefresh(true);
                break;
            case R.id.menu_refresh_20:
                refreshTime = 20;
                autoRefresh(true);
                break;
            case R.id.menu_refresh_30:
                refreshTime = 30;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * complete the refresh
     */
    private Runnable completeRefresh = new Runnable() {
        @Override
        public void run() {
            isRefreshing = false;
            MenuItem ri =  menu.findItem(R.id.action_refresh);
            if(ri.getActionView() != null)
            {
                ri.getActionView().clearAnimation();
                ri.setActionView(null);
            }
        }
    };

    /**
     * refresh the graph or list
     */
    private void refresh()
    {
        if(isRefreshing){
            return;
        }
        isRefreshing = true;
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.rotaterefresh, null);
        Animation rotate = AnimationUtils.loadAnimation(getApplication(),R.anim.rotate);
        rotate.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotate);
        menu.findItem(R.id.action_refresh).setActionView(iv);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(fragment!= null && fragment instanceof GraphFragment)
                {
                    GraphFragment gf = (GraphFragment)fragment;
                    gf.setData();
                }
                else if(fragment != null && fragment instanceof ContainersFragment)
                {
                    ContainersFragment cf = (ContainersFragment)fragment;
                    cf.setData();
                }
                runOnUiThread(completeRefresh);
            }
        }).start();
    }

    /**
     * Runnable for refreshing
     */
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    /**
     * Select item from the navigation drawer
     * @param position the position of the item
     */
    private void selectItemFromDrawer(int position)
    {
        Fragment f;
        switch (position)
        {
            case 0:
                f = getSupportFragmentManager().findFragmentByTag("Graph_one");
                if(f == null || !f.isVisible())
                {
                    autoRefresh(false);
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_one").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 1:
                f = getSupportFragmentManager().findFragmentByTag("Graph_two");
                if(f == null || !f.isVisible())
                {
                    autoRefresh(false);
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_two").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                f = getSupportFragmentManager().findFragmentByTag("Graph_three");
                if(f == null || !f.isVisible())
                {
                    autoRefresh(false);
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_three").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                f = getSupportFragmentManager().findFragmentByTag("Graph_four");
                if(f == null || !f.isVisible())
                {
                    autoRefresh(false);
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_four").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 4:
                f = getSupportFragmentManager().findFragmentByTag("Container_list");
                if(f == null || !f.isVisible()) {
                    autoRefresh(false);
                    ContainersFragment cf = new ContainersFragment();
                    fragment = cf;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,cf,"Container_list").commit();
                }
                mDrawerLayout.closeDrawers();
                break;

        }
    }

    @Override
    public void onFragmentInteraction(int id) {
        Intent i = new Intent(this,ContainerActivity.class);
        i.putExtra("ID",id);
        startActivity(i);
    }

    /**
     * Runnable for autorefreshing datat
     */
    private class AutoRefreshRunnable implements Runnable
    {
        private volatile boolean isRunning = true;

        @Override
        public void run() {
            while(isRunning){
                doJob();
            }
        }

        private void doJob()
        {
            if(refreshTime == 0)
                return;
            runOnUiThread(refreshRunnable);
            try{
                Thread.sleep(refreshTime * 1000);
            }catch (Exception e){}
        }

        public void stop()
        {
            isRunning = false;
        }
    }
}
