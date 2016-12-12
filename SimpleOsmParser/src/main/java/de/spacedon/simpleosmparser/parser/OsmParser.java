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
     * @param negative_ids allow negative IDs?
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
     * @param lat
     * @param lon
     * @return the ID of the first node with the same coordinates or just 0 if 
     * no node was found.
     */
    public long sameNode(double lat, double lon)
    {
       for(OSMNode node : this.nodes.values())
       {
           if(node.getLat() == lat && node.getLon() == lon)
               return node.getId();
       }
       return 0;
    }
    
    /**
     * Find node at the same position within a given tolerance area.
     * @param lat
     * @param lon
     * @param tolerance
     * @return the ID of the first node which is within the area or 0 if none
     * is found.
     */
    public long sameNode(double lat, double lon, double tolerance)
    {
       for(OSMNode node : this.nodes.values())
       {
           if(lat - tolerance < node.getLat() && node.getLat() < lat + tolerance
                   && lon - tolerance < node.getLon() && node.getLon() < lon + tolerance)
               return node.getId();
       }
       return 0;
    }
    
    /**
     * Compares two ways based on their coordinates and the sequence of the 
     * coordinates.
     * @param w1_id
     * @param w2_id
     * @return false if number of nodes in the two ways are different or if 
     * two nodes at the same index are different based on their coordinates.
     */
    public boolean samePathway(long w1_id, long w2_id)
    {
        if(this.ways.get(w1_id).getRefs().size() != this.ways.get(w2_id).getRefs().size())
            return false;
        ArrayList<Double> latlist = new ArrayList<>();
        ArrayList<Double> lonlist = new ArrayList<>();
        for(Long n1_id : this.ways.get(w1_id).getRefs())
        {
            latlist.add(this.nodes.get(n1_id).getLat());
            lonlist.add(this.nodes.get(n1_id).getLon());
        }
        for(Long n2_id : this.ways.get(w2_id).getRefs())
        {
            int index = this.ways.get(w2_id).getRefs().indexOf(n2_id);
            if(latlist.get(index) != this.nodes.get(n2_id).getLat()
                    || lonlist.get(index) != this.nodes.get(n2_id).getLon())
                return false;
        }
        return true;
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
     * A node with the same ID in the parser will be overwritten.
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
	
	/**
	 * Parser is empty if it contains neither nodes nor ways nor relations.
	 * @return 
	 */
	public boolean isEmpty() {
		return this.nodes.isEmpty() && this.ways.isEmpty() && this.relations.isEmpty();
	}
}
