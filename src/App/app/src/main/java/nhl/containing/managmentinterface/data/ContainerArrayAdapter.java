package nhl.containing.managmentinterface.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nhl.containing.managmentinterface.R;
import nhl.containing.networking.protobuf.AppDataProto.*;

/**
 * Created by Niels on 23-11-2015.
 */
public class ContainerArrayAdapter extends ArrayAdapter<ContainerDataListItem>
{
    private final Context context;
    private List<ContainerDataListItem> items;

    public ContainerArrayAdapter(Context context, List<ContainerDataListItem> items)
    {
        super(context, R.layout.listitem,items);
        this.items = items;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listitem,parent,false);
        TextView textView = (TextView)rowView.findViewById(R.id.list_item_text);
        textView.setText("Container " + items.get(position).getID());
        return rowView;
    }

    public void Update(List<ContainerDataListItem> items)
    {
        this.items = items;
        this.notifyDataSetChanged();
    }
}
