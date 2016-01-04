package nhl.containing.tests;

import java.awt.Dimension;
import java.io.File;
import static org.junit.Assert.*;
import nhl.containing.controller.PathFinder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PathfindingTests
{
    static
    {
        File resFile = new File(System.getProperty("java.io.tmpdir"), "JNITest.dll");
        System.load(resFile.getAbsolutePath());
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void NothingWrong()
    {
        PathFinder.initPath(new Dimension(25, 25));
        int[] path = PathFinder.getPath(0, 25*25-1);
        assertEquals(0, path[path.length - 1]);
        assertEquals(25*25-1, path[0]);
        assertNotNull(path);
        PathFinder.cleanup();
    }
    
    @Test
    public void OriginNotInRoadmap()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("origin is not in the roadmap");
        PathFinder.initPath(new Dimension(25, 25));
        int[] path = PathFinder.getPath(25*25, 130);
        assertNull(path);
        PathFinder.cleanup();
    }
    
    @Test
    public void DestNotInRoadmap()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("destination is not in the roadmap");
        PathFinder.initPath(new Dimension(25, 25));
        int[] path = PathFinder.getPath(0, 25*25);
        assertNull(path);
        PathFinder.cleanup();
    }
}
