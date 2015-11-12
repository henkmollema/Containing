package nhl.containing.managmentinterface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.*;

import java.util.ArrayList;

import nhl.containing.managmentinterface.navigationdrawer.*;

/**
 * Main activity for the app
 */
public class MainActivity extends ActionBarActivity
{
    //navigation drawer
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    //end navigation drawer

    private boolean isRefreshing = false;
    //private Thread test;
    //private AutoRefreshRunnable refreshThread;
    private Menu menu;
    private GraphView graph;
    //private int refreshTime = 0;

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
        setSupportActionBar(toolbar);
        //navigation drawer
        mNavItems.add(new NavItem("Per Category", "Numbers per category", R.drawable.ic_home_black));
        mNavItems.add(new NavItem("Graph2", "Unknown", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Graph3", "Unknown", R.drawable.ic_poll_black));
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
        //end navigationdrawer

        graph = (GraphView) findViewById(R.id.graph);
        //refreshThread = new AutoRefreshRunnable();
        //test = new Thread(refreshThread);
        setUpGraph();
        createGraph();
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
        menu.findItem(R.id.action_legend).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
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

                break;
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.menu_refresh_0:
                //refreshTime = 0;
                //refreshThread.stop();
                break;
            case R.id.menu_refresh_10:
                //refreshTime = 10;
                //if(!refreshThread.isRunning)
                //    test.run();
                break;
            case R.id.menu_refresh_20:
                //refreshTime = 20;
                //if(!refreshThread.isRunning)
                //    test.run();
                break;
            case R.id.menu_refresh_30:
                //refreshTime = 30;
                //if(!refreshThread.isRunning)
                //    test.run();
                break;
            case R.id.action_legend:
                showLegend();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a legend for the graph
     */
    private void showLegend()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Legend");
        builder.setMessage("Tra = Train\nTru = Truck\nSea = Seaship\nInl = Inlineship\nSto = Storage\nAGV = Automatic Guided Vehicles\nRem = Remaining");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Setup the Graph
     */
    private void setUpGraph()
    {
        StaticLabelsFormatter slf = new StaticLabelsFormatter(graph);
        slf.setHorizontalLabels(new String[]{"Tra", "Tru", "Sea", "Inl", "Sto", "AGV", "Rem"});
        graph.getGridLabelRenderer().setLabelFormatter(slf);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-0.5);
        graph.getViewport().setMaxX(6.5);
    }

    /**
     * Makes the graph
     */
    private void createGraph()
    {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
             new DataPoint(0,10),
             new DataPoint(1,50),
             new DataPoint(2,5),
             new DataPoint(3,100),
             new DataPoint(4,50),
             new DataPoint(5,0),
             new DataPoint(6,65)
        });
        graph.addSeries(series);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });
        series.setSpacing(20);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
    }

    /**
     * complete the refresh
     */
    private void completeRefresh()
    {
        MenuItem ri =  menu.findItem(R.id.action_refresh);
        ri.getActionView().clearAnimation();
        ri.setActionView(null);
        isRefreshing = false;
    }

    /**
     * refresh the graph
     */
    private void refresh()
    {
        isRefreshing = true;
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.rotaterefresh, null);
        Animation rotate = AnimationUtils.loadAnimation(getApplication(),R.anim.rotate);
        rotate.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotate);
        iv.setOnClickListener(refreshlistener);
        menu.findItem(R.id.action_refresh).setActionView(iv);
    }

    /**
     * Select item from the navigation drawer
     * @param position the position of the item
     */
    private void selectItemFromDrawer(int position)
    {
        switch (position)
        {
            case 0:
                mDrawerLayout.closeDrawers();
                break;
        }
    }

    /**
     * listens to a click on the refreshanimation
     */
    private View.OnClickListener refreshlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //stoprefresh
            completeRefresh();
        }
    };

    /*
    private class AutoRefreshRunnable implements Runnable
    {
        private Calendar refreshDate;
        private volatile boolean stopped = false;
        private volatile boolean isRunning = false;
        @Override
        public void run() {
            isRunning = true;
            while(!stopped) {
                doJob();
            }
        }

        private void doJob()
        {
            if(refreshTime == 0)
                return;
            if(refreshDate == null)
            {
                refreshDate = Calendar.getInstance();
                refreshDate.add(Calendar.SECOND,refreshTime);
            }
            if(refreshDate.getTime().after(Calendar.getInstance().getTime()))
            {
                refresh();
                try{
                    wait(1000);
                }
                catch (Exception e){}
                completeRefresh();
                refreshDate = Calendar.getInstance();
                refreshDate.add(Calendar.SECOND,refreshTime);
            }
        }

        public void stop()
        {
            this.stopped = true;
            this.isRunning = false;
        }
    }*/
}
