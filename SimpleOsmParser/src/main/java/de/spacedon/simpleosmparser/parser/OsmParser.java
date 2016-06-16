package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;
import java.util.HashMap;

/**
 *
 * @author Philipp Thöricht
 */
public abstract class OsmParser
{
    protected final HashMap<Long, OSMNode> nodes;
    protected final HashMap<Long, OSMWay> ways;
    protected final HashMap<Long, OSMRelation> relations;
    
    /**
     * 
     */
    public OsmParser()
    {
        this.nodes = new HashMap<>();
        this.ways = new HashMap<>();
        this.relations = new HashMap<>();
    }
    
    /**
     * 
     * @param parser 
     */
    public void mergeParsers(OsmParser parser)
    {
        this.nodes.putAll(parser.getNodes());
        this.ways.putAll(parser.getWays());
        this.relations.putAll(parser.getRelations());
    }
    
    /**
     * @return the nodes
     */
    public HashMap<Long, OSMNode> getNodes()
    {
        return nodes;
    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public OSMNode getNode(long id)
    {
        return this.nodes.get(id);
    }
    
    /**
     * 
     * @param node 
     */
    public void putNode(OSMNode node)
    {
        this.nodes.put(node.getId(), node);
    }

    /**
     * @return the ways
     */
    public HashMap<Long, OSMWay> getWays()
    {
        return ways;
    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public OSMWay getWay(long id)
    {
        return this.ways.get(id);
    }
    
    /**
     * 
     * @param way 
     */
    public void putWay(OSMWay way)
    {
        this.ways.put(way.getId(), way);
    }

    /**
     * @return the relations
     */
    public HashMap<Long, OSMRelation> getRelations()
    {
        return relations;
    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public OSMRelation getRelation(long id)
    {
        return this.relations.get(id);
    }
    
    /**
     * 
     * @param relation 
     */
    public void putRelation(OSMRelation relation)
    {
        this.relations.put(relation.getId(), relation);
    }
}
