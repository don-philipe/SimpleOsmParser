package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMElement;
import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

/**
 *
 * @author Philipp Thöricht
 */
public class SinkImpl implements Sink
{
    private final HashMap<Long, OSMNode> nodes;
    private final HashMap<Long, OSMWay> ways;
    private final HashMap<Long, OSMRelation> relations;
    private final BlockingQueue<OSMElement> itemQueue;
    
    /**
     * 
     * @param nodes
     * @param ways
     * @param relations
     */
    public SinkImpl(HashMap<Long, OSMNode> nodes, HashMap<Long, OSMWay> ways, 
            HashMap<Long, OSMRelation> relations)
    {
        this.nodes = nodes;
        this.ways = ways;
        this.relations = relations;
        this.itemQueue = new LinkedBlockingQueue<>(50000);
    }
    
    /**
     * 
     * @param map 
     */
    @Override
    public void initialize(Map<String, Object> map)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * 
     * @param entityContainer 
     */
    @Override
    public void process(EntityContainer entityContainer)
    {
        Entity entity = entityContainer.getEntity();
        if (entity instanceof Node)
        {
            processNode((Node) entity);
        }
        else if (entity instanceof Way)
        {
            processWay((Way) entity);
        }
        else if (entity instanceof Relation)
        {
            processRelation((Relation) entity);
        }
    }
    
    private void processNode(Node node)
    {
        OSMNode n = new OSMNode(node.getId(), node.getLatitude(), node.getLongitude());
        n.setTimestamp(node.getTimestamp());
        n.setVersion(node.getVersion());
        for(Tag t : node.getTags())
            n.setTag(t.getKey(), t.getValue());
        
        this.nodes.put(n.getId(), n);
    }
    
    private void processWay(Way way)
    {
        OSMWay w = new OSMWay(way.getId());
        w.setTimestamp(way.getTimestamp());
        w.setVersion(way.getVersion());
        int i = 1;
        for(WayNode n : way.getWayNodes())
        {
            w.addRef(n.getNodeId(), i);
            i++;
        }
        for(Tag t : way.getTags())
            w.setTag(t.getKey(), t.getValue());
        
        this.ways.put(w.getId(), w);
    }
    
    private void processRelation(Relation relation)
    {
        OSMRelation r = new OSMRelation(relation.getId(), "");
        r.setTimestamp(relation.getTimestamp());
        r.setVersion(relation.getVersion());
        for(RelationMember m : relation.getMembers())
        {
            if(m.getMemberType() == EntityType.Node)
                r.addMember(OSMElement.NODE, m.getMemberId(), m.getMemberRole());
            else if(m.getMemberType() == EntityType.Way)
                r.addMember(OSMElement.WAY, m.getMemberId(), m.getMemberRole());
            else if(m.getMemberType() == EntityType.Relation)
                r.addMember(OSMElement.RELATION, m.getMemberId(), m.getMemberRole());
        }
        for(Tag t : relation.getTags())
            r.setTag(t.getKey(), t.getValue());
        
        this.relations.put(r.getId(), r);
    }

    /**
     * 
     */
    @Override
    public void release() { }

    /**
     * 
     */
    @Override
    public void complete() { }
}
