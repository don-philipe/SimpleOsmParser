package de.spacedon.simpleosmparser.parser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

/**
 * This class is just for commandline usage.
 * @author Philipp Thöricht
 */
public class SimpleOsmParser
{
    /**
     * @param args the command line arguments
     *      [0]: -f for parsing a file, -d for parsing a DB
     *      [1]: the path to the osm-file
     * @throws java.net.MalformedURLException
     * @throws javax.xml.stream.XMLStreamException
     */
    public static void main(String[] args) throws MalformedURLException, IOException, XMLStreamException
    {
        File file = new File(args[0]);

        SimpleOsmParser sop = new SimpleOsmParser(file);
    }

    /**
     * 
     * @param file
     */
    public SimpleOsmParser(File file)
    {
        OsmFileParser ofp = new OsmFileParser();
        try
        {
            ofp.parseOsmFile(file);
        }
        catch (IOException | XMLStreamException | FactoryConfigurationError ex)
        {
            Logger.getLogger(SimpleOsmParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
