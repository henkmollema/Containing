package nhl.containing.managmentinterface.navigationdrawer;

import java.util.NavigableMap;

/**
 * Nav item class for the navigation drawer
 */
public class NavItem
{
    String mTitle;
    String mSubTitle;
    int mIcon;

    /**
     * Creates a navitem
     * @param title titel of the item
     * @param subTitle the subtitel
     * @param icon the icon
     */
    public NavItem(String title,String subTitle,int icon)
    {
        mTitle = title;
        mSubTitle = subTitle;
        mIcon = icon;
    }
}
