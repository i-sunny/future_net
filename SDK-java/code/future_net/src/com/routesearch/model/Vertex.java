package com.routesearch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 16/3/15.
 */
public class Vertex {
    public int oldID;
    public int id;
    public boolean mustPass;
    public int inDegree;
    public int outDegree;
    public List<Integer> inEdge = new ArrayList<Integer>();           //inEdge id
    public List<Integer> outEdge = new ArrayList<Integer>();    //outEdge id

    public Vertex(int oldID, int id, boolean mustPass, int inDegree, int outDegree) {
        this.oldID = oldID;
        this.id = id;
        this.mustPass = mustPass;
        this.inDegree = inDegree;
        this.outDegree = outDegree;
    }

    public Vertex() {}
    
    public static Vertex copyVertex(Vertex from) {

        Vertex to = new Vertex();
        to.oldID = from.oldID;
        to.id = from.id;
        to.mustPass = from.mustPass;
        to.inDegree = from.inDegree;
        to.outDegree = from.outDegree;
        to.inEdge = new ArrayList<Integer>();
        for (int in : from.inEdge) {
            to.inEdge.add(in);
        }
        to.outEdge = new ArrayList<Integer>();
        for (int out : from.outEdge) {
            to.outEdge.add(out);
        }
        return to;
    }
}
