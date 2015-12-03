package nhl.containing.managmentinterface;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nhl.containing.managmentinterface.data.ClassBridge;
import nhl.containing.networking.protobuf.AppDataProto.*;

/**
 * Activity for showing container details
 */
public class ContainerActivity extends AppCompatActivity {

    public int ID;
    private ProgressDialog dialog;

    /**
     * Creates the containeractivity
     * @param savedInstanceState saved instance of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getExtras() != null && ClassBridge.communicator != null && ClassBridge.communicator.isRunning())
        {
            ID = getIntent().getExtras().getInt("ID");
            getSupportActionBar().setTitle("Container " + ID);
            ClassBridge.communicator.setContainerActivity(this);
            dialog = ProgressDialog.show(this,"Loading...","Loading container information, please wait",true,false);
            return;
        }
        finish();
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

    /**
     * Receives a datablock and updates the activity
     * @param datablock data
     */
    public void setData(final datablockApp datablock)
    {
        try{
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            final ContainerInfo info = datablock.getContainer();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.ContainerID)).setText(Integer.toString(info.getID()));
                    ((TextView) findViewById(R.id.Owner)).setText(info.getEigenaar());
                    ((TextView) findViewById(R.id.Content)).setText(info.getInhoud());
                    ((TextView) findViewById(R.id.WeightE)).setText(info.getGewichtLeeg() + " Ton");
                    ((TextView) findViewById(R.id.Weight)).setText(info.getGewichtVol() + " Ton");
                    ((TextView) findViewById(R.id.ArrivalCompany)).setText(info.getAanvoerMaatschappij());
                    ((TextView) findViewById(R.id.ArrivalDate)).setText(df.format(new Date(info.getBinnenkomstDatum())));
                    ((TextView) findViewById(R.id.ArrivalTransport)).setText(getTransportType(info.getVervoerBinnenkomst()));
                    ((TextView) findViewById(R.id.DepartmentCompany)).setText(info.getAfvoerMaatschappij());
                    ((TextView) findViewById(R.id.DepartmentDate)).setText(df.format(new Date(info.getVertrekDatum())));
                    ((TextView) findViewById(R.id.DepartmentTransport)).setText(getTransportType(info.getVervoerVertrek()));
                    dialog.dismiss();
                }
            });
        }catch (Exception e)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Couldn't get container information!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    /**
     * Picks the right name with the enum category
     * @param category category
     * @return normal name
     */
    private String getTransportType(ContainerCategory category)
    {
        switch (category)
        {
            case TRUCK:
                return "Truck";
            case SEASHIP:
                return "Seagoing vessel";
            case INLINESHIP:
                return "Barge";
            case TRAIN:
                return "Train";
            default:
                return "-";
        }
    }

    /**
     * Goes back to last activity
     */
    public void goBack()
    {
        finish();
    }

    /**
     * Called on destruction of the fragment
     */
    @Override
    protected void onDestroy() {
        if(ClassBridge.communicator != null)
            ClassBridge.communicator.detachContainerActivity();
        super.onDestroy();
    }
}
