package nhl.containing.managmentinterface;

import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;

import junit.framework.TestCase;

/**
 * Created by Niels on 26-11-2015.
 */
public class ContainerActivityTest extends ActivityUnitTestCase<ContainerActivity> {

    private ContainerActivity containerActivity;
    public ContainerActivityTest()
    {
        super(ContainerActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.Material);
        setActivityContext(context);
        Intent i = new Intent(getInstrumentation().getTargetContext(),ContainerActivity.class);
        i.putExtra("ID",1);
        startActivity(i,null,null);
        containerActivity = getActivity();
    }

    public void testOnCreate() throws Exception {
        boolean testing1 = containerActivity.ID == 1;
        assertEquals(testing1,true);
    }
}