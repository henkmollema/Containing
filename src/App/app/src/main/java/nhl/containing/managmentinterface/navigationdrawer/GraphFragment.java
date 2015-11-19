package nhl.containing.managmentinterface.navigationdrawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import nhl.containing.managmentinterface.R;
import nhl.containing.managmentinterface.communication.Communicator;

/**
 * Fragment for showing the Graph
 */
public class GraphFragment extends Fragment {

    private GraphView graph;
    private int graphID;
    private static BarGraphSeries<DataPoint> series;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        if(getArguments() != null)
        {
            graphID = getArguments().getInt("graphID");
            return;
        }
        graphID = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.graphfragment, container, false);
        graph = (GraphView)view.findViewById(R.id.graph);
        setUpGraph(graph);
        setData();
        return view;
    }

    /**
     * Update the graph
     */
    public void setData()
    {
        updateGraph(Communicator.getData(graphID));
    }

    /**
     * Setup the Graph
     */
    private void setUpGraph(GraphView graph)
    {
        StaticLabelsFormatter slf = new StaticLabelsFormatter(graph);
        slf.setHorizontalLabels(new String[]{"Tra", "Tru", "Sea", "Inl", "Sto", "AGV", "Rem"});
        graph.getGridLabelRenderer().setLabelFormatter(slf);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-0.5);
        graph.getViewport().setMaxX(6.5);
        series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0,0),
                new DataPoint(1,0),
                new DataPoint(2,0),
                new DataPoint(3,0),
                new DataPoint(4,0),
                new DataPoint(5,0),
                new DataPoint(6,0)
        });
        graph.addSeries(series);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                switch ((int)data.getX())
                {
                    case 0:
                        return Color.GREEN;
                    case 1:
                        return Color.BLUE;
                    case 2:
                        return Color.RED;
                    case 3:
                        return Color.GRAY;
                    case 4:
                        return Color.YELLOW;
                    case 5:
                        return Color.CYAN;
                    case 6:
                        return Color.BLACK;
                    default:
                        return Color.WHITE;
                }
            }
        });
        series.setSpacing(20);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
    }

    /**
     * Makes the graph
     */
    private void updateGraph(final DataPoint[] data)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                series.resetData(data);
            }
        });
    }



}
