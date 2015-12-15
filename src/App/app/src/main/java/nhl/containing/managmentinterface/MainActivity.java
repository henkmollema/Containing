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

import nhl.containing.managmentinterface.communication.Communicator;
import nhl.containing.managmentinterface.navigationdrawer.*;

/**
 * Main activity for the app
 */
public class MainActivity extends AppCompatActivity implements ContainersFragment.OnFragmentInteractionListener
{
    private static MainActivity main;
    //navigation drawer
    public ListView mDrawerList;
    public RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public  ArrayList<NavItem> mNavItems = new ArrayList<>();
    private String[] fragmentList = new String[]{"Graph_one","Graph_two","Graph_three","Graph_four","Container_list"};
    //end navigation drawer

    public Menu menu;

    public SharedPreferences preferences;
    private ConnectivityManager connManager;

    public Communicator communicator;
    public volatile Fragment fragment;
    public volatile int refreshTime = 0;
    private AutoRefreshRunnable autorefreshRunnable;
    private volatile boolean isRefreshing = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    public volatile boolean rightNetwork = false;

    /**
     * Creates the Activity
     * @param savedInstanceState used for saved data (on resume)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if(toolbar == null)
            this.finishAffinity();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        checkNetwork();
        setSupportActionBar(toolbar);
        setupNavDrawer(toolbar);
        selectItemFromDrawer(0);
        //setupHomeFragment();
    }

    /**
     * Called on destruction of the Activity
     */
    @Override
    protected void onDestroy() {
        if(communicator != null)
            communicator.stop();
        autoRefresh(false);
        super.onDestroy();
    }

    /**
     * Gives pop-up when using backbutton in mainactivity
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new  AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        dialog.setTitle(R.string.exit_title);
        dialog.setMessage(R.string.exit_message);
        dialog.setPositiveButton(R.string.exit_pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {finish();
            }
        });
        dialog.setNegativeButton(R.string.exit_neg, null);
        dialog.show();
    }

    /**
     * Setup the nav drawer
     * @param toolbar toolbar
     */
    private void setupNavDrawer(Toolbar toolbar)
    {
        int[][] navitems = new int[][]{
                new int[]{R.string.nav_home_titel,R.string.nav_graph1_titel,R.string.nav_graph2_titel,R.string.nav_graph3_titel,R.string.nav_containerlist_titel},
                new int[]{R.string.nav_home_subtitel,R.string.nav_graph1_subtitel,R.string.nav_graph2_subtitel,R.string.nav_graph3_subtitel,R.string.nav_containerlist_subtitel},
                new int[]{R.drawable.ic_home_black,R.drawable.ic_poll_black,R.drawable.ic_poll_black,R.drawable.ic_poll_black,R.drawable.ic_list_black}
        };
        for(int i = 0; i < navitems[0].length; i++)
            mNavItems.add(new NavItem(getResources().getString(navitems[0][i]),getResources().getString(navitems[1][i]),navitems[2][i]));
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
     * Checks if connected to the right network
     * @param network id of the connectivity
     */
    public void setNetwork(int network)
    {
        String networkType = preferences.getString("Refresh_Network", "1");
        switch (network)
        {
            case -1:
                rightNetwork = false;
                Toast.makeText(this,R.string.no_internet,Toast.LENGTH_SHORT).show();
                break;
            case ConnectivityManager.TYPE_MOBILE:
                rightNetwork = !networkType.equals("1");
                if(!networkType.equals("1"))
                    Toast.makeText(this,R.string.wrong_connection,Toast.LENGTH_SHORT).show();
                break;
            case ConnectivityManager.TYPE_WIFI:
                rightNetwork = true;
                break;
        }
    }

    /**
     * Check the network state
     */
    public void checkNetwork()
    {
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if(activeNetwork != null)
        {
            setNetwork(activeNetwork.getType());
        }
        else
        {
           setNetwork(-1);
        }
    }

    /**
     * Check the autorefresh settings
     */
    public boolean checkAutoRefresh()
    {
        if(!rightNetwork)
            return false;
        if(preferences.getBoolean("Refresh_Always",false))
        {
            try{
                refreshTime = Integer.parseInt(preferences.getString("Refresh_Auto_Time","30"));;
                autoRefresh(true);
                return true;
            }
            catch (Exception e){}
        }
        return false;
    }

    /**
     * Checks if the communicator class is running
     */
    private boolean checkCommunicator()
    {
        if(communicator != null && communicator.isRunning())
            return true;
        try
        {
            communicator = new Communicator(this);
            new Thread(communicator).start();
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            return false;
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
        int[] items = new int[]{R.id.action_refresh,R.id.action_refresh_time,R.id.action_settings};
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        for(int i : items)
            menu.findItem(i).setVisible(!drawerOpen);
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
                executor = Executors.newSingleThreadExecutor();
                executor.submit(autorefreshRunnable);
            }
            return;
        }
        if(autorefreshRunnable != null)
            autorefreshRunnable.stop();
        if(!executor.isShutdown())
            executor.shutdown();
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
        switch (item.getItemId())
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
    public Runnable completeRefresh = new Runnable() {
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
     * Gives the status of refreshing
     * @return true when refreshing, else false
     */
    public boolean getRefreshStatus()
    {
        return this.isRefreshing;
    }

    /**
     * refresh the graph or list
     */
    public void refresh()
    {
        if(isRefreshing || !rightNetwork){
            return;
        }
        isRefreshing = true;
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.rotaterefresh, null);
        Animation rotate = AnimationUtils.loadAnimation(getApplication(),R.anim.rotate);
        rotate.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotate);
        menu.findItem(R.id.action_refresh).setActionView(iv);
        if(!checkCommunicator()){
            isRefreshing = false;
            completeRefresh.run();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(fragment != null)
                {
                    if(fragment instanceof GraphFragment)
                        ((GraphFragment) fragment).setData();
                    else if(fragment instanceof ContainersFragment)
                        ((ContainersFragment) fragment).setData();
                }
            }
        }).start();
    }

    /**
     * Runnable for refreshing
     */
    public Runnable refreshRunnable = new Runnable() {
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
        if(position < fragmentList.length)
        {
            Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentList[position]);
            if(f == null || !f.isVisible())
            {
                autoRefresh(false);
                if(position == 4)
                {
                    ContainersFragment cf = new ContainersFragment();
                    fragment = cf;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,cf,fragmentList[position]).commit();
                }
                else
                {
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,fragmentList[position]).commit();
                }
            }
        }
        mDrawerLayout.closeDrawers();
    }

    /**
     * Called when clicked on a container in the containerlist. Provides the ID of the container
     * @param id id of the container
     */
    @Override
    public void onFragmentInteraction(int id) {
        Intent i = new Intent(this,ContainerActivity.class);
        i.putExtra("ID",id);
        completeRefresh.run();
        startActivity(i);
    }

    /**
     * Gets an instance of the Mainactivity
     * @return instance of mainactivity
     */
    public static MainActivity getInstance()
    {
        return main;
    }

    /**
     * Runnable for autorefreshing
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
