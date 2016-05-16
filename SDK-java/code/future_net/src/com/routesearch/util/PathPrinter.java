package com.routesearch.util;

import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;

/**
 * Created by sunny on 16/3/17.
 */
public class PathPrinter {

    // print old edge
    public static void printEdges(Graph graph, Path path) {
        Edge edge;
        StringBuffer sb = new StringBuffer("Edge Path: ");
        for (int eid : path.getEdges()) {
            edge = graph.getEdgeByID(eid);
            sb.append(edge.oldID).append("|");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("  Weight: ").append(path.getWeight());
        System.out.println(sb);
    }

    public static void printMappedEdges(Graph graph, Path path) {
        System.out.println(path.toString());
    }

    public static void printVertices(Graph graph, Path path) {
        Edge edge;
        StringBuffer sb = new StringBuffer("Vertex Path: ");
        for (int eid : path.getEdges()) {
            edge = graph.getEdgeByID(eid);
            if (eid == path.getEdges().get(0))
                sb.append(graph.getVertexByID(edge.sourceID).oldID).append("|");
            sb.append(graph.getVertexByID(edge.destinationID).oldID).append("|");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("  Weight: ").append(path.getWeight());
        System.out.println(sb);
    }

    public static void printMappedVertices(Graph graph, Path path) {
        Edge edge;
        StringBuffer sb = new StringBuffer("MappedVertex Path: ");
        for (int eid : path.getEdges()) {
            edge = graph.getEdgeByID(eid);
            if (eid == path.getEdges().get(0))
                sb.append(edge.sourceID).append("|");
            sb.append(edge.destinationID).append("|");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("  Weight: ").append(path.getWeight());
        System.out.println(sb);
    }

}
