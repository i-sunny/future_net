package com.routesearch.model;

/**
 * Created by sunny on 16/3/15.
 */
public class Edge {
    public int oldID;
    public int id;            // edge id
    public int sourceID;      //vertex from id
    public int destinationID; //vertex to id
    public int cost;
    public boolean valid = true;    //true for default

    public Edge(int oldID, int id, int sourceID, int destinationID, int cost) {
        this.oldID = oldID;
        this.id = id;
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.cost = cost;
    }
}
