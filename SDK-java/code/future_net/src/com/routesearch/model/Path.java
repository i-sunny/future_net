package com.routesearch.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunny on 16/3/16.
 */
public class Path {
    private int weight;
    private List<Integer> edges;

    public Path() {
        edges = new LinkedList<Integer>();
        weight = 0;
    }

    public Path(List<Integer> edges, int weight) {
        this.edges = edges;
        this.weight = weight;
    }

    public void addEdge(int eid, int cost) {
        edges.add(eid);
        weight += cost;
    }

    public void addEdge(int index, int eid, int cost) {
        edges.add(0, eid);
        weight += cost;
    }

    public void addAll(Path path) {
        edges.addAll(path.getEdges());
        weight += path.weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Integer> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("MappedEdge Path: ");
        for (int i : edges) {
            sb.append(i + "|");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("  Weight: " + weight);
        return sb.toString();
    }
}
