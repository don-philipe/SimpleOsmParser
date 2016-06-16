package de.spacedon.simpleosmparser.osm;

import java.util.ArrayList;


public class OSMWay extends OSMElement
{
    private String action;
    private ArrayList<Long> refs;
    
    public OSMWay()
    {
        super(OSMElement.WAY);
        this.refs = new ArrayList<>();
    }
    
    /**
     * 
     * @param ref
     * @param sequence 
     */
    public void addRef(long ref, int sequence)
    {
        this.getRefs().add(sequence, ref);
    }
    
    /**
     * Adds new ref to the end of the list of refs.
     * @param ref 
     */
    public void addRefToEnd(long ref)
    {
        this.getRefs().add(ref);
    }

    /**
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * @return the refs
     */
    public ArrayList<Long> getRefs()
    {
        return refs;
    }

    /**
     * The overhanded references must be in appropriate order.
     * @param refs the refs to set
     */
    public void setRefs(ArrayList<Long> refs)
    {
        this.refs = refs;
    }
    
    /**
     * 
     * @param sequence
     * @return the deleted node id
     */
    public Long delRef(int sequence)
    {
        return this.getRefs().remove(sequence);
    }
}
