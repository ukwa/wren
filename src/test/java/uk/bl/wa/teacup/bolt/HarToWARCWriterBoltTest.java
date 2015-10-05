/**
 * 
 */
package uk.bl.wa.teacup.bolt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import uk.bl.wa.teacup.model.CrawlURL;

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
            this.warcOutputPath = "warcs/";
        } else {
            this.warcOutputPath = this.warcOutputPath + "/warcs/";
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
        when(context.getThisComponentId()).thenReturn("1");
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

}
