package de.spacedon.simpleosmparser.osm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Philipp Thöricht
 */
public class OSMRelation extends OSMElement
{
    private String type;
    private List<HashMap<String, String>> members;
    
    /**
     * 
     */
    public OSMRelation()
    {
        super(OSMElement.RELATION);
        this.members = new LinkedList<>();
    }
    
    /**
     * 
     * @param type
     * @param ref
     * @param role 
     */
    public void addMember(String type, String ref, String role)
    {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("type", type);
        hm.put("ref", ref);
        hm.put("role", role);
        this.members.add(hm);
    }
    
    /**
     * 
     * @return 
     */
    public List<HashMap<String, String>> getMembers()
    {
        return this.members;
    }
    
    /**
     * 
     * @param member
     * @return 
     */
    public boolean delMember(long member)
    {
        boolean success = false;
        for(HashMap<String, String> hm : this.members)
        {
            if(Long.valueOf(hm.get("ref")).equals(member))
            {
                success = this.members.remove(hm);
                break;
            }
        }
        return success;
    }
}
