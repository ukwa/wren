/**
 * 
 */
package uk.bl.wa.teacup.render;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.bl.wa.teacup.model.CrawlURL;

/**
 * 
 * Renders URLs through PhantomJS via the command-line API.
 * 
 * TODO Make location of harRenderScript more sensible/not hardcoded.
 * 
 * TODO optionally point to an archiving proxy. --proxy=192.168.1.42:8080
 * 
 * @see http://phantomjs.org/api/command-line.html
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PhantomJSCliRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(PhantomJSCliRenderer.class);

    private String phantomjsPath = "phantomjs";

    private String harRenderScript = "/Users/andy/Documents/workspace/teacup/src/main/resources/resources/netsniff-rasterize.js";

    private List<String> selectors = Arrays.asList(new String[] { ":root" });

    public String renderToHar(CrawlURL url) throws IOException, InterruptedException {
        LOG.info("Attempting to render " + url);
        // Set up a tmp file:
        File tmp = File.createTempFile("phantomjs", ".har");
        tmp.deleteOnExit();
        // Set up the process
        List<String> cmd = new ArrayList<String>(Arrays.asList(new String[] {
                phantomjsPath, "--ssl-protocol=any", "--ignore-ssl-errors=true",
                harRenderScript, url.url, tmp.getAbsolutePath() }));
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
            return null;
        }
        // Read output file into string:
        String output = FileUtils.readFileToString(tmp);
        tmp.delete();
        // Emit the output:
        if (output != null && output.length() > 0) {
            LOG.info("Emitting: HAR - string of length " + output.length());
            return output;
        } else {
            LOG.info("No output created for: " + url);
        }
        return null;
    }

    public String getPhantomjsPath() {
        return phantomjsPath;
    }

    public void setPhantomjsPath(String phantomjsPath) {
        this.phantomjsPath = phantomjsPath;
    }

    public String getHarRenderScript() {
        return harRenderScript;
    }

    public void setHarRenderScript(String harRenderScript) {
        this.harRenderScript = harRenderScript;
    }

    public List<String> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<String> selectors) {
        this.selectors = selectors;
    }

}
