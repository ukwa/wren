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

}
