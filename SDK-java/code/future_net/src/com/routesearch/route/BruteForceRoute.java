package com.routesearch.route;

import com.filetool.util.FileUtil;
import com.routesearch.algorithm.DepthFirstSearch;
import com.routesearch.algorithm.DijkstaShortestPath;
import com.routesearch.algorithm.RouteValidChecker;
import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;
import com.routesearch.model.Vertex;
import com.routesearch.util.PathPrinter;
import com.routesearch.util.PreHandler;

import java.util.*;

/**
 * Created by sunny on 16/3/16.
 */
public final class BruteForceRoute {

    private Graph graph;
//    private int upperCost = 150;
    private int curCost = 0;
    public BruteForceRoute(Graph graph) {
        this.graph = graph;
    }


    public Path searchRoute() {
        if (!PreHandler.phr(graph)) return null;

        return search();
    }

    public Path search() {
        Path path = null;

        //递归出口
        if (graph.getSrcID() == graph.getDstID()) {
            if (graph.getDemandList().isEmpty()){
                return new Path();
            } else {
                return null;
            }
        }
        if (graph.getDemandList().isEmpty()) {
            DepthFirstSearch dfs = new DepthFirstSearch(graph);
            if (dfs.isReachable(graph.getSrcID(), graph.getDstID())) {
                DijkstaShortestPath dsp = new DijkstaShortestPath(graph, graph.getSrcID()).shortestPath();
                return dsp.getShortestPath(graph.getDstID());
            } else {
                return null;
            }
        }

        // valid test, fail fast
        if (!RouteValidChecker.rvc(graph)) {
            return null;
        }

        Vertex src = graph.getVertexByID(graph.getSrcID());
        Set<Edge> outEdges = new TreeSet<Edge>(new Comparator<Edge>(){
            @Override
            public int compare(Edge e1, Edge e2) {
                return e1.cost < e2.cost? -1 : 1;
            }
        });
        for (int eid : src.outEdge){
            outEdges.add(graph.getEdgeByID(eid));
        }

        List<Integer> backupEdges;     //backup src edges and nxtSrc inEdges
        int backupDemand;              // -1: invalid
        for (Edge edge : outEdges) {

//            if (curCost + edge.cost > upperCost) continue;
            //update
            curCost += edge.cost;
            backupEdges = new ArrayList<Integer>();
            backupDemand  = updateGraph(edge, backupEdges);
            graph.setSrcID(edge.destinationID);


            Path subPath;
            if ((subPath = search()) != null) {

//                we have a better path, so update
//                if (path == null || (subPath.getWeight() + edge.cost < path.getWeight())) {
                    path = subPath;
                    path.addEdge(0, edge.id, edge.cost);
                return path;
//                }
            }
            //restore
            curCost -= edge.cost;
            restoreGraph(backupEdges, backupDemand);
            graph.setSrcID(src.id);
        }
        return path;
    }

    private int updateGraph(Edge edge, List<Integer> backupEdges) {
        //clear
        int backupDemand = -1;

        //delete src edges
        Vertex src = graph.getVertexByID(edge.sourceID);
        if (src.inDegree != 0) backupEdges.addAll(src.inEdge);
        while (src.inDegree != 0) graph.removeEdge(src.inEdge.get(0));
        if (src.outDegree != 0) backupEdges.addAll(src.outEdge);
        while (src.outDegree != 0) graph.removeEdge(src.outEdge.get(0));

        //delete next src in edges
        Vertex nxt = graph.getVertexByID(edge.destinationID);
        if (nxt.inDegree != 0) backupEdges.addAll(nxt.inEdge);
        while (nxt.inDegree != 0) graph.removeEdge(nxt.inEdge.get(0));

        if (graph.getDemandList().contains(nxt.id)) {
            backupDemand = nxt.id;
            graph.removeFromDemandList(nxt.id);
        }

        return backupDemand;
    }

    private void restoreGraph(List<Integer> backupEdges, int backupDemand) {
        for (int eid : backupEdges) {
            graph.restoreEdge(eid);
        }
        if (backupDemand != -1) {
            graph.restoreToDemandList(backupDemand);
        }
    }

    //test
    //arg: ./test_case/case1/topo.csv ./test_case/case1/demand.csv
    public static void main(String[] args) {
        String graphFilePath = args[0];
        String conditionFilePath = args[1];

        // 读取输入文件
        String graphContent = FileUtil.read(graphFilePath, null);
        String conditionContent = FileUtil.read(conditionFilePath, null);

        Graph graph = new Graph(graphContent, conditionContent);
        BruteForceRoute bfr = new BruteForceRoute(graph);
        Path path = bfr.searchRoute();
        PathPrinter.printEdges(graph, path);
    }
}
