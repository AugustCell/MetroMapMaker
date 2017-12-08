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
public class ShortestPath {
    static final int V = 9;
    
    public void djikstra(int graph[][], int src){
        int distance[] = new int[V];
        
        Boolean helpSet[] = new Boolean[V];
        
        for(int i = 0; i < V; i++){
            distance[i] = Integer.MAX_VALUE;
            helpSet[i] = false;
        }
        
        distance[src] = 0;
        
        for(int i = 0; i < V-1; i++){
            
        }
    }
}
