/**
 * 
 */
package uk.bl.wa.wren.bolt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.bl.wa.wren.bolt.HarToWARCWriterBolt;
import uk.bl.wa.wren.model.CrawlURL;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class HarToWARCWriterBoltTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(HarToWARCWriterBoltTest.class);

    private String warcOutputPath;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        warcOutputPath = System.getProperty("maven.basedir");
        if (warcOutputPath == null) {
            this.warcOutputPath = "target/";
        } else {
            this.warcOutputPath = this.warcOutputPath + "/target/";
        }
    }

    public Tuple createHarTuple(CrawlURL url, String har) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.getValueByField("url")).thenReturn(url);
        when(tuple.getValueByField("har")).thenReturn(har);
        return tuple;
    }

    @Test
    public void testSimpleWarcWriting() {
        //
        HarToWARCWriterBolt hw = new HarToWARCWriterBolt();
        hw.setOutputFolder(warcOutputPath);
        LOG.info("Set filePrefix: " + hw.getOutputFolder());

        //
        TopologyContext context = mock(TopologyContext.class);
        when(context.getThisComponentId()).thenReturn("har-writer");
        when(context.getThisTaskId()).thenReturn(1);
        hw.prepare(null, context, mock(OutputCollector.class));

        CrawlURL url = new CrawlURL();
        url.url = "http://example.org/";
        String har = "{\"har\"}";
        Tuple input = this.createHarTuple(url, har);

        // And run it:
        hw.execute(input);

        // And clean up:
        hw.cleanup();
    }

    @Test
    public void lengthCheck() throws UnsupportedEncodingException {
        // The input string for this test
        char end = 0x0d;
        final String string = "Hello World" + end;
        System.out.println("'" + string + "'");

        // Check length, in characters
        System.out.println(string.length()); // prints "11"

        // Check encoded sizes
        final byte[] utf8Bytes = string.getBytes("UTF-8");
        System.out.println(utf8Bytes.length); // prints "11"
    }

}
