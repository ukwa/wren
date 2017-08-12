/**
 * 
 */
package uk.bl.wa.wren.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.archive.modules.CrawlURI;
import org.archive.modules.extractor.LinkContext;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * Broadly based on <a href=
 * "https://github.com/internetarchive/heritrix3/blob/master/contrib/src/main/java/org/archive/crawler/frontier/AMQPUrlReceiver.java#L352>Heritrix3's
 * AMQP model</a>
 * 
 * Then with extensions based on the HAR format.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class CrawlURL implements Serializable {

    private static final long serialVersionUID = 7452432417457114150L;

    public String url = "";

    public String method;

    public String httpVersion;

    public List<String> cookies = new ArrayList<String>();

    public Map<String, String> headers = new HashMap<String, String>();

    public String parentUrl = "";

    public String pathFromSeed = "S";

    public boolean forceFetch = false;

    public boolean isSeed = false;

    @JsonProperty
    public ParentUrlMetadata parentUrlMetadata = new ParentUrlMetadata();

    public CrawlURL toParent() {
        CrawlURL nurl = new CrawlURL();
        nurl.parentUrl = this.url;
        nurl.parentUrlMetadata.pathFromSeed = this.pathFromSeed;
        return nurl;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(this);
    }

    public static CrawlURL fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CrawlURL.class);
    }

    public static CrawlURL fromCrawlURI(CrawlURI curi) {
        CrawlURL nurl = new CrawlURL();
        nurl.url = curi.getURI();
        nurl.method = "GET";
        nurl.httpVersion = "1.0";
        nurl.pathFromSeed = curi.getPathFromSeed();
        nurl.forceFetch = curi.forceFetch();
        nurl.isSeed = curi.isSeed();
        return nurl;
    }

    public static CrawlURI toCrawlURI(CrawlURL curi) throws URIException {
        UURI uuri = UURIFactory.getInstance(curi.url);
        CrawlURI nuri = new CrawlURI(uuri, curi.pathFromSeed, null,
                LinkContext.NAVLINK_MISC);
        nuri.setSeed(curi.isSeed);
        nuri.setForceFetch(curi.forceFetch);
        return nuri;
    }
}
