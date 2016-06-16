package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMWay;
import java.io.File;
import java.io.FileNotFoundException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Philipp Thöricht
 */
public class PbfFileParserTest
{
    @Test
    public void testReadPbfFile() throws FileNotFoundException
    {
        PbfFileParser pfp = new PbfFileParser();
        File pbf = new File("./src/test/resources/apb-outdoor.pbf");
        pfp.parsePbfFile(pbf);
        
        assertTrue(pfp.nodes.containsKey(534887L));
    }
    
    @Test
    public void testNodeSequenceInWay() throws FileNotFoundException
    {
        PbfFileParser pfp = new PbfFileParser();
        File pbf = new File("./src/test/resources/apb-outdoor.pbf");
        pfp.parsePbfFile(pbf);
        
        OSMWay w = pfp.ways.get(117500191L);
        assertEquals(295678631, w.getRefs().get(0).longValue());
        assertEquals(500061407, w.getRefs().get(1).longValue());
    }
}
