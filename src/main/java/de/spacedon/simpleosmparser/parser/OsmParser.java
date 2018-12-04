package de.spacedon.simpleosmparser.parser;

import de.spacedon.simpleosmparser.osm.OSMElement;
import de.spacedon.simpleosmparser.osm.OSMNode;
import de.spacedon.simpleosmparser.osm.OSMRelation;
import de.spacedon.simpleosmparser.osm.OSMWay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Philipp Thöricht
 */
public abstract class OsmParser {

    protected HashMap<Long, OSMNode> nodes;
    protected HashMap<Long, OSMWay> ways;
    protected HashMap<Long, OSMRelation> relations;

    /**
     *
     */
    public OsmParser() {
        this.nodes = new HashMap<>();
        this.ways = new HashMap<>();
        this.relations = new HashMap<>();
    }

    /**
     * Merges the overhandes parser (osm files) into this. Especially takes care
     * of IDs and can merge double nodes (regarding position).
     *
     * @param parser            the osm elements of this osmparser might change if several
     *                          IDs are doublings of IDs of this osmparser.
     * @param negative_ids      allow negative IDs?
     * @param nodetags_to_merge merge nodes at same position with same values
     *                          for these keys, can be left empty
     * @return a map containing mapping for every osm element type that was changed
     * (see OSMELement.NODE, OSMElement.WAY, OSMElement.RELATION). The inner
     * maps containing the old IDs as keys and the new IDs as values.
     */
    public HashMap<Integer, HashMap<Long, Long>> mergeParsers(OsmParser parser, boolean negative_ids, List<String> nodetags_to_merge) {
        HashMap<Integer, HashMap<Long, Long>> changed_ids = new HashMap<>();
        ArrayList<Long> remove_nodes = new ArrayList<>();
        ArrayList<OSMNode> new_nodes = new ArrayList<>();
        HashMap<Long, Long> merge_nodes = new HashMap<>();    // key -> this node, value -> parser node
        long min_id = 0L;
        long max_id = 0L;
        if (!this.nodes.isEmpty() && !parser.nodes.isEmpty()) {
            min_id = Math.min(Collections.min(this.nodes.keySet()), Collections.min(parser.nodes.keySet()));
            max_id = Math.max(Collections.max(this.nodes.keySet()), Collections.max(parser.nodes.keySet()));
        }
        changed_ids.put(OSMElement.NODE, new HashMap<Long, Long>());
        for (Long id : parser.getNodes().keySet()) {
            if (this.nodes.containsKey(id)) {
                OSMNode n = parser.getNode(id);
                remove_nodes.add(id);
                if (negative_ids) {
                    n.setId(--min_id);
                } else {
                    n.setId(++max_id);
                }
                new_nodes.add(n);
                changed_ids.get(OSMElement.NODE).put(id, n.getId());
            }
        }
        for (Long id : remove_nodes) {
            parser.getNodes().remove(id);
        }
        for (OSMNode n : new_nodes) {
            parser.putNode(n);
        }
        this.nodes.putAll(parser.getNodes());

        for (Long id : parser.getNodes().keySet()) {
            if (nodetags_to_merge != null && !nodetags_to_merge.isEmpty()) {
                long same_node = this.samePos(id, parser.getNode(id).getLat(), parser.getNode(id).getLon(), 0);
                if (same_node != 0) {
                    boolean merge = true;
                    for (String key : nodetags_to_merge) {
                        String thisvalue = this.nodes.get(same_node).getTag(key);
                        String parservalue = parser.getNode(id).getTag(key);
                        if (thisvalue == null || parservalue == null || !thisvalue.equals(parservalue)) {
                            merge = false;
                            break;
                        }
                    }

                    if (merge) {
                        merge_nodes.put(same_node, id);
                        changed_ids.get(OSMElement.NODE).put(id, same_node);
                    }
                }
            }
        }

        // refresh refs in all ways:
        for (OSMWay w : parser.getWays().values()) {
            for (int i = 0; i < remove_nodes.size(); i++) {
                long old_id = remove_nodes.get(i);
                if (w.getRefs().contains(old_id)) {
                    int pos = w.getRefs().indexOf(old_id);
                    w.replaceRef(new_nodes.get(i).getId(), pos);
                }
            }
        }
        for (Long this_n_id : merge_nodes.keySet()) {
            long parser_n_id = merge_nodes.get(this_n_id);
            this.nodes.remove(parser_n_id);
            for (OSMWay w : parser.getWays().values()) {
                if (w.getRefs().contains(parser_n_id)) {
                    w.replaceRef(parser_n_id, this_n_id);
                }
            }
        }

        //TODO refresh refs in all relations
        ArrayList<Long> remove_ways = new ArrayList<>();
        ArrayList<OSMWay> new_ways = new ArrayList<>();
        min_id = 0L;
        max_id = 0L;
        if (!this.ways.isEmpty() && !parser.ways.isEmpty()) {
            min_id = Math.min(Collections.min(this.ways.keySet()), Collections.min(parser.ways.keySet()));
            max_id = Math.max(Collections.max(this.ways.keySet()), Collections.max(parser.ways.keySet()));
        }
        changed_ids.put(OSMElement.WAY, new HashMap<Long, Long>());
        for (Long id : parser.getWays().keySet()) {
            if (this.ways.containsKey(id)) {
                OSMWay w = parser.getWay(id);
                remove_ways.add(id);
                if (negative_ids) {
                    w.setId(--min_id);
                } else {
                    w.setId(++max_id);
                }
                new_ways.add(w);
                changed_ids.get(OSMElement.WAY).put(id, w.getId());
            }
        }
        for (Long id : remove_ways) {
            parser.getWays().remove(id);
        }
        for (OSMWay w : new_ways) {
            parser.putWay(w);
        }
        this.ways.putAll(parser.getWays());
        //TODO refresh refs in all relations

        LinkedList<Long> remove_relations = new LinkedList<>();
        LinkedList<OSMRelation> new_relations = new LinkedList<>();
        min_id = 0L;
        max_id = 0L;
        if (!this.relations.isEmpty() && !parser.relations.isEmpty()) {
            min_id = Math.min(Collections.min(this.relations.keySet()), Collections.min(parser.relations.keySet()));
            max_id = Math.max(Collections.max(this.relations.keySet()), Collections.max(parser.relations.keySet()));
        }
        changed_ids.put(OSMElement.RELATION, new HashMap<Long, Long>());
        for (Long id : parser.getRelations().keySet()) {
            if (this.relations.containsKey(id)) {
                OSMRelation r = parser.getRelation(id);
                remove_relations.add(id);
                if (negative_ids) {
                    r.setId(--min_id);
                } else {
                    r.setId(++max_id);
                }
                new_relations.add(r);
                changed_ids.get(OSMElement.RELATION).put(id, r.getId());
            }
        }
        for (Long id : remove_relations) {
            parser.getRelations().remove(id);
        }
        for (OSMRelation r : new_relations) {
            parser.putRelation(r);
        }
        this.relations.putAll(parser.getRelations());

        return changed_ids;
    }

    /**
     * @param lat
     * @param lon
     * @return the ID of the first node with the same coordinates or just 0 if
     * no node was found.
     */
    public long sameNode(double lat, double lon) {
        for (OSMNode node : this.nodes.values()) {
            if (node.getLat() == lat && node.getLon() == lon) {
                return node.getId();
            }
        }
        return 0;
    }

    /**
     * Find node at the same position within a given tolerance area.
     *
     * @param n_id      the id of the node to check against
     * @param lat
     * @param lon
     * @param tolerance
     * @return the ID of the first node which is within the area or 0 if none is
     * found.
     */
    public long samePos(long n_id, double lat, double lon, double tolerance) {
        for (OSMNode node : this.nodes.values()) {
            if (n_id != node.getId() && lat - tolerance <= node.getLat()
                    && node.getLat() <= lat + tolerance
                    && lon - tolerance <= node.getLon() && node.getLon() <= lon + tolerance) {
                return node.getId();
            }
        }
        return 0;
    }

    /**
     * Compares two ways based on their coordinates and the sequence of the
     * coordinates.
     *
     * @param w1_id
     * @param w2_id
     * @return false if number of nodes in the two ways are different or if two
     * nodes at the same index are different based on their coordinates.
     */
    public boolean samePathway(long w1_id, long w2_id) {
        if (this.ways.get(w1_id).getRefs().size() != this.ways.get(w2_id).getRefs().size()) {
            return false;
        }
        ArrayList<Double> latlist = new ArrayList<>();
        ArrayList<Double> lonlist = new ArrayList<>();
        for (Long n1_id : this.ways.get(w1_id).getRefs()) {
            latlist.add(this.nodes.get(n1_id).getLat());
            lonlist.add(this.nodes.get(n1_id).getLon());
        }
        for (Long n2_id : this.ways.get(w2_id).getRefs()) {
            int index = this.ways.get(w2_id).getRefs().indexOf(n2_id);
            if (latlist.get(index) != this.nodes.get(n2_id).getLat()
                    || lonlist.get(index) != this.nodes.get(n2_id).getLon()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes all nodes, ways and relations from this parser and copies those
     * from overhanded parser to this one.
     *
     * @param parser
     */
    public void setParser(OsmParser parser) {
        this.nodes = new HashMap<>();
        for (OSMNode n : parser.getNodes().values()) {
            this.putNode(n);
        }
        this.ways = new HashMap<>();
        for (OSMWay w : parser.getWays().values()) {
            this.putWay(w);
        }
        this.relations = new HashMap<>();
        for (OSMRelation r : parser.getRelations().values()) {
            this.putRelation(r);
        }
    }

    /**
     * @return the nodes
     */
    public HashMap<Long, OSMNode> getNodes() {
        return nodes;
    }

    /**
     * @param id
     * @return
     */
    public OSMNode getNode(long id) {
        return this.nodes.get(id);
    }

    /**
     * A node with the same ID in the parser will be overwritten.
     *
     * @param node
     */
    public void putNode(OSMNode node) {
        this.nodes.put(node.getId(), node);
    }

    /**
     * Replaces node with old_id with the given new node. Takes care of refs in
     * ways and members in relations.
     *
     * @param old_id
     * @param node
     */
    public void replaceNode(long old_id, OSMNode node) {
        this.nodes.remove(old_id);
        this.nodes.put(node.getId(), node);

        for (OSMWay w : this.ways.values()) {
            if (w.getRefs().contains(old_id)) {
                w.replaceRef(old_id, node.getId());
            }
        }

        for (OSMRelation r : this.relations.values()) {
            for (Long n_id : r.getAllMembers().get(0).keySet()) {
                if (n_id.equals(old_id)) {
                    r.replaceMember(old_id, node.getId(), OSMElement.NODE);
                }
            }
        }
    }

    /**
     * @return the ways
     */
    public HashMap<Long, OSMWay> getWays() {
        return ways;
    }

    /**
     * @param id
     * @return
     */
    public OSMWay getWay(long id) {
        return this.ways.get(id);
    }

    /**
     * @param way
     */
    public void putWay(OSMWay way) {
        this.ways.put(way.getId(), way);
    }

    /**
     * @return the relations
     */
    public HashMap<Long, OSMRelation> getRelations() {
        return relations;
    }

    /**
     * @param id
     * @return
     */
    public OSMRelation getRelation(long id) {
        return this.relations.get(id);
    }

    /**
     * @param relation
     */
    public void putRelation(OSMRelation relation) {
        this.relations.put(relation.getId(), relation);
    }

    /**
     * Parser is empty if it contains neither nodes nor ways nor relations.
     *
     * @return
     */
    public boolean isEmpty() {
        return this.nodes.isEmpty() && this.ways.isEmpty() && this.relations.isEmpty();
    }
}
