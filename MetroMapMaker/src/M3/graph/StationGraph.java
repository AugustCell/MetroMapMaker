/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.graph;
import M3.graph.AbstractEdgeWeightedGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Augusto
 */
public class StationGraph extends AbstractEdgeWeightedGraph<Station>{

    //Set of stations in this set
    private final HashSet<Station> stationSet;
    
    private final HashMap<Station, Set<Station>> stationMap;
    
    public StationGraph(Iterator<Station> stations) {
        stationSet = new HashSet<>();
        stationMap = new HashMap<>();
        while (stations.hasNext()) {
            stationSet.add(stations.next());
        }
        Station[] help = stationSet.toArray(new Station[stationSet.size()]);
        for(int i = 0; i < help.length - 1; i++){
            addToMap(help[i], help[i + 1]);
            
        }
    }

    private void addToMap(Station from, Station to){
        Set<Station> m = stationMap.get(from);
        if(m == null){
            m = new HashSet<>();
            stationMap.put(from, m);
        }
        m.add(to);
    }
    @Override
    public double edgeWeight(Station from, Station to) {
        if(isAdjacent(from, to)){
            return 1;
        }
        else{
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public boolean containsNode(Station node) {
        return stationSet.contains(node);
    }

    @Override
    public boolean isAdjacent(Station from, Station to) {
        Set<Station> l = stationMap.get(from);
        if(l == null){
            return false;
        }
        else{
            return l.contains(to);
        }
    }

    @Override
    public Iterator<Station> nodeIterator(Station from) {
        if(from == null){
            return stationSet.iterator();
        }
        else{
            Set<Station> m = stationMap.get(from);
            if(m == null){
                m = new HashSet<>();
            }
            return m.iterator();
        }
    }
    
}
