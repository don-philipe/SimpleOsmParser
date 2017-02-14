package de.spacedon.simpleosmparser.osm;

import java.util.ArrayList;


public class OSMNode extends OSMElement
{
    private double lat;
    private double lon;
    
    public OSMNode(long id, double lat, double lon)
    {
		super(id, OSMElement.NODE);
		this.lat = lat;
		this.lon = lon;
    }
    
    /**
     * Determines if the node is in a bounding box or not.
     * @param bb the boundingbox to check
     * @return true if inside, false if not
     */
    public boolean isInBB(ArrayList<Double> bb)
    {
	double la = this.getLat();
	double lo = this.getLon();
	return la > bb.get(0) && la < bb.get(2) && lo > bb.get(1) && lo < bb.get(3);
    }

    /**
     * @return the lat
     */
    public double getLat()
    {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(double lat)
    {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public double getLon()
    {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(double lon)
    {
        this.lon = lon;
    }
}
