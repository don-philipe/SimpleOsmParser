package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMElement;
import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Reads and writes *.osm files (XML format).
 *
 * @author Philipp Thöricht
 */
public class OsmFileParser extends OsmParser {
    private XMLStreamReader reader;

    public OsmFileParser() {

    }

    /**
     * @param file
     * @return number of errors while reading
     * @throws IOException
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     */
    public long parseOsmFile(File file) throws IOException, XMLStreamException, FactoryConfigurationError {
        long numErrors = 0;
        InputStream in = new FileInputStream(file);
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
            int event = reader.getEventType();
            while (true) {
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if ("osm".equals(reader.getLocalName())) {
                        numErrors = readOsm();
                    } else {
                        jumpToEnd();
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT)
                    return numErrors;

                if (reader.hasNext())
                    event = reader.next();
                else
                    break;
            }
            reader.close();
        } finally {
            in.close();
        }
        return numErrors;
    }

    /**
     * @param file
     * @throws javax.xml.stream.XMLStreamException
     */
    public void writeOsmFile(File file) throws XMLStreamException {
        XMLStreamWriter writer;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String timestamp = sdf.format(cal.getTime());
        try {
            OutputStream out = new FileOutputStream(file);
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            writer.writeStartDocument();

            writer.writeStartElement("osm");
            writer.writeAttribute("version", "0.6");
            writer.writeAttribute("upload", "false");
            writer.writeAttribute("generator", "CN");
            for (OSMNode n : this.nodes.values()) {
                writer.writeStartElement("node");
                writer.writeAttribute("id", String.valueOf(n.getId()));
                if (n.getTimestamp() != null)
                    writer.writeAttribute("timestamp", sdf.format(n.getTimestamp()));
                else
                    writer.writeAttribute("timestamp", timestamp);
                writer.writeAttribute("version", String.valueOf(n.getVersion()));
                writer.writeAttribute("lat", String.valueOf(n.getLat()));
                writer.writeAttribute("lon", String.valueOf(n.getLon()));
                this.writeTags(writer, n);
                writer.writeEndElement();
                writer.writeCharacters(System.getProperty("line.separator"));
            }
            for (OSMWay w : this.ways.values()) {
                writer.writeStartElement("way");
                writer.writeAttribute("id", String.valueOf(w.getId()));
                if (w.getTimestamp() != null)
                    writer.writeAttribute("timestamp", sdf.format(w.getTimestamp()));
                else
                    writer.writeAttribute("timestamp", timestamp);
                writer.writeAttribute("version", String.valueOf(w.getVersion()));
                for (long nid : w.getRefs()) {
                    writer.writeEmptyElement("nd");
                    writer.writeAttribute("ref", String.valueOf(nid));
                }
                this.writeTags(writer, w);
                writer.writeEndElement();
                writer.writeCharacters(System.getProperty("line.separator"));
            }
            for (OSMRelation r : this.relations.values()) {
                writer.writeStartElement("relation");
                writer.writeAttribute("id", String.valueOf(r.getId()));
                if (r.getTimestamp() != null)
                    writer.writeAttribute("timestamp", sdf.format(r.getTimestamp()));
                else
                    writer.writeAttribute("timestamp", timestamp);
                writer.writeAttribute("version", String.valueOf(r.getVersion()));
                for (int i = 0; i < r.getAllMembers().size(); i++) {
                    HashMap<Long, String> element_map = r.getAllMembers().get(i);
                    for (Long id : element_map.keySet()) {
                        writer.writeEmptyElement("member");
                        switch (i) {
                            case 0:
                                writer.writeAttribute("type", "node");
                                break;
                            case 1:
                                writer.writeAttribute("type", "way");
                                break;
                            case 2:
                                writer.writeAttribute("type", "relation");
                                break;
                        }
                        writer.writeAttribute("ref", String.valueOf(id));
                        writer.writeAttribute("role", element_map.get(id));
                    }
                }
                this.writeTags(writer, r);
                writer.writeEndElement();
                writer.writeCharacters(System.getProperty("line.separator"));
            }
            writer.writeEndElement();
            writer.writeCharacters(System.getProperty("line.separator"));

            writer.flush();
            writer.close();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleOsmParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OsmFileParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param writer
     * @param ele
     * @throws XMLStreamException
     */
    private void writeTags(XMLStreamWriter writer, OSMElement ele) throws XMLStreamException {
        for (String key : ele.getTags().keySet()) {
            writer.writeEmptyElement("tag");
            writer.writeAttribute("k", key);
            writer.writeAttribute("v", ele.getTag(key));
        }
    }

    /**
     * @return number of errors while reading
     */
    private long readOsm() throws XMLStreamException {
        String version = reader.getAttributeValue(null, "version");
        if (version != null)
            ;
        String upload = reader.getAttributeValue(null, "upload");
        if (upload != null)
            ;
        String generator = reader.getAttributeValue(null, "generator");

        long numErrors = 0;
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
//                    case "bounds":
//                        readBounds(generator);
//                        break;
                    case "node":
                        OSMNode n = readNode();
                        if (n == null) {
                            numErrors++;
                            continue;
                        }
                        this.nodes.put(n.getId(), n);
                        break;
                    case "way":
                        OSMWay w = readWay();
                        if (w == null) {
                            numErrors++;
                            continue;
                        }
                        this.ways.put(w.getId(), w);
                        break;
                    case "relation":
                        OSMRelation r = readRelation();
                        if (r == null) {
                            numErrors++;
                            continue;
                        }
                        this.relations.put(r.getId(), r);
                        break;
//                    case "changeset":
//                        readChangeset(uploadChangesetId);
//                        break;
                    default:
                        jumpToEnd();
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                return numErrors;
            }
        }
    }

    /**
     * @return
     * @throws XMLStreamException
     */
    private OSMNode readNode() throws XMLStreamException {
        OSMNode n = null;
        String id = reader.getAttributeValue(null, "id");
        String lat = reader.getAttributeValue(null, "lat");
        String lon = reader.getAttributeValue(null, "lon");
        if (id != null && lat != null && lon != null) {
            n = new OSMNode(Long.valueOf(id), Double.valueOf(lat), Double.valueOf(lon));
        }
        String version = reader.getAttributeValue(null, "version");
        if (n != null && version != null)
            n.setVersion(Integer.valueOf(version));
        String visible = reader.getAttributeValue(null, "visible");
        if (n != null && visible != null)
            n.setVisible(Boolean.valueOf(visible));
        String user = reader.getAttributeValue(null, "user");
        if (n != null && user != null)
            n.setUser(user);

        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("tag".equals(reader.getLocalName()))
                    n.setTag(readTag());
                else
                    jumpToEnd();
            } else if (event == XMLStreamConstants.END_ELEMENT)
                return n;
        }
    }

    /**
     * @return
     * @throws XMLStreamException
     */
    private OSMWay readWay() throws XMLStreamException {
        OSMWay w = null;

        String id = reader.getAttributeValue(null, "id");
        if (id != null)
            w = new OSMWay(Long.valueOf(id));
        String version = reader.getAttributeValue(null, "version");
        if (w != null && version != null)
            w.setVersion(Integer.valueOf(version));
        String visible = reader.getAttributeValue(null, "visible");
        if (w != null && visible != null)
            w.setVisible(Boolean.valueOf(visible));
        String user = reader.getAttributeValue(null, "user");
        if (w != null && user != null)
            w.setUser(user);

        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "nd":
                        String ref = reader.getAttributeValue(null, "ref");
                        if (w != null && ref != null) {
                            w.addRefToEnd(Long.valueOf(ref));
                            // add a reverse link (inverse to "ref") to the nodes of
                            // this way
                            this.nodes.get(Long.valueOf(ref)).addBelongsTo(w);
                        }
                        jumpToEnd();
                        break;
                    case "tag":
                        w.setTag(readTag());
                        break;
                    default:
                        jumpToEnd();
                }
            } else if (event == XMLStreamConstants.END_ELEMENT)
                return w;
        }
    }

    /**
     * @return
     * @throws XMLStreamException
     */
    private OSMRelation readRelation() throws XMLStreamException {
        OSMRelation r = null;

        String id = reader.getAttributeValue(null, "id");
        if (id != null)
            r = new OSMRelation(Long.valueOf(id), "");
        String version = reader.getAttributeValue(null, "version");
        if (r != null && version != null)
            r.setVersion(Integer.valueOf(version));
        String visible = reader.getAttributeValue(null, "visible");
        if (r != null && visible != null)
            r.setVisible(Boolean.valueOf(visible));
        String user = reader.getAttributeValue(null, "user");
        if (r != null && user != null)
            r.setUser(user);

        while (true) {
            int event = reader.next();
            if (r != null && event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "member":
                        String type = reader.getAttributeValue(null, "type");
                        String ref = reader.getAttributeValue(null, "ref");
                        String role = reader.getAttributeValue(null, "role");
                        if (type != null && ref != null && role != null) {
                            // add a reverse link (inverse to "member" attribute) to
                            // the members of this relation
                            switch (type) {
                                case "node":
                                    r.addMember(OSMElement.NODE, Long.valueOf(ref), role);
                                    if (Long.valueOf(ref) != null && this.nodes.containsKey(Long.valueOf(ref)))
                                        this.nodes.get(Long.valueOf(ref)).addBelongsTo(r);
                                    break;
                                case "way":
                                    r.addMember(OSMElement.WAY, Long.valueOf(ref), role);
                                    if (Long.valueOf(ref) != null && this.ways.containsKey(Long.valueOf(ref)))
                                        this.ways.get(Long.valueOf(ref)).addBelongsTo(r);
                                    break;
                                case "relation":
                                    r.addMember(OSMElement.RELATION, Long.valueOf(ref), role);
                                    if (Long.valueOf(ref) != null && this.relations.containsKey(Long.valueOf(ref)))
                                        this.relations.get(Long.valueOf(ref)).addBelongsTo(r);
                                    break;
                                default:
                                    break;
                            }
                        }
                        jumpToEnd();
                        break;
                    case "tag":
                        r.setTag(readTag());
                        break;
                    default:
                        jumpToEnd();
                }
            } else if (event == XMLStreamConstants.END_ELEMENT)
                return r;
        }
    }

    /**
     * @return
     * @throws XMLStreamException
     */
    private HashMap<String, String> readTag() throws XMLStreamException {
        HashMap<String, String> hm = new HashMap<>();
        String key = reader.getAttributeValue(null, "k");
        String value = reader.getAttributeValue(null, "v");
        if (key == null || value == null)
            ;   // error

        hm.put(key, value);
        jumpToEnd();
        return hm;
    }

    /**
     * @throws XMLStreamException
     */
    private void jumpToEnd() throws XMLStreamException {
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT)
                jumpToEnd();
            else if (event == XMLStreamConstants.END_ELEMENT)
                return;
        }
    }
}
