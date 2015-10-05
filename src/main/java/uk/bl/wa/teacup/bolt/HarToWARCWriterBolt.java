/**
 * 
 */
package uk.bl.wa.teacup.bolt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

import org.jwat.common.Base32;
import org.jwat.common.Base64;
import org.jwat.common.ContentType;
import org.jwat.common.Uri;
import org.jwat.warc.WarcConstants;
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

    private static final Logger LOG = LoggerFactory
            .getLogger(HarToWARCWriterBolt.class);

    private WarcFileWriter writer;

    private OutputCollector _collector;

    private String outputFolder = "warcs";

    private String filePrefix = "BL-TEACUP";

    private String hostname = "localhost";

    private boolean useBase32 = true;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
            OutputCollector collector) {
        // Remember the collector
        _collector = collector;
        // extend prefix using entity ID to guarentee no collisions:
        String componentId = context.getThisComponentId();
        int taskId = context.getThisTaskId();
        this.filePrefix = this.filePrefix + "-" + componentId + "-" + taskId;
        // Set up the writer
        WarcFileNaming warcFileNaming = new WarcFileNamingDefault(filePrefix,
                null, hostname, null);
        WarcFileWriterConfig warcFileConfig = new WarcFileWriterConfig(
                new File(outputFolder), true,
                WarcFileWriterConfig.DEFAULT_MAX_FILE_SIZE, false);
        writer = WarcFileWriter.getWarcWriterInstance(warcFileNaming,
                warcFileConfig);
    }

    @Override
    public void execute(Tuple input) {
        CrawlURL url = (CrawlURL) input.getValueByField("url");
        String har = (String) input.getValueByField("har");

        try {
            boolean nextWriter = writer.nextWriter();
            WarcWriter w = writer.getWriter();
            // Add warcinfo if it's a new file:
            if (nextWriter) {
                String filename = writer.getFile().getName();
                filename = filename.replaceFirst("\\.open$", "");
                LOG.info("Started writing file " + filename);
                this.createWarcInfoRecord(w, filename);
            }
            // Make the HAR record:
            WarcRecord wr = WarcRecord.createRecord(w);
            WarcHeader header = wr.header;
            header.warcRecordIdUri = createRecordID();
            header.warcTypeIdx = WarcConstants.RT_IDX_METADATA;
            header.warcTargetUriStr = url.url;
            header.contentLength = (long) har.length();
            header.contentType = ContentType
                    .parseContentType("application/json");
            header.warcDate = Calendar.getInstance().getTime();
            byte[] payload = har.getBytes("UTF-8");
            this.addDigests(payload, header);
            w.writeHeader(wr);
            w.writePayload(payload);
            w.closeRecord();
            // All has gone well, so ACK:
            _collector.ack(input);
        } catch (Exception e) {
            LOG.error("Exception when writing HAR to WARC file.", e);
            _collector.reportError(e);
        }

    }

    private void addDigests(byte[] payload, WarcHeader header)
            throws NoSuchAlgorithmException {

        // Digest:
        MessageDigest md = MessageDigest.getInstance("sha1");
        md.update(payload);
        byte[] digest = md.digest();

        // Format the digest:
        if (useBase32) {
            String digestEncoded = Base32.encodeArray(digest);
            header.warcBlockDigest = WarcDigest.createWarcDigest("sha1", digest,
                "Base32", digestEncoded);
            header.warcPayloadDigest = header.warcBlockDigest;
        } else {
            String digestEncoded = Base64.encodeArray(payload);
            header.warcBlockDigest = WarcDigest.createWarcDigest("sha1", digest,
                    "Base64", digestEncoded);
        }
    }

    private Uri createRecordID() {
        return Uri.create("urn:uuid:" + UUID.randomUUID().toString());
    }

    public void createWarcInfoRecord(WarcWriter wr, String filename)
            throws IOException, NoSuchAlgorithmException {
        WarcRecord record = WarcRecord.createRecord(wr);
        WarcHeader header = record.header;
        header.warcTypeIdx = WarcConstants.RT_IDX_WARCINFO;
        header.warcDate = Calendar.getInstance().getTime();
        header.warcRecordIdUri = createRecordID();
        header.warcFilename = filename;
        header.contentType = ContentType
                .parseContentType("application/warc-fields");
        String description = "software: Teacup Version 1.0.0 https://github.com/ukwa\n"
                + "description: generated via PhantomJS\n"
                + "format: WARC file version 1.0\n";
        byte[] descriptionBytes = description.getBytes("UTF-8");
        record.header.addHeader("Content-Length",
                Long.toString(descriptionBytes.length));
        this.addDigests(descriptionBytes, header);
        wr.writeHeader(record);
        ByteArrayInputStream inBytes = new ByteArrayInputStream(
                descriptionBytes);
        wr.streamPayload(inBytes);
        wr.closeRecord();
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

    // -- Getters/Setter--

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

}
