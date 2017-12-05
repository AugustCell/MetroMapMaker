/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.data;

/**
 *
 * @author Augusto
 */
public class StationEnds {
    public String lineName;
    public String leftEnd;
    public String rightEnd;
    
    public StationEnds(){
        lineName = "";
        leftEnd = "";
        rightEnd = "";
    }
    
    public String getLineName(){
        return lineName;
    }
    public void setLineName(String name){
        lineName = name;
    }
    public String getLeftEnd(){
        return leftEnd;
    }
    public void setLeftEnd(String name){
        leftEnd = name;
    }
    public String getRightEnd(){
        return rightEnd;
    }
    public void setRightEnd(String name){
        rightEnd = name;
    }
}
