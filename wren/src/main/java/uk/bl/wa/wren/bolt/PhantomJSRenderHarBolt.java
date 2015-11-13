/**
 * 
 */
package uk.bl.wa.wren.bolt;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import uk.bl.wa.wren.model.CrawlURL;
import uk.bl.wa.wren.model.RendererOutput;
import uk.bl.wa.wren.render.PhantomJSCliRenderer;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PhantomJSRenderHarBolt implements IRichBolt {
    
    private static final long serialVersionUID = -3492942137871899567L;

    private static final Logger LOG = LoggerFactory.getLogger(PhantomJSRenderHarBolt.class);

    private OutputCollector _collector;

    private PhantomJSCliRenderer phantomjs;

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#prepare(java.util.Map, backtype.storm.task.TopologyContext, backtype.storm.task.OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
        phantomjs = new PhantomJSCliRenderer();
    }

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
     */
    @Override
    public void execute(Tuple input) {
        // Get the URL
        CrawlURL url = (CrawlURL) input.getValueByField("url");
        // Render and emit:
        try {
            // Render:
            String har = phantomjs.renderToHar(url);
            // If NULL, assume URL is not something that should be rendered this
            // way rather than system failure:
            if (har == null) {
                LOG.info("Could not render " + url.url);
            } else {
                // Emit the HAR:
                _collector.emit(input, new Values(url, har));

                // Parse the HAR:
                RendererOutput ro = new RendererOutput(url, har);

                // Emit new URLs:
                for (CrawlURL nurl : ro.discoveredURLs) {
                    _collector.emit("urls", input, new Values(nurl));
                }

                // Emit rendered forms:
                if (ro.renderedContent != null)
                    _collector.emit("renderedContent", input, new Values(url, ro.renderedContent));
                if (ro.renderedImage != null)
                    _collector.emit("renderedImage", input, new Values(url, ro.renderedImage));
            }
            // All has gone well, so ACK:
            _collector.ack(input);
        } catch (IOException | InterruptedException e) {
            _collector.reportError(e);
        }

    }

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#cleanup()
     */
    @Override
    public void cleanup() {

    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("url", "har"));
        declarer.declareStream("urls", new Fields("url"));
        declarer.declareStream("renderedContent", new Fields("url", "content"));
        declarer.declareStream("renderedImage", new Fields("url", "image"));
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#getComponentConfiguration()
     */
    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
