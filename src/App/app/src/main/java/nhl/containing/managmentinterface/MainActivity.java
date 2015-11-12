package nhl.containing.managmentinterface;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private boolean isRefreshing = false;
    //private Thread test;
    //private AutoRefreshRunnable refreshThread;
    private Menu menu;
    private GraphView graph;
    //private int refreshTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if(toolbar == null)
            this.finishAffinity();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Management Interface");
        graph = (GraphView) findViewById(R.id.graph);
        //refreshThread = new AutoRefreshRunnable();
        //test = new Thread(refreshThread);
        setUpGraph();
        createGraph();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpGraph()
    {
        StaticLabelsFormatter slf = new StaticLabelsFormatter(graph);
        slf.setHorizontalLabels(new String[]{"T","V","Z","B","O","A","D"});
        graph.getGridLabelRenderer().setLabelFormatter(slf);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-0.5);
        graph.getViewport().setMaxX(6.5);
    }

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

    private void completeRefresh()
    {
        MenuItem ri =  menu.findItem(R.id.action_refresh);
        ri.getActionView().clearAnimation();
        ri.setActionView(null);
        isRefreshing = false;
    }

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

    private View.OnClickListener refreshlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
