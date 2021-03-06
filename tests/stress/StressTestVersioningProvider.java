package stress;

import junit.framework.*;
import java.io.*;
import java.util.*;

import com.ecyrd.jspwiki.*;
import com.ecyrd.jspwiki.providers.*;

public class StressTestVersioningProvider extends TestCase
{
    public static final String NAME1 = "Test1";

    Properties props = new Properties();

    TestEngine engine;

    public StressTestVersioningProvider( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        props.load( TestEngine.findTestProperties("/jspwiki_vers.properties") );

        props.setProperty( CachingProvider.PROP_CACHECAPACITY, "10000" );
        engine = new TestEngine(props);
    }

    public void tearDown()
    {
        String files = props.getProperty( FileSystemProvider.PROP_PAGEDIR );

        // Remove file
        File f = new File( files, NAME1+FileSystemProvider.FILE_EXT );
        f.delete();

        f = new File( files, "OLD" );

        TestEngine.deleteAll(f);
    }

    public void testMillionChanges()
        throws Exception
    {
        String text = "";
        String name = NAME1;
        int    maxver = 2000; // Save 2000 versions.
        Benchmark mark = new Benchmark();

        mark.start();
        for( int i = 0; i < maxver; i++ )
        {
            text = text + ".";
            engine.saveText( name, text );
        }

        mark.stop();

        System.out.println("Benchmark: "+mark.toString(2000)+" pages/second");
        WikiPage pageinfo = engine.getPage( NAME1 );

        assertEquals( "wrong version", maxver, pageinfo.getVersion() );
        
        // +2 comes from \r\n.
        assertEquals( "wrong text", maxver+2, engine.getText(NAME1).length() );
    }

    private void runMassiveFileTest(int maxpages)
        throws Exception
    {
        String text = "Testing, 1, 2, 3: ";
        String name = NAME1;
        Benchmark mark = new Benchmark();

        System.out.println("Building a massive repository of "+maxpages+" pages...");

        mark.start();
        for( int i = 0; i < maxpages; i++ )
        {
            engine.saveText( name+i, text+i );
        }
        mark.stop();

        System.out.println("Total time to save "+maxpages+" pages was "+mark.toString() );
        System.out.println("Saved "+mark.toString(maxpages)+" pages/second");

        mark.reset();
        
        mark.start();
        Collection pages = engine.getPageManager().getAllPages();
        mark.stop();
        
        System.out.println("Got a list of all pages in "+mark);
        
        mark.reset();
        mark.start();
        
        for( Iterator i = pages.iterator(); i.hasNext(); )
        {
            String foo = engine.getPureText( (WikiPage)i.next() );
            
            assertNotNull( foo );
        }
        mark.stop();

        System.out.println("Read through all of the pages in "+mark);
        System.out.println("which is "+mark.toString(maxpages)+" pages/second");
    }

    public void testMillionFiles1() throws Exception
    {
        runMassiveFileTest(100);
    }
    
    public void testMillionFiles2() throws Exception
    {
        runMassiveFileTest(1000);
    }
    
    public void testMillionFiles3() throws Exception
    {
        runMassiveFileTest(10000);
    }
    /*
    public void testMillionFiles4()throws Exception
    {
        runMassiveFileTest(100000);
    }
    */
    public static Test suite()
    {
        return new TestSuite( StressTestVersioningProvider.class );
    }
}
