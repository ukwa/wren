/**
 * 
 */
package uk.bl.wa.teacup.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class ParentUrlMetadata implements Serializable {

    private static final long serialVersionUID = -7113181332360596211L;

    String pathFromSeed;

    Map<String, String> heritableData;

}
