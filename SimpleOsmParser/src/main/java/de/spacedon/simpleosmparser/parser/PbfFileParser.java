package de.spacedon.simpleosmparser.parser;

import crosby.binary.osmosis.OsmosisReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

/**
 * Can read .pbf files
 * @author Philipp Thöricht
 */
public class PbfFileParser extends OsmParser
{
    /**
     * 
     */
    public PbfFileParser()
    {
        
    }
    
    /**#
     * 
     * @param file
     * @throws FileNotFoundException 
     */
    public void parsePbfFile(File file) throws FileNotFoundException
    {
        SinkImpl sinkImplementation = new SinkImpl(this.nodes, this.ways, this.relations);

        boolean pbf = false;
        CompressionMethod compression = CompressionMethod.None;

        if(file.getName().endsWith(".pbf"))
            pbf = true;
        else if(file.getName().endsWith(".gz"))
            compression = CompressionMethod.GZip;
        else if(file.getName().endsWith(".bz2"))
            compression = CompressionMethod.BZip2;

        RunnableSource reader;

        if(pbf)
            reader = new OsmosisReader(new FileInputStream(file));
        else
            reader = new XmlReader(file, true, compression);

        reader.setSink(sinkImplementation);

        Thread readerThread = new Thread(reader);
        readerThread.start();

        while (readerThread.isAlive())
        {
            try
            {
                readerThread.join();
            }
            catch(InterruptedException e)
            {
                // do nothing
            }
        }
    }
}
