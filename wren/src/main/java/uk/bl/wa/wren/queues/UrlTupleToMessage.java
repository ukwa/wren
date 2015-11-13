/**
 * 
 */
package uk.bl.wa.wren.queues;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonProcessingException;

import backtype.storm.tuple.Tuple;
import io.latent.storm.rabbitmq.TupleToMessage;
import uk.bl.wa.wren.model.CrawlURL;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class UrlTupleToMessage extends TupleToMessage {

    private static final long serialVersionUID = -949046746396956815L;

    @Override
    protected byte[] extractBody(Tuple input) {
        CrawlURL curl = (CrawlURL) input.getValueByField("url");
        try {
            return curl.toJson().getBytes("UTF-8");
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String determineExchangeName(Tuple input) {
        return "teacup";
    }

    @Override
    protected String specifyContentType(Tuple input) {
        return "application/json";
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
