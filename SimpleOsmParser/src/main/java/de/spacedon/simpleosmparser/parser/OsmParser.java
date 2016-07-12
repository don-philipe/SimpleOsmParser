package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;
import java.util.Collections;
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
     * Merges two parsers (osm files) together into one. Especially takes care of IDs.
     * @param parser 
     * @param negative_ids 
     */
    public void mergeParsers(OsmParser parser, boolean negative_ids)
    {
        int iddiff = 1;
        if(negative_ids)
            iddiff = -1;
        
        for(Long id : parser.getNodes().keySet()) {
            if(this.nodes.containsKey(id)) {
                OSMNode n = parser.getNode(id);
                parser.getNodes().remove(id);
                n.setId(Collections.max(this.nodes.keySet()) + iddiff);
                parser.putNode(n);
            }
        }
        this.nodes.putAll(parser.getNodes());
        
        for(Long id : parser.getWays().keySet()) {
            if(this.ways.containsKey(id)) {
                OSMWay w = parser.getWay(id);
                parser.getWays().remove(id);
                w.setId(Collections.max(this.ways.keySet()) + iddiff);
                parser.putWay(w);
            }
        }
        this.ways.putAll(parser.getWays());
        
        for(Long id : parser.getRelations().keySet()) {
            if(this.relations.containsKey(id)) {
                OSMRelation r = parser.getRelation(id);
                parser.getRelations().remove(id);
                r.setId(Collections.max(this.relations.keySet()) + iddiff);
                parser.putRelation(r);
            }
        }
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
