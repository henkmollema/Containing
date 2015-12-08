package nhl.containing.managmentinterface.navigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nhl.containing.managmentinterface.MainActivity;
import nhl.containing.managmentinterface.data.ClassBridge;
import nhl.containing.managmentinterface.data.ContainerArrayAdapter;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Fragment for the containerlist
 */
public class ContainersFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private ContainerArrayAdapter items;
    private MainActivity main;
    private volatile boolean isVisible = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContainersFragment() {
    }

    /**
     * Creates the fragement
     * @param savedInstanceState bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<ContainerDataListItem> list = new ArrayList<>();
        items = new ContainerArrayAdapter(getActivity(),list);
        setListAdapter(items);
        if(MainActivity.getInstance() == null)
        {
            Toast.makeText(getActivity(),"Containerfragement couldn't be initialized",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        main = MainActivity.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isVisible){}
                if(!main.checkAutoRefresh())
                    getActivity().runOnUiThread(main.refreshRunnable);
            }
        }).start();
    }

    /**
     * Shows the fragment
     */
    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
    }

    /**
     * Attachs a contexts to the fragment
     * @param context contexts
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            try {
                mListener = (OnFragmentInteractionListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    /**
     * On detach event
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Event when clicked on an item in the list
     * @param l list
     * @param v view
     * @param position the position
     * @param id the id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(items.getItem(position).getID());
        }
    }

    /**
     * Requests new data
     */
    public void setData()
    {
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
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setInstructionType(InstructionType.APP_REQUEST_DATA);
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(4);
        ClassBridge.communicator.setRequest(builder.build());
    }

    /**
     * Receives the new data and begins the list update
     * @param block
     */
    public void UpdateGraph(datablockApp block)
    {
        try
        {
            if(!block.getItemsList().isEmpty())
            {
                updateList(block.getItemsList());
            }
        }
        catch (Exception e){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    main.completeRefresh.run();
                }
            });
        }
    }


    /**
     * Puts the block items in the ArrayAdapter of the list
     * @param listItems list with container items
     */
    private void updateList(final List<ContainerDataListItem> listItems)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                items.Update(listItems);
                main.completeRefresh.run();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id);
    }

}
