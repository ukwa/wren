/**
 * 
 */
package uk.bl.wa.teacup.bolt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import uk.bl.wa.teacup.model.CrawlURL;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class HarToWARCWriterBoltTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    public Tuple createHarTuple(CrawlURL url, String har) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.getValueByField("url")).thenReturn(url);
        when(tuple.getValueByField("har")).thenReturn(har);
        return tuple;
    }

    @Test
    public void testSimpleWarcWriting() {
        HarToWARCWriterBolt hw = new HarToWARCWriterBolt();
        hw.prepare(null, null, mock(OutputCollector.class));

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
