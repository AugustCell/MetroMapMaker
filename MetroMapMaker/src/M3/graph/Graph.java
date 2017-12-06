/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.graph;

import java.util.Iterator;

/**
 *
 * @author Augusto
 */
public interface Graph<Station> {
    /**
     * Test whether a specified node belongs to this graph.
     * 
     * @param node  the node to test.
     * @return true if the specified node belongs to the graph; otherwise
     * false.
     */
    public boolean containsNode(Station node);
    
    /**
     * Determine whether two nodes are adjacent in this graph.
     * 
     * @param from the starting node.
     * @param to the node to be tested for adjacency to the starting node.
     * @return true node "to" can be reached by an edge from node "from";
     * otherwise false.
     */
    public boolean isAdjacent(Station from, Station to);
        
    /**
     * Get an iterator over the nodes of this graph that are adjacent to a
     * specified node.
     * 
     * @param from a specified node, or null.
     * @return an iterator over the nodes of this graph that are adjacent to
     * the specified node, or if the specified node is null, then an iterator
     * over all the nodes of this graph.
     * 
     */
    public Iterator<Station> nodeIterator(Station from);
    
}
