package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMElement;
import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;
import java.util.ArrayList;
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
	public void testMergeNodes()
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
	
	@Test
	public void mergeWays() {
		OsmTestParser otp1 = new OsmTestParser();
		OSMNode n = new OSMNode();
		n.setId(23L);
		n.setTag("node", "1");
		otp1.nodes.put(n.getId(), n);
		n = new OSMNode();
		n.setId(42L);
		n.setTag("node", "2");
		otp1.nodes.put(n.getId(), n);
		OSMWay w = new OSMWay();
		w.setId(1337L);
		w.addRefToEnd(23L);
		w.addRefToEnd(42L);
		otp1.ways.put(w.getId(), w);
		
		OsmTestParser otp2 = new OsmTestParser();
		n = new OSMNode();
		n.setId(23L);
		n.setTag("node", "3");
		otp2.nodes.put(n.getId(), n);
		n = new OSMNode();
		n.setId(42L);
		n.setTag("node", "4");
		otp2.nodes.put(n.getId(), n);
		w = new OSMWay();
		w.setId(1337L);
		w.addRefToEnd(23L);
		w.addRefToEnd(42L);
		otp2.ways.put(w.getId(), w);
		
		otp1.mergeParsers(otp2, false);
		
		assertEquals(4, otp1.getNodes().size());
		assertEquals(2, otp1.getWays().size());
		ArrayList<Long> refs = new ArrayList<>();
		refs.add(23L);
		refs.add(42L);
		assertTrue(otp1.getWays().get(1337L).getRefs().containsAll(refs));
		refs = new ArrayList<>();
		refs.add(43L);
		refs.add(44L);
		assertTrue(otp1.getWays().get(1338L).getRefs().containsAll(refs));
	}
	
	@Test
	public void testMergeParsers()
	{
		OsmTestParser otp1 = new OsmTestParser();
		OsmTestParser otp2 = new OsmTestParser();
		OSMNode n11 = new OSMNode();
		n11.setId(-1);
		n11.setLat(1.0);
		n11.setLon(1.0);
		OSMNode n12 = new OSMNode();
		n12.setId(-2);
		n12.setLat(2.0);
		n12.setLon(2.0);
		n12.setTag("entrance", "yes");
		OSMNode n21 = new OSMNode();
		n21.setId(-3);
		n21.setLat(2.0);
		n21.setLon(2.0);
		n21.setTag("entrance", "yes");
		OSMNode n22 = new OSMNode();
		n22.setId(-1);
		n22.setLat(3.0);
		n22.setLon(3.0);
		n22.setTag("node", "4");
		OSMWay w1 = new OSMWay();
		w1.setId(-1);
		w1.addRefToEnd(-1);
		w1.addRefToEnd(-2);
		OSMWay w2 = new OSMWay();
		w2.setId(-2);
		w2.addRefToEnd(-3);
		w2.addRefToEnd(-1);
		otp1.nodes.put(n11.getId(), n11);
		otp1.nodes.put(n12.getId(), n12);
		otp2.nodes.put(n21.getId(), n21);
		otp2.nodes.put(n22.getId(), n22);
		otp1.ways.put(w1.getId(), w1);
		otp2.ways.put(w2.getId(), w2);

		OsmParser otp3 = otp1.mergeParsers(otp2, true);
		assertEquals(2, otp3.nodes.size());
		assertTrue(otp3.nodes.containsKey(-3L));
		assertTrue(otp3.nodes.get(-3L).hasTag("entrance", "yes"));
		assertTrue(otp3.nodes.containsKey(-4L));
		assertTrue(otp3.nodes.get(-4L).hasTag("node", "4"));
		assertEquals(1, otp3.ways.size());
		
		assertEquals(4, otp1.nodes.size());
		assertEquals(2, otp1.ways.size());
		assertTrue(otp1.ways.get(-1L).getRefs().contains(-1L));
		assertTrue(otp1.ways.get(-1L).getRefs().contains(-2L));
		assertTrue(otp1.ways.get(-2L).getRefs().contains(-3L));
		assertTrue(otp1.ways.get(-2L).getRefs().contains(-4L));
	}
	
	@Test
	public void testReplaceNode()
	{
		OsmTestParser otp = new OsmTestParser();
		OSMNode n1 = new OSMNode();
		n1.setId(1L);
		n1.setLat(2.0);
		n1.setLon(3.0);
		n1.setTag("node", "1");
		otp.putNode(n1);
		
		assertTrue(otp.nodes.containsKey(1L));
		assertEquals("1", otp.nodes.get(n1.getId()).getTag("node"));
		
		OSMNode n2 = new OSMNode();
		n2.setId(2L);
		n2.setLat(2.0);
		n2.setLon(3.0);
		n2.setTag("node", "2");
		otp.replaceNode(n1.getId(), n2);
		
		assertFalse(otp.nodes.containsKey(1L));
		assertTrue(otp.nodes.containsKey(2L));
		assertEquals("2", otp.nodes.get(n2.getId()).getTag("node"));
	}
	
	@Test
	public void testReplaceNodeInWay()
	{
		OsmTestParser otp = new OsmTestParser();
		OSMNode n1 = new OSMNode();
		n1.setId(1L);
		n1.setLat(2.0);
		n1.setLon(3.0);
		n1.setTag("node", "1");
		otp.putNode(n1);
		OSMNode n2 = new OSMNode();
		n2.setId(2L);
		n2.setLat(4.0);
		n2.setLon(5.0);
		n2.setTag("node", "2");
		otp.putNode(n2);
		OSMWay w = new OSMWay();
		w.setId(1L);
		ArrayList<Long> refs = new ArrayList<>();
		refs.add(n1.getId());
		refs.add(n2.getId());
		w.setRefs(refs);
		otp.putWay(w);
		
		assertTrue(otp.nodes.containsKey(1L));
		assertTrue(otp.nodes.containsKey(2L));
		assertArrayEquals(new Long[]{1L, 2L}, otp.ways.get(1L).getRefs().toArray());
		
		OSMNode n3 = new OSMNode();
		n3.setId(3L);
		n3.setLat(4.0);
		n3.setLon(5.0);
		n3.setTag("node", "3");
		otp.replaceNode(n2.getId(), n3);
		
		assertTrue(otp.nodes.containsKey(1L));
		assertFalse(otp.nodes.containsKey(2L));
		assertTrue(otp.nodes.containsKey(3L));
		assertArrayEquals(new Long[]{1L, 3L}, otp.ways.get(1L).getRefs().toArray());
	}
	
	@Test
	public void testReplaceNodeInRelation()
	{
		OsmTestParser otp = new OsmTestParser();
		OSMNode n1 = new OSMNode();
		n1.setId(1L);
		n1.setLat(2.0);
		n1.setLon(3.0);
		n1.setTag("node", "1");
		otp.putNode(n1);
		OSMRelation r = new OSMRelation();
		r.setId(1L);
		r.addMember(n1, "node");
		otp.putRelation(r);
		
		assertTrue(otp.nodes.containsKey(1L));
		assertArrayEquals(new Long[]{1L}, otp.relations.get(1L).getMembersByElementType(OSMElement.NODE).keySet().toArray());
		
		OSMNode n2 = new OSMNode();
		n2.setId(2L);
		n2.setLat(2.0);
		n2.setLon(3.0);
		n2.setTag("node", "2");
		otp.replaceNode(n1.getId(), n2);
		
		assertTrue(otp.nodes.containsKey(2L));
		assertArrayEquals(new Long[]{2L}, otp.relations.get(1L).getMembersByElementType(OSMElement.NODE).keySet().toArray());
	}
	
	private class OsmTestParser extends OsmParser
	{
		public OsmTestParser()
		{
			super();
		}
	}
}
