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
public class AdjacentList {
    
    
    private int v;
    private int weight;
    AdjacentList(int vL, int wL){
        v = vL;
        weight = wL;
    }
    int getV(){
        return v;
    }
    int getWeight(){
        return weight;
    }
}
