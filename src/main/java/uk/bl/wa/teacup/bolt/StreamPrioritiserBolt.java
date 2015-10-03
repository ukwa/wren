/**
 * 
 */
package uk.bl.wa.teacup.bolt;

import java.util.Iterator;
import java.util.Map;

import com.digitalpebble.storm.crawler.Metadata;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class StreamPrioritiserBolt extends BaseRichBolt {

    private static final long serialVersionUID = -9214076246349301273L;

    private OutputCollector _collector;

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#prepare(java.util.Map, backtype.storm.task.TopologyContext, backtype.storm.task.OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    /**
     * Set up a tick tuple for stream rate measurement
     */
    @Override
    public Map<String, Object> getComponentConfiguration() {
        // configure how often a tick tuple will be sent to our bolt
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 5 * 60);
        return conf;
    }

    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
     */
    @Override
    public void execute(Tuple input) {

        try {
            if (isTickTuple(input)) {
                // _cache.rotate();
                return;
            }

            // do your bolt stuff
            //
            //
            Iterator<String> iterator = input.getFields().iterator();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                if ("url".equals(fieldName)) {
                    _collector.emit("urls", input, new Values());
                }
                Object obj = input.getValueByField(fieldName);

                if (obj instanceof byte[])
                    System.out.println(fieldName + "\t" + input.getBinaryByField(fieldName).length + " bytes");
                else if (obj instanceof Metadata) {
                    Metadata md = (Metadata) obj;
                    System.out.println(md.toString(fieldName + "."));
                } else {
                    String value = input.getValueByField(fieldName).toString();
                    System.out.println(fieldName + "\t" + value);
                }

            }

            _collector.ack(input);

        } catch (Exception e) {
            // log.error("Bolt execute error: {}", e);
            _collector.reportError(e);
        }


    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

    protected static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }
}
