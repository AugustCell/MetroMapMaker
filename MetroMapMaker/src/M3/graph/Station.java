/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.graph;

import java.util.Objects;


/**
 *
 * @author Augusto
 */
public class Station implements Comparable<Station>{
    public String text;
    
    public Station(String name){
        text = name;
    }
    
    public String getText(){
        return text;
    }
    
    @Override
    public String toString(){
        return text;
    }

    @Override
    public int compareTo(Station o) {
        return text.compareTo(o.text);

    }
    
    @Override
    public boolean equals(Object other) {
        if(other == null)
            return false;
        if(other == this)
            return true;
        if(other.getClass() != getClass())
            return false;
        Station sw = (Station)other;
        return text.equals(sw.text);
    }
    
}
