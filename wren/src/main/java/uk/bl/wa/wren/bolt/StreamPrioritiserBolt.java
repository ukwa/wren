/**
 * 
 */
package uk.bl.wa.wren.bolt;

import java.util.Iterator;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.digitalpebble.stormcrawler.Metadata;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class StreamPrioritiserBolt extends BaseTickTupleAwareRichBolt {

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

    @Override
    protected void process(Tuple input) {
        try {
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.storm.topology.base.BaseTickTupleAwareRichBolt#onTickTuple(org
     * .apache.storm.tuple.Tuple)
     */
    @Override
    protected void onTickTuple(Tuple tuple) {
        super.onTickTuple(tuple);
        // _cache.rotate();
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

}
