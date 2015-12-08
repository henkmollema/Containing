package nhl.containing.managmentinterface.navigationdrawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.syncfusion.charts.*;
import com.syncfusion.charts.enums.*;

import java.util.ArrayList;
import java.util.List;

import nhl.containing.managmentinterface.MainActivity;
import nhl.containing.managmentinterface.R;
import nhl.containing.managmentinterface.data.ClassBridge;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Fragment for showing the Graph
 */
public class GraphFragment extends Fragment {

    private SfChart chart;
    private int graphID;
    private ObservableArrayList list;
    private MainActivity main;
    private String[] names = new String[] {"Train","Truck","Seaship","Barge","Storage","AGV","Remainer"};

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the Graph fragment
     * @param savedInstance saved instance of this fragment
     */
    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        if(getArguments() != null)
            graphID = getArguments().getInt("graphID");
        else
            graphID = 0;
        if(MainActivity.getInstance() == null)
        {
            Toast.makeText(getActivity(),"Containerfragement couldn't be initialized",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        main = MainActivity.getInstance();
    }

    /**
     * Create the view of the fragment
     * @param inflater layout inflater
     * @param container container
     * @param savedInstanceState bundle savedinstance
     * @return view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphfragment, container, false);
        chart = (SfChart)view.findViewById(R.id.graph);
        setupChart();
        return view;
    }

    /**
     * Called when fragment becomes visible
     */
    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch (Exception e){}
                if(!main.checkAutoRefresh())
                    getActivity().runOnUiThread(main.refreshRunnable);
            }
        }).start();
    }

    /**
     * Setting up the graph/ chart
     */
    private void setupChart()
    {
        list = new ObservableArrayList();
        if(graphID == 0)
        {
            for(int i = 0; i < 7; i++)
            {
                list.add(new ChartDataPoint<String,Integer>(names[i],0));
            }
            PieSeries pieSeries = new PieSeries();
            pieSeries.getDataMarker().setShowLabel(true);
            pieSeries.getDataMarker().setLabelContent(LabelContent.YValue);
            pieSeries.getDataMarker().getLabelStyle().setBackgroundColor(Color.TRANSPARENT);
            pieSeries.setDataSource(list);
            chart.getSeries().add(pieSeries);
            chart.getLegend().setVisibility(Visibility.Visible);
            chart.getLegend().setDockPosition(ChartDock.Right);
            chart.getLegend().setOrientation(Orientation.Vertical);
        }
        else
        {
            ColumnSeries seriesChart = new ColumnSeries();
            for(int i = 0; i < 4; i++)
            {
                list.add(new ChartDataPoint<String,Integer>(names[i],0));
            }
            seriesChart.setDataSource(list);
            seriesChart.getDataMarker().setShowLabel(true);
            seriesChart.getDataMarker().getLabelStyle().setLabelPosition(DataMarkerLabelPosition.Inner);
            chart.getSeries().add(seriesChart);
            chart.setPrimaryAxis(new CategoryAxis());
            NumericalAxis test = new NumericalAxis();
            test.setInterval(10);
            chart.setSecondaryAxis(test);
        }
    }

    /**
     * Update the Graph / chart
     * @param data update data
     */
    private void updateChart(final List<Integer> data)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                if (graphID == 0) {
                    for(int i = 0; i < 7; i++)
                    {
                        list.add(new ChartDataPoint<String,Integer>(names[i],data.get(i)));
                    }
                } else {
                    for(int i = 0; i < 4; i++)
                    {
                        list.add(new ChartDataPoint<String,Integer>(names[i],data.get(i)));
                    }
                }
               main.completeRefresh.run();
            }
        });
    }

    /**
     * Requests the new data from the controller
     */
    public void setData()
    {
        //make instruction
        if(ClassBridge.communicator == null || !ClassBridge.communicator.isRunning())
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Coundn't connect to controller", Toast.LENGTH_SHORT).show();
                    main.completeRefresh.run();
                }
            });
            return;
        }
        Instruction.Builder builder = Instruction.newBuilder();
        builder.setInstructionType(InstructionType.APP_REQUEST_DATA);
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(graphID);
        ClassBridge.communicator.setRequest(builder.build());
    }

    /**
     * Receives the new data and starts updating the graph
     * @param block
     */
    public void UpdateGraph(datablockApp block)
    {
        List<ContainerGraphData> list = block.getGraphsList();
        List<Integer> dataList = new ArrayList<>();
        if(!list.isEmpty())
        {
            for(int i = 0; i < list.size();i++)
            {
                ContainerGraphData data = list.get(i);
                dataList.add(data.getCategory().getNumber(),data.getAantal());
            }
            updateChart(dataList);
            return;
        }
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    MainActivity.getInstance().completeRefresh.run();
                }
            });
        }
        catch (Exception e){}
    }

    /**
     * Returns the id of the graph
     * @return graphid
     */
    public int getGraphID()
    {
        return graphID;
    }




}
