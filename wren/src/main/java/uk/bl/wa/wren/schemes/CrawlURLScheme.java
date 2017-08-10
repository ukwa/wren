package uk.bl.wa.wren.schemes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import uk.bl.wa.wren.model.CrawlURL;

public class CrawlURLScheme implements Scheme {

    private static final long serialVersionUID = 827397701726849709L;

    public Fields getOutputFields() {
        return new Fields("url");
    }

    @Override
    public List<Object> deserialize(ByteBuffer ser) {
        try {
            return new Values(
                    CrawlURL.fromJson(new String(ser.array(), "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
