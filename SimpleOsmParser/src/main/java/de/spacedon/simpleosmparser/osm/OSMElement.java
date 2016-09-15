package de.spacedon.simpleosmparser.osm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Philipp Thöricht
 */
public abstract class OSMElement
{
    public static final int NODE = 0;
    public static final int WAY = 1;
    public static final int RELATION = 2;
    
    private long id;
    protected HashMap<String, String> tags;
    private int version;
    private boolean visible;
    // adds the possibility to know the way/relation this node belongs to,
    // or to know the relation this node/way/relation belongs to
    private ArrayList<OSMElement> belongsto = new ArrayList<>();
    private final int elementtype;
    private Date timestamp;
    private String user;
    private long uid;
    
    public OSMElement(int elementtype)
    {
        this.elementtype = elementtype;
        this.tags = new HashMap<>();
    }
    
    /**
     * Checks whether this element has the key-value combination.
     * @param key must be the exact key
     * @param value can be the exact value, or a valuefragment, or even a wildcard
     * java regex characters can be used
     * @return true if the key-value combination was found, false otherwise.
     */
    public boolean hasTag(String key, String value)
    {
        if(key != null && value != null)
        {
            Iterator<String> iter = this.tags.keySet().iterator();
            while(iter.hasNext())
            {
                String k = iter.next();
                if(key.equals(k) && this.tags.get(k).matches(value))
                    return true;
            }
        }
	return false;
    }
    
    /**
     * 
     * @param ele 
     */
    public void addBelongsTo(OSMElement ele)
    {
        this.belongsto.add(ele);
    }
    
    /**
     * 
     * @return 
     */
    public ArrayList<OSMElement> getBelongsTo()
    {
        return this.belongsto;
    }
    
    /**
     * 
     * @param ele 
     */
    public void removeBelongsTo(OSMElement ele)
    {
        this.belongsto.remove(ele);
    }
    
    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return a hashmap with all tags
     */
    public HashMap<String, String> getTags()
    {
        return tags;
    }
    
    /**
     * Gets you a value specified by the key. Or null if no value is linked to
     * that key.
     * @param key
     * @return the value or null if no mapping for that key
     */
    public String getTag(String key)
    {
        return this.tags.get(key);
    }

    /**
     * @param tags set all tags at once
     */
    public void setTags(HashMap<String, String> tags)
    {
        this.tags = tags;
    }
    
    /**
     * Set a specific key and value pair. But only if the key isnt present yet.
     * @param key
     * @param value 
     */
    public void setTag(String key, String value)
    {
        if(!this.tags.containsKey(key))
            this.tags.put(key, value);
    }
    
    /**
     * 
     * @param tag 
     */
    public void setTag(HashMap<String, String> tag)
    {
        if(!this.tags.containsKey(tag.keySet().iterator().next()))
            this.tags.putAll(tag);
    }
    
    /**
     * 
     * @return 
     */
    public int getElementtype()
    {
        return this.elementtype;
    }
    
    /**
     * 
     * @return 
     */
    public int getVersion()
    {
        return this.version;
    }
    
    /**
     * 
     * @param version 
     */
    public void setVersion(int version)
    {
        this.version = version;
    }
    
    /**
     * 
     * @return 
     */
    public Date getTimestamp()
    {
        return this.timestamp;
    }
    
    /**
     * 
     * @param timestamp 
     */
    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @return the visibility
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * @param visible set the visibility
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    /**
     * 
     * @return 
     */
    public String getUser()
    {
        return this.user;
    }
    
    /**
     * 
     * @param user 
     */
    public void setUser(String user)
    {
        this.user = user;
    }
    
    /**
     * 
     * @return the user id
     */
    public long getUid()
    {
        return this.uid;
    }
    
    /**
     * 
     * @param uid the user id
     */
    public void setUid(long uid)
    {
        this.uid = uid;
    }
}
