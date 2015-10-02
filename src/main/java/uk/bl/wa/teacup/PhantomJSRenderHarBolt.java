/**
 * 
 */
package uk.bl.wa.teacup;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PhantomJSRenderHarBolt implements IRichBolt {
    
    private static final long serialVersionUID = -3492942137871899567L;

    private static final Logger LOG = LoggerFactory.getLogger(PhantomJSRenderHarBolt.class);

    private String phantomjsPath = "phantomjs";

    private String harRenderScript = "netsniff-rasterize.js";

    private OutputCollector _collector;

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#prepare(java.util.Map, backtype.storm.task.TopologyContext, backtype.storm.task.OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
     */
    @Override
    public void execute(Tuple input) {
        // Get the URL
        String url = input.getString(0);
        try {
            // Set up a tmp file:
            File tmp = File.createTempFile("phantomjs", ".har");
            tmp.deleteOnExit();
            // Set up the process
            Process p = new ProcessBuilder(phantomjsPath, harRenderScript, url, tmp.getAbsolutePath()).start();
            // Await...
            p.waitFor();
            // Read output file into string:
            String output = FileUtils.readFileToString(tmp);
            tmp.delete();
            // Emit the output:
            if (output != null && output.length() > 0) {
                LOG.info("Emitting: " + output);
                _collector.emit(input, new Values(output));
            }
            // Ack
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
        declarer.declare(new Fields("word"));
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#getComponentConfiguration()
     */
    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
