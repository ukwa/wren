/**
 * 
 */
package uk.bl.wa.teacup;

import io.latent.storm.rabbitmq.TupleToMessage;
import backtype.storm.tuple.Tuple;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class UrlTupleToMessage extends TupleToMessage {

    private static final long serialVersionUID = -949046746396956815L;

    @Override
    protected byte[] extractBody(Tuple input) {
        return input.getStringByField("url").getBytes();
    }

    @Override
    protected String determineExchangeName(Tuple input) {
        return "teacup";
    }

    @Override
    protected String specifyContentType(Tuple input) {
        return "text/plain";
    }

    @Override
    protected String specifyContentEncoding(Tuple input) {
        return "UTF-8";
    }

    @Override
    protected boolean specifyMessagePersistence(Tuple input) {
        return true;
    }

}
