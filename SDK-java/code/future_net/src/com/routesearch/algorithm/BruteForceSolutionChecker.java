package com.routesearch.algorithm;

import com.filetool.util.FileUtil;
import com.routesearch.algorithm.DepthFirstSearch;
import com.routesearch.algorithm.RouteValidChecker;
import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Vertex;
import com.routesearch.util.PreHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 16/3/16.
 */
public final class BruteForceSolutionChecker {

    private Graph graph;

    public BruteForceSolutionChecker(Graph graph) {
        this.graph = graph;
    }


    public boolean searchRoute() {
//        if (!PreHandler.phr(graph)) return false;
        PreHandler.phr(graph);
        return search();
    }

    private boolean search() {
        //递归出口
        if (graph.getSrcID() == graph.getDstID()) {
            return graph.getDemandList().isEmpty();
        }
        if (graph.getDemandList().isEmpty()) {
            return new DepthFirstSearch(graph).isReachable(graph.getSrcID(), graph.getDstID());
        }
        // valid test, fail fast
        if (!RouteValidChecker.rvc(graph)) {
            return false;
        }

        Vertex src = graph.getVertexByID(graph.getSrcID());
        List<Integer> outEdges = new ArrayList<Integer>();
        for (int eid : src.outEdge){
            outEdges.add(eid);
        }

        Edge edge;
        List<Integer> backupEdges;     //backup src edges and nxtSrc inEdges
        int backupDemand;              // -1: invalid
        for (int eid : outEdges) {
            edge = graph.getEdgeByID(eid);
            //update
            backupEdges = new ArrayList<Integer>();
            backupDemand  = updateGraph(edge, backupEdges);
            graph.setSrcID(edge.destinationID);

            if (search()) {
                return true;
            }
            //restore
            restoreGraph(backupEdges, backupDemand);
            graph.setSrcID(src.id);
        }
        return false;
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
        BruteForceSolutionChecker checker = new BruteForceSolutionChecker(graph);
        System.out.println(checker.searchRoute());
    }
}

