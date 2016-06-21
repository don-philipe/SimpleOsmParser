package de.spacedon.simpleosmparser.osm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Philipp Thöricht
 */
public class OSMRelation extends OSMElement
{
    private String type;
    private HashMap<Long, String> node_members;
    private HashMap<Long, String> way_members;
    private HashMap<Long, String> relation_members;
    
    /**
     * Just calls OSMRelation("").
     */
    public OSMRelation()
    {
        this("");
    }
    
    /**
     * 
     * @param relationtype the type of this relation
     */
    public OSMRelation(String relationtype)
    {
        super(OSMElement.RELATION);
        this.type = relationtype;
        this.node_members = new HashMap<>();
        this.way_members = new HashMap<>();
        this.relation_members = new HashMap<>();
    }
    
    public void addMember(int elementtype, long ref, String role)
    {
        if(elementtype == OSMElement.NODE)
            this.node_members.put(ref, role);
        else if(elementtype == OSMElement.WAY)
            this.way_members.put(ref, role);
        else if(elementtype == OSMElement.RELATION)
            this.relation_members.put(ref, role);
    }
    
    /**
     * 
     * @param ele
     * @param role is optional in OSM
     */
    public void addMember(OSMElement ele, String role)
    {
        if(role == null)
            role = "";
        
        if(ele instanceof OSMNode)
            this.node_members.put(ele.getId(), role);
        else if(ele instanceof OSMWay)
            this.way_members.put(ele.getId(), role);
        else if(ele instanceof OSMRelation)
            this.relation_members.put(ele.getId(), role);
    }
    
    /**
     * 
     * @param elementtype either OSMElement.NODE, OSMElement.WAY OR OSMElement.RELATION
     * @return the mapping of id-role, or an empty HashMap in case elementtype 
     * value was invalid.
     */
    public HashMap<Long, String> getMembersByElementType(int elementtype)
    {
        if(elementtype == OSMElement.NODE)
            return this.node_members;
        else if(elementtype == OSMElement.WAY)
            return this.way_members;
        else if(elementtype == OSMElement.RELATION)
            return this.relation_members;
        else
            return new HashMap<>();
    }
    
    /**
     * 
     * @return list of members of elementtype NODE, WAY, RELATION in this order
     */
    public ArrayList<HashMap<Long, String>> getAllMembers()
    {
        ArrayList<HashMap<Long, String>> all_members = new ArrayList<>();
        all_members.add(0, this.node_members);
        all_members.add(1, this.way_members);
        all_members.add(2, this.relation_members);
        
        return all_members;
    }
    
    /**
     * 
     * @param member
     * @param elementtype either OSMElement.NODE, OSMElement.WAY OR OSMElement.RELATION
     */
    public void delMember(long member, int elementtype)
    {
        if(elementtype == OSMElement.NODE)
            this.node_members.remove(member);
        else if(elementtype == OSMElement.WAY)
            this.way_members.remove(member);
        else if(elementtype == OSMElement.RELATION)
            this.relation_members.remove(member);
    }
    
    /**
     * 
     * @return 
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * 
     * @param relationtype 
     */
    public void setType(String relationtype)
    {
        this.type = relationtype;
    }
}
