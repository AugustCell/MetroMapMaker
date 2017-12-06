/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author Augusto
 */
public abstract class AbstractEdgeWeightedGraph<Station extends Comparable<? super Station>>
        implements EdgeWeightedGraph<Station>{
    
    private class StationWithWeight{
        private final Station station;
        private final double weight;
        
        public StationWithWeight(Station station, double weight){
            this.station = station;
            this.weight = weight;
        }
    } 
    

    public List<Station> minimumWeightPath(Station start, Station goal){
        HashMap<Station, Double> weights = new HashMap<>();
        HashSet<Station> unvisited = new HashSet<>();
        TreeSet<StationWithWeight> treeSet = new TreeSet<>();
        HashMap<Station, Station> predecessor = new HashMap<>();
        double distance = 0;

        
        for(Iterator<Station> it = nodeIterator(null) ; it.hasNext();){
            Station station = it.next();
            unvisited.add(station);
            weights.put(station, Double.POSITIVE_INFINITY);
        }
        weights.put(start, 0.0);
        treeSet.add(new StationWithWeight(start, 0.0));
        
        while(treeSet.size() > 0){
            StationWithWeight sn = treeSet.iterator().next();
            treeSet.remove(sn);
            Station adjacent = sn.station;
            unvisited.remove(adjacent);
            double weightToAdjacent = sn.weight;
            
            if(adjacent.equals(goal)){
                break;
            }
            
            for(Iterator<Station> it = nodeIterator(adjacent); it.hasNext();){
                Station s = it.next();
                if(unvisited.contains(s)){
                    double ew = edgeWeight(adjacent, s);
                    double ow = weights.get(s);
                    double nw = weightToAdjacent + ew;
                    if(nw < ow){
                        weights.put(s, nw);
                        predecessor.put(s, adjacent);
                        sn = new StationWithWeight(s, ow);
                        treeSet.remove(sn);
                        sn = new StationWithWeight(s, nw);
                        treeSet.add(sn);
                        distance = nw;
                    }
                }
            }
        }
        if(weights.get(goal) == Double.POSITIVE_INFINITY){
            return null;
        }
        else{
            List<Station> path = new LinkedList<>();
            Station s = goal;
            while(!start.equals(s)){
                path.add(0, s);
                s = predecessor.get(s);
            }
            path.add(0, start);
            return path;
        }
    }
    
}
