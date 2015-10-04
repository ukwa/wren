/**
 * 
 */
package uk.bl.wa.teacup.bolt;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.jwat.common.ContentType;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.jwat.warc.WarcFileNaming;
import org.jwat.warc.WarcFileNamingDefault;
import org.jwat.warc.WarcFileWriter;
import org.jwat.warc.WarcFileWriterConfig;
import org.jwat.warc.WarcHeader;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.util.Calendar;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import uk.bl.wa.teacup.model.CrawlURL;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class HarToWARCWriterBolt implements IRichBolt {

    private static final long serialVersionUID = 6588071684574175562L;

    private static final Logger LOG = LoggerFactory.getLogger(HarToWARCWriterBolt.class);

    private WarcFileWriter writer;

    private OutputCollector _collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
        WarcFileNaming warcFileNaming = new WarcFileNamingDefault(null, null, null, null);
        WarcFileWriterConfig warcFileConfig = new WarcFileWriterConfig(new File("/Users/andy/Documents/workspace/teacup/warcs/"), true, WarcFileWriterConfig.DEFAULT_MAX_FILE_SIZE, false);
        writer = WarcFileWriter.getWarcWriterInstance(warcFileNaming, warcFileConfig);

    }

    @Override
    public void execute(Tuple input) {
        CrawlURL url = (CrawlURL) input.getValueByField("url");
        String har = (String) input.getValueByField("har");

        try {
            boolean nextWriter = writer.nextWriter();
            WarcWriter w = writer.getWriter();
            LOG.info("Writer is " + w);
            WarcRecord wr = WarcRecord.createRecord(w);
            String recordId = "urn:uuid:" + UUID.randomUUID().toString();
            WarcHeader header = wr.header;
            header.warcRecordIdUri = Uri.create(recordId);
            header.warcTypeStr = "metadata";
            header.warcTargetUriStr = url.url;
            header.contentLength = (long) har.length();
            header.contentType = ContentType.parseContentType("application/json");
            header.warcDate = Calendar.getInstance().getTime();
            byte[] digest = { 1 };
            String digestEncoded = "q";
            header.warcBlockDigest = WarcDigest.createWarcDigest("sha1", digest, "hex", digestEncoded);
            w.writeHeader(wr);
            w.writePayload(har.getBytes("UTF-8"));
            w.closeRecord();
        } catch (Exception e) {
            LOG.error("Exception when writing HAR to WARC file.", e);
            _collector.reportError(e);
        }

    }

    @Override
    public void cleanup() {
        try {
            writer.close();
        } catch (IOException e) {
            _collector.reportError(e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

}
