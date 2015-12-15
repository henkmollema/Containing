package nhl.containing.managmentinterface.navigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nhl.containing.managmentinterface.R;

/**
 * Drawerlist adapter for the navigation drawer
 */
public class DrawerListAdapter extends BaseAdapter
{
    Context mContext;
    ArrayList<NavItem> mNavItems;

    /**
     * Creates an Drawerlist adapter
     * @param context the context
     * @param navItems the items of the drawer
     */
    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems){
        mContext = context;
        mNavItems = navItems;
    }

    /**
     * returns the amount of items
     * @return amount
     */
    @Override
    public int getCount() {
        return mNavItems.size();
    }

    /**
     * returns the object a position
     * @param position position
     * @return the navitem
     */
    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    /**
     * [Not implemented]
     * @param position position
     * @return zero
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Gets the view of a certain position
     * @param position position
     * @param convertView the convert view
     * @param parent the parent
     * @return the view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item,null);
        }
        else
        {
            view = convertView;
        }
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        titleView.setText(mNavItems.get(position).mTitle);
        subtitleView.setText(mNavItems.get(position).mSubTitle);
        iconView.setImageResource(mNavItems.get(position).mIcon);
        return view;
    }
}
