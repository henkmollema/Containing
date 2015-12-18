package nhl.containing.managmentinterface.data;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Filter for searching the ContainerListItems
 * Created by Niels on 18-12-2015.
 */
public class ContainerFilter extends Filter
{
    private ContainerArrayAdapter adapter;

    /**
     * Constructor
     * @param adapter the Arrayadapter with the data
     */
    public ContainerFilter(ContainerArrayAdapter adapter)
    {
        this.adapter = adapter;
    }

    /**
     * <p>Invoked in a worker thread to filter the data according to the
     * constraint. Subclasses must implement this method to perform the
     * filtering operation. Results computed by the filtering operation
     * must be returned as a {@link FilterResults} that
     * will then be published in the UI thread through
     * {@link #publishResults(CharSequence,
     * FilterResults)}.</p>
     * <p/>
     * <p><strong>Contract:</strong> When the constraint is null, the original
     * data must be restored.</p>
     *
     * @param constraint the constraint used to filter the data
     * @return the results of the filtering operation
     * @see #filter(CharSequence, FilterListener)
     * @see #publishResults(CharSequence, FilterResults)
     * @see FilterResults
     */
    @Override
    protected FilterResults performFiltering(CharSequence constraint)
    {
        FilterResults results = new FilterResults();
        List<ContainerListItem> items = new ArrayList<>();
        if(constraint != null)
        {
            for(int i = 0; i < adapter.getCount(); i++)
            {
                ContainerListItem item = adapter.getItem(i);
                if(Integer.toString(item.ID).contains(constraint))
                    items.add(item);
            }
        }
        else
        {
            items = adapter.original;
        }
        results.values = items;
        results.count = items.size();
        return results;
    }

    /**
     * <p>Invoked in the UI thread to publish the filtering results in the
     * user interface. Subclasses must implement this method to display the
     * results computed in {@link #performFiltering}.</p>
     *
     * @param constraint the constraint used to filter the data
     * @param results    the results of the filtering operation
     * @see #filter(CharSequence, FilterListener)
     * @see #performFiltering(CharSequence)
     * @see FilterResults
     */
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results)
    {
        List<ContainerListItem> items = (ArrayList<ContainerListItem>)results.values;
        if(items == null){
            adapter.notifyDataSetInvalidated();
            return;
        }
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
