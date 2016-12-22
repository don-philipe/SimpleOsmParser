package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMElement;
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
     * Merges the overhandes parser (osm files) into this. Especially takes care of IDs.
     * @param parser 
     * @param negative_ids allow negative IDs?
     * @return the parser that was overhanded to this method, because its IDs might have changed.
     */
    public OsmParser mergeParsers(OsmParser parser, boolean negative_ids)
    {        
        ArrayList<Long> remove_nodes = new ArrayList<>();
        ArrayList<OSMNode> new_nodes = new ArrayList<>();
		long min_id = 0L;
		long max_id = 0L;
		if(!this.nodes.isEmpty())
		{
			min_id = Math.min(Collections.min(this.nodes.keySet()), Collections.min(parser.nodes.keySet()));
			max_id = Math.max(Collections.max(this.nodes.keySet()), Collections.max(parser.nodes.keySet()));
		}
        for(Long id : parser.getNodes().keySet())
        {
            if(this.nodes.containsKey(id))
            {
                OSMNode n = parser.getNode(id);
                remove_nodes.add(id);
                if(negative_ids)
                    n.setId(--min_id);
                else
                    n.setId(++max_id);
                new_nodes.add(n);
            }
        }
        for(Long id : remove_nodes)
            parser.getNodes().remove(id);
        for(OSMNode n : new_nodes)
            parser.putNode(n);
        this.nodes.putAll(parser.getNodes());
        
        // refresh refs in all ways:
        for(OSMWay w : parser.getWays().values())
        {
            for(int i = 0; i < remove_nodes.size(); i++)
            {
				long old_id = remove_nodes.get(i);
                if(w.getRefs().contains(old_id))
				{
                    int pos = w.getRefs().indexOf(old_id);
					w.replaceRef(new_nodes.get(i).getId(), pos);
				}
            }
        }
        //TODO refresh refs in all relations
        
        ArrayList<Long> remove_ways = new ArrayList<>();
        ArrayList<OSMWay> new_ways = new ArrayList<>();
		min_id = 0L;
		max_id = 0L;
		if(!this.ways.isEmpty())
		{
			min_id = Math.min(Collections.min(this.ways.keySet()), Collections.min(parser.ways.keySet()));
			max_id = Math.max(Collections.max(this.ways.keySet()), Collections.max(parser.ways.keySet()));
		}
        for(Long id : parser.getWays().keySet())
        {
            if(this.ways.containsKey(id))
            {
                OSMWay w = parser.getWay(id);
                remove_ways.add(id);
                if(negative_ids)
                    w.setId(--min_id);
                else
                    w.setId(++max_id);
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
		min_id = 0L;
		max_id = 0L;
		if(!this.relations.isEmpty())
		{
			min_id = Math.min(Collections.min(this.relations.keySet()), Collections.min(parser.relations.keySet()));
			max_id = Math.max(Collections.max(this.relations.keySet()), Collections.max(parser.relations.keySet()));
		}
        for(Long id : parser.getRelations().keySet())
        {
            if(this.relations.containsKey(id))
            {
                OSMRelation r = parser.getRelation(id);
                remove_relations.add(id);
                if(negative_ids)
                    r.setId(--min_id);
                else
                    r.setId(++max_id);
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
	 * Replaces node with old_id with the given new node. Takes care of refs in
	 * ways and members in relations.
	 * @param old_id
	 * @param node 
	 */
	public void replaceNode(long old_id, OSMNode node)
	{
		this.nodes.remove(old_id);
		this.nodes.put(node.getId(), node);
		
		for(OSMWay w : this.ways.values())
		{
			if(w.getRefs().contains(old_id))
			{
				w.replaceRef(old_id, node.getId());
			}
		}
		
		for(OSMRelation r : this.relations.values())
		{
			for(Long n_id : r.getAllMembers().get(0).keySet())
			{
				if(n_id.equals(old_id))
				{
					r.replaceMember(old_id, node.getId(), OSMElement.NODE);
				}
			}
		}
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
	public boolean isEmpty()
	{
		return this.nodes.isEmpty() && this.ways.isEmpty() && this.relations.isEmpty();
	}
}
