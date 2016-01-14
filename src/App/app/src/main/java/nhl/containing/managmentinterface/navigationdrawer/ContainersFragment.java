package nhl.containing.managmentinterface.navigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nhl.containing.managmentinterface.activity.MainActivity;
import nhl.containing.managmentinterface.R;
import nhl.containing.managmentinterface.communication.Communicator;
import nhl.containing.managmentinterface.data.*;

import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protocol.*;

/**
 * Fragment for the container list
 */
public class ContainersFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private ContainerArrayAdapter adapter;
    private MainActivity main;

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
        List<ContainerListItem> list = new ArrayList<>();
        adapter = new ContainerArrayAdapter(getActivity(),list);
        setListAdapter(adapter);
        if(MainActivity.getInstance() == null)
        {
            Toast.makeText(getActivity(), R.string.containerlist_error_initialized,Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        main = MainActivity.getInstance();
    }

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy <em>must</em> have a ListView whose id
     * is {@link android.R.id#list android.R.id.list} and can optionally
     * have a sibling view id {@link android.R.id#empty android.R.id.empty}
     * that is to be shown when the list is empty.
     * <p/>
     * <p>If you are overriding this method with your own custom content,
     * consider including the standard layout {@link android.R.layout#list_content}
     * in your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.listfragment, null);
        //final View view = super.onCreateView(inflater, container, savedInstanceState);
        if(view != null)
        {
            ViewTreeObserver vto = view.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                            }
                            ;
                            if (!main.checkAutoRefresh())
                                getActivity().runOnUiThread(main.refreshRunnable);
                        }
                    }).start();
                }
            });
        }
        return view;
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if(getListView() != null)
        {
            getListView().setEmptyView(view.findViewById(R.id.empty));
        }
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
            mListener.onFragmentInteraction(adapter.getItem(position).ID);
        }
    }

    /**
     * Requests new data
     */
    public void setData()
    {
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
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setInstructionType(InstructionType.APP_REQUEST_DATA);
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(4);
        Communicator.getInstance().setRequest(builder.build());
    }

    /**
     * Receives the new data and begins the list update
     * @param block datablock
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
                    Toast.makeText(getActivity(), R.string.containerlist_error_wrong, Toast.LENGTH_SHORT).show();
                    main.completeRefresh.run();
                }
            });
        }
    }


    /**
     * Puts the block adapter in the ArrayAdapter of the list
     * @param listItems list with container adapter
     */
    private void updateList(final List<ContainerDataListItem> listItems)
    {
        final List<ContainerListItem> itemsList = new ArrayList<>();
        for(ContainerDataListItem item : listItems){
            itemsList.add(new ContainerListItem(item.getID()));
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.Update(itemsList);
                main.completeRefresh.run();
            }
        });
    }

    /**
     * Used to search in the ArrayAdapter of the listview
     * @param s search string
     */
    public void find(String s)
    {
        adapter.getFilter().filter(s);
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
