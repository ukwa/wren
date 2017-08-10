package uk.bl.wa.wren.schemes;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;

public class StringScheme implements Scheme {

    private static final long serialVersionUID = 827397701726849709L;

    public Fields getOutputFields() {
        return new Fields("str");
    }

    @Override
    public List<Object> deserialize(ByteBuffer ser) {
        try {
            return new org.apache.storm.tuple.Values(
                    new String(ser.array(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
