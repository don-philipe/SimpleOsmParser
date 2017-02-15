package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Philipp Thöricht
 */
public class OsmFileParserTest
{    
    @Test
    public void testParsing() throws IOException, XMLStreamException
    {
        OsmFileParser sop = new OsmFileParser();
        File f1 = new File("./src/test/resources/map1.osm");
        sop.parseOsmFile(f1);
        
        assertTrue(sop.getNode(-95758).hasTag("entrance", "yes"));
        assertTrue(sop.getNode(-95758).hasTag("addr:door", "2"));
        
        assertTrue(sop.getWay(-95750).hasTag("highway", "footway"));
        assertEquals(Long.valueOf("-95749"), sop.getWay(-95750).getRefs().get(1));
        assertTrue(sop.getNode(-95749).getBelongsTo().contains(sop.getWay(Long.valueOf("-95750"))));
        
        int map1nodes = sop.getNodes().size();
        File f2 = new File("./src/test/resources/map2.osm");
        sop.parseOsmFile(f2);      
        assertTrue(sop.getNodes().size() == map1nodes + 2);
    }
    
    @Test
    public void testSameIdOnMerge()
    {
        OsmFileParser sop1 = new OsmFileParser();
        OSMNode n1 = new OSMNode(1, 1.0, 1.0);
        sop1.putNode(n1);
        
        OsmFileParser sop2 = new OsmFileParser();
        OSMNode n2 = new OSMNode(1, 2.0, 2.0);
        sop2.putNode(n2);
        
        assertTrue(sop1.getNodes().size() == 1);
        
		ArrayList<String> merge_by_means_of = new ArrayList<>();
        sop1.mergeParsers(sop2, false, merge_by_means_of);
        
        assertTrue(sop1.getNodes().size() == 2);
        assertTrue(sop1.getNode(1).getId() == 1);
        assertTrue(sop1.getNode(2).getId() == 2);
        assertTrue(sop2.getNode(2).getId() == 2);
    }
}
