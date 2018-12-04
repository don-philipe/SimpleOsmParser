package de.spacedon.simpleosmparser.osm;

import java.util.ArrayList;


public class OSMWay extends OSMElement {
    private String action;
    private ArrayList<Long> refs;

    public OSMWay(long id) {
        super(id, OSMElement.WAY);
        this.refs = new ArrayList<>();
    }

    /**
     * @param ref
     * @param sequence 1-based, values small than 1 will be set to 1.
     * @throws IndexOutOfBoundsException if sequence is out of range
     */
    public void addRef(long ref, int sequence) throws IndexOutOfBoundsException {
        if (sequence < 1)
            sequence = 1;
        this.getRefs().add(sequence - 1, ref);
    }

    /**
     * Adds new ref to the end of the list of refs.
     *
     * @param ref
     */
    public void addRefToEnd(long ref) {
        this.getRefs().add(ref);
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the refs
     */
    public ArrayList<Long> getRefs() {
        return refs;
    }

    /**
     * The overhanded references must be in appropriate order.
     *
     * @param refs the refs to set
     */
    public void setRefs(ArrayList<Long> refs) {
        this.refs = refs;
    }

    /**
     * @param sequence
     * @return the deleted node id
     * @throws IndexOutOfBoundsException if sequence is out of range
     */
    public Long delRef(int sequence) throws IndexOutOfBoundsException {
        return this.getRefs().remove(sequence);
    }

    /**
     * Replace a ref at the given sequence position with the given new ref.
     *
     * @param ref      new reference
     * @param sequence position of reference to be replaced
     * @throws IndexOutOfBoundsException if sequence is out of range
     */
    public void replaceRef(long ref, int sequence) throws IndexOutOfBoundsException {
        this.delRef(sequence);
        this.addRef(ref, sequence);
    }

    /**
     * Replace old ref with new ref while retaining position of reference.
     *
     * @param old_ref
     * @param new_ref
     */
    public void replaceRef(long old_ref, long new_ref) {
        int pos = this.refs.indexOf(old_ref);
        this.refs.remove(pos);
        this.refs.add(pos, new_ref);
    }
}
