/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.graph;


/**
 *
 * @author Augusto
 */
public interface EdgeWeightedGraph<Station> extends Graph<Station>{
    /**
     * Get the weight of the edge from node "from" to node "to".
     * 
     * @param from  the starting node of the edge.
     * @param to  then ending node of the edge.
     * @return  the weight of the edge from the starting node to the ending
     * node, if an edge exists, otherwise Double.POSITIVE_INFINITY.
     */
    public double edgeWeight(Station from, Station to);
}
