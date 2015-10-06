/**
 * 
 */
package uk.bl.wa.teacup.bolt;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import uk.bl.wa.wren.bolt.PhantomJSRenderHarBolt;
import uk.bl.wa.wren.model.CrawlURL;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PhantomJSRenderHarBoltIntegrationTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(PhantomJSRenderHarBoltIntegrationTest.class);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    private Tuple createUrlTuple(CrawlURL url) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.getValueByField("url")).thenReturn(url);
        return tuple;
    }


    @Test
    public void testLocalRender() throws IOException {
        PhantomJSRenderHarBolt bolt = new PhantomJSRenderHarBolt();

        // Setup
        OutputCollector collector = mock(OutputCollector.class);
        bolt.prepare(null, null, collector);

        // Execute
        CrawlURL url = new CrawlURL();
        url.url = "http://localhost:8080/archivalAcidTest/";
        Tuple input = this.createUrlTuple(url);
        bolt.execute(input);

        // Check the results:
        verify(collector).emit(eq(input), any(Values.class));

        // Actually get the default stream outputs:
        ArgumentCaptor<Values> argumentCaptor = ArgumentCaptor
                .forClass(Values.class);
        verify(collector, atLeastOnce()).emit(eq(input),
                argumentCaptor.capture());
        //
        Values v = argumentCaptor.getAllValues().get(0);
        CrawlURL ourl = (CrawlURL) v.get(0);
        String ohar = (String) v.get(1);
        LOG.info("Test " + ourl.url);
        LOG.info("Test " + ohar.substring(0, 100));

        // Get the default PNG outputs:
        argumentCaptor.getAllValues().clear();
        verify(collector, atLeastOnce()).emit(eq("renderedImage"), eq(input),
                argumentCaptor.capture());
        //
        v = argumentCaptor.getAllValues().get(0);
        CrawlURL ourl2 = (CrawlURL) v.get(0);
        byte[] opng = (byte[]) v.get(1);
        LOG.info("Test " + ourl2.url);
        LOG.info("Test " + opng);
        // Write to a file:
        FileOutputStream output = new FileOutputStream(new File("aat.png"));
        IOUtils.write(opng, output);

    }

}
