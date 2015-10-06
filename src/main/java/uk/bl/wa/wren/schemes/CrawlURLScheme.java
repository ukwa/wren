package uk.bl.wa.wren.schemes;

import java.io.IOException;
import java.util.List;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import uk.bl.wa.wren.model.CrawlURL;

public class CrawlURLScheme implements Scheme {

    private static final long serialVersionUID = 827397701726849709L;

    public List<Object> deserialize(byte[] bytes) {
        try {
            return new Values(CrawlURL.fromJson(new String(bytes, "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Fields getOutputFields() {
        return new Fields("url");
    }
}
