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
	
	@Test
	public void testMergeParsers()
	{
		OsmTestParser otp1 = new OsmTestParser();
		OSMNode n = new OSMNode();
		n.setId(23L);
		n.setTag("node", "1");
		otp1.nodes.put(n.getId(), n);
		n = new OSMNode();
		n.setId(42L);
		n.setTag("node", "2");
		otp1.nodes.put(n.getId(), n);
		n = new OSMNode();
		n.setId(1337L);
		n.setTag("node", "3");
		otp1.nodes.put(n.getId(), n);
		
		assertEquals(3, otp1.getNodes().size());
		assertTrue(otp1.getNodes().get(23L).hasTag("node", "1"));
		assertTrue(otp1.getNodes().get(42L).hasTag("node", "2"));
		assertTrue(otp1.getNodes().get(1337L).hasTag("node", "3"));
		
		OsmTestParser otp2 = new OsmTestParser();
		n = new OSMNode();
		n.setId(23L);
		n.setTag("node", "4");
		otp2.nodes.put(n.getId(), n);
		n = new OSMNode();
		n.setId(42L);
		n.setTag("node", "5");
		otp2.nodes.put(n.getId(), n);
		n = new OSMNode();
		n.setId(1337L);
		n.setTag("node", "6");
		otp2.nodes.put(n.getId(), n);
		
		assertEquals(3, otp2.getNodes().size());
		assertTrue(otp2.getNodes().get(23L).hasTag("node", "4"));
		assertTrue(otp2.getNodes().get(42L).hasTag("node", "5"));
		assertTrue(otp2.getNodes().get(1337L).hasTag("node", "6"));
		
		otp1.mergeParsers(otp2, false);
		
		assertEquals(3, otp2.getNodes().size());
		assertTrue(otp2.getNodes().containsKey(1338L));
		String tag = otp2.getNodes().get(1338L).getTag("node");
		assertTrue("456".contains(tag));
		assertTrue(otp2.getNodes().containsKey(1339L));
		tag = otp2.getNodes().get(1339L).getTag("node");
		assertTrue("456".contains(tag));
		assertTrue(otp2.getNodes().containsKey(1340L));
		tag = otp2.getNodes().get(1340L).getTag("node");
		assertTrue("456".contains(tag));
		
		assertEquals(6, otp1.getNodes().size());
		assertTrue(otp1.getNodes().containsKey(23L));
		assertTrue(otp1.getNodes().get(23L).hasTag("node", "1"));
		assertTrue(otp1.getNodes().containsKey(42L));
		assertTrue(otp1.getNodes().get(42L).hasTag("node", "2"));
		assertTrue(otp1.getNodes().containsKey(1337L));
		assertTrue(otp1.getNodes().get(1337L).hasTag("node", "3"));
		assertTrue(otp1.getNodes().containsKey(1338L));
		tag = otp1.getNodes().get(1338L).getTag("node");
		assertTrue("456".contains(tag));
		assertTrue(otp1.getNodes().containsKey(1339L));
		tag = otp1.getNodes().get(1339L).getTag("node");
		assertTrue("456".contains(tag));
		assertTrue(otp1.getNodes().containsKey(1340L));
		tag = otp1.getNodes().get(1340L).getTag("node");
		assertTrue("456".contains(tag));
	}
	
	private class OsmTestParser extends OsmParser
	{
		public OsmTestParser()
		{
			super();
		}
	}
}
