package nhl.containing.managmentinterface.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nhl.containing.managmentinterface.MainActivity;
import nhl.containing.managmentinterface.R;
import nhl.containing.networking.protobuf.AppDataProto.*;

/**
 * Array adapter for the containerlist
 */
public class ContainerArrayAdapter extends ArrayAdapter<ContainerDataListItem>
{
    private final Context context;

    /**
     * Constructor for the array adapter
     * @param context contexts
     * @param items list with items
     */
    public ContainerArrayAdapter(Context context, List<ContainerDataListItem> items)
    {
        super(context, R.layout.listitem,items);
        this.context = context;
    }

    /**
     * Gets the view for an item in the list
     * @param position position of the item
     * @param convertView convert view
     * @param parent parent viewgroup
     * @return view of the item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem,parent,false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.list_item_text);
        textView.setText("Container " + this.getItem(position).getID());
        return convertView;
    }

    /**
     * Checks if item on list is enabled
     * @param position position of the item
     * @return true when enabled and not refreshing, else false
     */
    @Override
    public boolean isEnabled(int position) {
        return !(MainActivity.getInstance() != null && MainActivity.getInstance().getRefreshStatus()) && super.isEnabled(position);
    }

    /**
     * Updates the list
     * @param items list with new items
     */
    public void Update(List<ContainerDataListItem> items)
    {
        this.clear();
        this.addAll(items);
        this.notifyDataSetChanged();
    }


}
