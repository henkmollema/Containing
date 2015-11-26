package nhl.containing.managmentinterface;

import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.*;

import nhl.containing.managmentinterface.navigationdrawer.GraphFragment;

/**
 * Created by Niels on 26-11-2015.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity>{

    private MainActivity mainactivity;

    public MainActivityTest()
    {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.Material);
        setActivityContext(context);
        startActivity(new Intent(getInstrumentation().getTargetContext(),MainActivity.class),null,null);
        mainactivity = getActivity();
    }

    public void tearDown() throws Exception {

    }

    @MediumTest
    public void testOnCreate()
    {
        boolean testing1 = mainactivity.mDrawerList != null;
        boolean testing2 = mainactivity.mDrawerPane != null;
        boolean testing3 = mainactivity.mNavItems.size() != 0;
        boolean testing4 = ((GraphFragment)mainactivity.fragment).getId() == 1;
        assertEquals(testing1 && testing2 && testing3 && testing4, true);
    }

    @MediumTest
    public void testOnCreateOptionsMenu()
    {
        boolean testing1 = mainactivity.menu != null;
        boolean testing2 = mainactivity.menu.size() > 0;
        assertEquals(testing1 && testing2, true);
    }

    @MediumTest
    public void testOnPrepareOptionsMenu()
    {
        boolean testing1 = mainactivity.menu.findItem(R.id.action_refresh).isVisible();
        boolean testing2 = mainactivity.menu.findItem(R.id.action_refresh_time).isVisible();
        boolean testing3 = mainactivity.menu.findItem(R.id.action_legend).isVisible();
        boolean testing4 = mainactivity.menu.findItem(R.id.action_settings).isVisible();
        assertEquals(testing1 && testing2 && testing3 && testing4,true);
    }



}