package nhl.containing.managmentinterface.navigationdrawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.syncfusion.charts.*;
import com.syncfusion.charts.enums.*;

import java.util.List;

import nhl.containing.managmentinterface.activity.MainActivity;
import nhl.containing.managmentinterface.R;
import nhl.containing.managmentinterface.communication.Communicator;

import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.*;

/**
 * Fragment for showing the Graph
 */
public class GraphFragment extends Fragment {

    private SfChart chart;
    private int graphID;
    private ObservableArrayList list;
    private MainActivity main;
    private String[] names;

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
            Toast.makeText(getActivity(),R.string.graph_error_initialized,Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        main = MainActivity.getInstance();
        names = getResources().getStringArray(R.array.graph_items);
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
        final View view = inflater.inflate(R.layout.graphfragment, container, false);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(500);
                        }catch (Exception e){};
                        if(!main.checkAutoRefresh())
                            getActivity().runOnUiThread(main.refreshRunnable);
                    }
                }).start();
            }
        });
        chart = (SfChart)view.findViewById(R.id.graph);
        setupChart();
        return view;
    }

    /**
     * Setting up the graph/ chart
     */
    private void setupChart()
    {
        list = new ObservableArrayList();
        int max;
        if(graphID == 0)
        {
            max = 7;
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
            max = 4;
            ColumnSeries seriesChart = new ColumnSeries();
            seriesChart.setDataSource(list);
            seriesChart.getDataMarker().setShowLabel(true);
            seriesChart.getDataMarker().getLabelStyle().setLabelPosition(DataMarkerLabelPosition.Inner);
            chart.getSeries().add(seriesChart);
            chart.setPrimaryAxis(new CategoryAxis());
            chart.setSecondaryAxis(new NumericalAxis());
        }
        for(int i = 0; i < max; i++)
        {
            list.add(new ChartDataPoint<String,Integer>(names[i],0));
        }
    }

    /**
     * Update the Graph / chart
     * @param data update data
     */
    private void updateChart(final int[] data)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                for(int i = 0; i < data.length; i++)
                {
                    list.add(new ChartDataPoint<String,Integer>(names[i],data[i]));
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
        if(Communicator.getInstance() == null || !Communicator.getInstance().isRunning())
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.error_connect_controller, Toast.LENGTH_SHORT).show();
                    main.completeRefresh.run();
                }
            });
            return;
        }
        Instruction.Builder builder = Instruction.newBuilder();
        builder.setInstructionType(InstructionType.APP_REQUEST_DATA);
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(graphID);
        Communicator.getInstance().setRequest(builder.build());
    }

    /**
     * Receives the new data and starts updating the graph
     * @param block datablock
     */
    public void UpdateGraph(datablockApp block)
    {
        List<ContainerGraphData> list = block.getGraphsList();
        int[] dataList = new int[list.size()];
        if(!list.isEmpty())
        {
            for(int i = 0; i < list.size();i++)
            {
                ContainerGraphData data = list.get(i);
                dataList[data.getCategory().getNumber()] = data.getAantal();
            }
            updateChart(dataList);
            return;
        }
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.graph_error_wrong, Toast.LENGTH_SHORT).show();
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
