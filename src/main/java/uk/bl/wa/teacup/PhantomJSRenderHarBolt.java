/**
 * 
 */
package uk.bl.wa.teacup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import uk.bl.wa.teacup.model.CrawlURL;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PhantomJSRenderHarBolt implements IRichBolt {
    
    private static final long serialVersionUID = -3492942137871899567L;

    private static final Logger LOG = LoggerFactory.getLogger(PhantomJSRenderHarBolt.class);

    private String phantomjsPath = "phantomjs";

    private String harRenderScript = "/Users/andy/Documents/workspace/teacup/src/main/resources/resources/netsniff-rasterize.js";

    private List<String> selectors = Arrays.asList(new String[] { ":root" });

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
        CrawlURL url = (CrawlURL) input.getValueByField("url");
        //
        LOG.info("Attempting to render " + url);
        try {
            // Set up a tmp file:
            File tmp = File.createTempFile("phantomjs", ".har");
            tmp.deleteOnExit();
            // Set up the process
            List<String> cmd = new ArrayList<String>(Arrays.asList(new String[] { phantomjsPath, harRenderScript, url.url, tmp.getAbsolutePath() }));
            // Add any selectors:
            cmd.addAll(selectors);
            ProcessBuilder pb = new ProcessBuilder(cmd);
            LOG.info("Running: " + pb.command());
            Process p = pb.start();
            // Await...
            p.waitFor();
            LOG.info("Checking results...");
            // Check for errors and exit if there were any:
            String stderr = IOUtils.toString(p.getErrorStream());
            if (stderr != null && stderr.length() > 0) {
                _collector.reportError(new Exception(stderr));
                return;
            }
            // Read output file into string:
            String output = FileUtils.readFileToString(tmp);
            tmp.delete();
            // Emit the output:
            if (output != null && output.length() > 0) {
                LOG.info("Emitting: string of length " + output.length());
                _collector.emit(input, new Values(output));
                // Parse to emit specific elements of the result:
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readValue(output, JsonNode.class);
                // Look for dynamic dependencies, i.e. downloaded resources:
                for (JsonNode node : root.path("log").path("entries")) {
                    JsonNode rurl = node.path("request").path("url");
                    LOG.info("Got requested url: " + rurl.asText());
                    // Mark as embeds:
                    CrawlURL nurl = url.toParent();
                    nurl.url = rurl.asText();
                    nurl.parentUrl = url.url;
                    nurl.pathFromSeed = url.pathFromSeed + "E";
                    // TODO consider using "pageref" to capture 'parent' page.
                    _collector.emit("urls", input, new Values(nurl));
                }
                // Look through the pages:
                for (JsonNode node : root.path("log").path("pages")) {
                    // Look for known links:
                    for (JsonNode map : node.path("map")) {
                        JsonNode rurl = map.path("href");
                        LOG.info("Got requested href: " + rurl.asText());
                        // Mark as links:
                        CrawlURL nurl = url.toParent();
                        nurl.url = rurl.asText();
                        nurl.parentUrl = url.url;
                        nurl.pathFromSeed = url.pathFromSeed + "L";
                        _collector.emit("urls", input, new Values(nurl));
                    }

                    // Extract DOM:
                    JsonNode renderedContentB64 = node.path("renderedContent").path("text");
                    String renderedContent = new String(Base64.decode(renderedContentB64.asText()), "UTF-8");
                    _collector.emit("renderedContent", input, new Values(renderedContent));

                    // Extract root view image:
                    for (JsonNode img : node.path("renderedElements")) {
                        String selector = img.path("selector").asText();
                        if (":root".equals(selector)) {
                            JsonNode pngB64 = img.path("content");
                            byte[] image = Base64.decode(pngB64.asText());
                            _collector.emit("renderedImage", input, new Values(image));
                        }
                    }
                }
            } else {
                LOG.info("No output created for: " + url);
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
        declarer.declare(new Fields("word"));
        declarer.declareStream("urls", new Fields("url"));
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#getComponentConfiguration()
     */
    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
