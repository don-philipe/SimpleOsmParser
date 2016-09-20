package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMWay;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Philipp Thöricht
 */
public class OsmParserTest
{
    @Test
    public void testSamePathway()
    {
        OsmParser op = new OsmFileParser();
        OSMNode n1 = new OSMNode();
        n1.setId(1);
        n1.setLat(1.1);
        n1.setLon(2.2);
        op.putNode(n1);
        OSMNode n2 = new OSMNode();
        n2.setId(2);
        n2.setLat(1.2);
        n2.setLon(2.3);
        op.putNode(n2);
        OSMNode n3 = new OSMNode();
        n3.setId(3);
        n3.setLat(1.3);
        n3.setLon(2.4);
        op.putNode(n3);
        OSMWay w1 = new OSMWay();
        w1.setId(1);
        w1.addRefToEnd(1);
        w1.addRefToEnd(2);
        w1.addRefToEnd(3);
        op.putWay(w1);
        OSMWay w2 = new OSMWay();
        w2.setId(2);
        w2.addRefToEnd(1);
        w2.addRefToEnd(2);
        w2.addRefToEnd(3);
        op.putWay(w2);
        
        assertTrue(op.samePathway(1, 2));
        
        OSMWay w3 = new OSMWay();
        w3.setId(3);
        w3.addRefToEnd(3);
        w3.addRefToEnd(2);
        w3.addRefToEnd(1);
        op.putWay(w3);
        
        assertFalse(op.samePathway(1, 3));
        
        w1.addRefToEnd(2);
        
        assertFalse(op.samePathway(1, 2));
    }
}
