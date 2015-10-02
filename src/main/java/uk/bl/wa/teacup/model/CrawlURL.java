/**
 * 
 */
package uk.bl.wa.teacup.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * Broadly based on
 * https://github.com/internetarchive/heritrix3/blob/master/contrib/src/main/
 * java/org/archive/crawler/frontier/AMQPUrlReceiver.java#L352
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class CrawlURL implements Serializable {

    private static final long serialVersionUID = 7452432417457114150L;

    public String url = "";

    public String parentUrl = "";

    public String pathFromSeed = "S";

    public Map<String, String> headers = new HashMap<String, String>();

    @JsonProperty
    public ParentUrlMetadata parentUrlMetadata = new ParentUrlMetadata();

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(this);
    }

    public static CrawlURL fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CrawlURL.class);
    }
}
