package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Philipp Thöricht
 */
public abstract class OsmParser
{
    protected HashMap<Long, OSMNode> nodes;
    protected HashMap<Long, OSMWay> ways;
    protected HashMap<Long, OSMRelation> relations;
    
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
     * @return the parser that was overhanded to this method, because its IDs might have changed.
     */
    public OsmParser mergeParsers(OsmParser parser, boolean negative_ids)
    {        
        ArrayList<Long> remove_nodes = new ArrayList<>();
        ArrayList<OSMNode> new_nodes = new ArrayList<>();
        for(Long id : parser.getNodes().keySet())
        {
            if(this.nodes.containsKey(id))
            {
                OSMNode n = parser.getNode(id);
                remove_nodes.add(id);
                if(negative_ids)
                    n.setId(Collections.min(this.nodes.keySet()) - 1);
                else
                    n.setId(Collections.max(this.nodes.keySet()) + 1);
                new_nodes.add(n);
            }
        }
        for(Long id : remove_nodes)
            parser.getNodes().remove(id);
        for(OSMNode n : new_nodes)
            parser.putNode(n);
        this.nodes.putAll(parser.getNodes());
        
        // refresh refs in all ways:
        LinkedList<OSMWay> modify_ways = new LinkedList<>();
        for(OSMWay w : parser.getWays().values())
        {
            for(Long id : remove_nodes)
            {
                if(w.getRefs().contains(id))
                    modify_ways.add(w);
            }
        }
        for(OSMWay w : modify_ways)
        {
            for(int i = 0; i < remove_nodes.size(); i++)
            {
                long old_id = remove_nodes.get(i);
                if(w.getRefs().contains(old_id))
                {
                    int pos = parser.getWay(w.getId()).getRefs().indexOf(old_id);
                    parser.getWay(w.getId()).addRef(new_nodes.get(i).getId(), pos);
                }
            }
        }
        //TODO refresh refs in all relations
        
        ArrayList<Long> remove_ways = new ArrayList<>();
        ArrayList<OSMWay> new_ways = new ArrayList<>();
        for(Long id : parser.getWays().keySet())
        {
            if(this.ways.containsKey(id))
            {
                OSMWay w = parser.getWay(id);
                remove_ways.add(id);
                if(negative_ids)
                    w.setId(Collections.min(this.ways.keySet()) - 1);
                else
                    w.setId(Collections.max(this.ways.keySet()) + 1);
                new_ways.add(w);
            }
        }
        for(Long id : remove_ways)
            parser.getWays().remove(id);
        for(OSMWay w : new_ways)
            parser.putWay(w);
        this.ways.putAll(parser.getWays());
        //TODO refresh refs in all relations
        
        LinkedList<Long> remove_relations = new LinkedList<>();
        LinkedList<OSMRelation> new_relations = new LinkedList<>();
        for(Long id : parser.getRelations().keySet())
        {
            if(this.relations.containsKey(id))
            {
                OSMRelation r = parser.getRelation(id);
                remove_relations.add(id);
                if(negative_ids)
                    r.setId(Collections.min(this.relations.keySet()) - 1);
                else
                    r.setId(Collections.max(this.relations.keySet()) + 1);
                new_relations.add(r);
            }
        }
        for(Long id : remove_relations)
            parser.getRelations().remove(id);
        for(OSMRelation r : new_relations)
            parser.putRelation(r);
        this.relations.putAll(parser.getRelations());
        
        return parser;
    }
    
    /**
     * 
     * @return 
     */
    public OsmParser getParser()
    {
        return this;
    }
    
    /**
     * Removes all nodes, ways and relations from this parser and copies those
     * from overhanded parser to this one.
     * @param parser 
     */
    public void setParser(OsmParser parser)
    {
        this.nodes = new HashMap<>();
        for(OSMNode n : parser.getNodes().values())
            this.putNode(n);
        this.ways = new HashMap<>();
        for(OSMWay w : parser.getWays().values())
            this.putWay(w);
        this.relations = new HashMap<>();
        for(OSMRelation r : parser.getRelations().values())
            this.putRelation(r);
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
