package com.routesearch.route;

import com.filetool.util.FileUtil;
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
public final class TracingBackRoute {

    private Graph graph;
    private Path validPath = new Path();
    private List<Integer> backupEdges;    //backup for deleted edges
    private List<Integer> backupDemands = new ArrayList<Integer>();  //backup for deleted demand vertices
    private DijkstaShortestPath dsp;

    public TracingBackRoute(Graph graph) {
        this.graph = graph;
    }

    public boolean searchRoute() {

        int curSrcId = graph.getSrcID();
        boolean isValid;
        while (true) {

            dsp = new DijkstaShortestPath(graph, curSrcId).shortestPath();

            Map<Integer, Integer> costMap = new TreeMap<Integer, Integer>();
            SortedSet<Map.Entry<Integer, Integer>> costSortedSet;

            for (Integer vid : graph.getDemandList()) {
                if (vid != curSrcId) {
                    if (dsp.getCost(vid) == Integer.MAX_VALUE)
                        return false;
                    costMap.put(vid, dsp.getCost(vid));     // map of [vId, cost]
                }
            }
            //sort cost(src->u) by ascending order, u in V'
            costSortedSet = entriesSortedByValues(costMap);

            Path path;
            isValid = false;
            backupEdges = new ArrayList<Integer>();
            backupDemands = new ArrayList<Integer>();
            for (Map.Entry<Integer, Integer> entry: costSortedSet) {
                int vid = entry.getKey();

                path = dsp.getShortestPath(vid);
                // 1.update
                // several-vertices-step update
                removeEdgesInPath(path);
                curSrcId = graph.getSrcID();  // backup
                graph.setSrcID(vid);          // update src

                // 2.valid test
                if (RouteValidChecker.rvc(graph)) {
                    // if valid, step forward
                    isValid = true;
                    validPath.addAll(path);
                    curSrcId = vid;
                    break;
                } else {
                    // if not valid, roll back
                    graph.setSrcID(curSrcId);
                    restoreEdgesInPath();
                }
            }

            // if fail several-vertices-step update,
            // then try one-vertex-step update
            if (!isValid) {

                costMap = new TreeMap<Integer, Integer>();
                Edge edge;
                Vertex curSrc = graph.getVertexByID(curSrcId);
                for (Integer eid : curSrc.outEdge) {
                    edge = graph.getEdgeByID(eid);
                    costMap.put(edge.destinationID, edge.cost);   // map of [vId, cost]
                }
                costSortedSet = entriesSortedByValues(costMap);

                backupEdges = new ArrayList<Integer>();
                backupDemands = new ArrayList<Integer>();
                for (Map.Entry<Integer, Integer> entry: costSortedSet) {
                    int vid = entry.getKey();

                    path = new Path();
                    edge = graph.getEdge(curSrcId, vid);
                    path.addEdge(edge.id, entry.getValue());
                    // 1.update
                    removeEdgesInPath(path);
                    curSrcId = graph.getSrcID();  // backup
                    graph.setSrcID(vid);          // update src

                    // 2.valid test
                    if (RouteValidChecker.rvc(graph)) {
                        // if valid, step forward
                        isValid = true;
                        validPath.addAll(path);
                        curSrcId = vid;
                        break;
                    } else {
                        // if not valid, roll back
                        graph.setSrcID(curSrcId);
                        restoreEdgesInPath();
                    }
                }
            }

            if (!isValid) {
                return false;
            }

            // have passed all demand nodes
            if (graph.getDemandList().size() == 0) {
                //add the last path to dst
                dsp = new DijkstaShortestPath(graph, curSrcId).shortestPath();
                path = dsp.getShortestPath(graph.getDstID());
                validPath.addAll(path);
                return true;
            }
        }
    }

    //graph pre-handle
    public Path getValidPath() {
        return validPath;
    }



    //删除path上each vertex u's inEdges and outEdges
    //path的终点d只删除inEdges
    //删除之前先做备份 便于恢复
    private void removeEdgesInPath(Path path) {
        Edge edge;
        int l = 0;
        for (int eid : path.getEdges()) {
            edge = graph.getEdgeByID(eid);

            //delete sv edges
            Vertex sv = graph.getVertexByID(edge.sourceID);
            if (sv.inDegree != 0)
                backupEdges.addAll(sv.inEdge);
            while (sv.inDegree != 0) graph.removeEdge(sv.inEdge.get(0));
            if (sv.outDegree != 0)
                backupEdges.addAll(sv.outEdge);
            while (sv.outDegree != 0) graph.removeEdge(sv.outEdge.get(0));

            //delete dv edges
            Vertex dv = graph.getVertexByID(edge.destinationID);
            if (dv.inDegree != 0)
                backupEdges.addAll(dv.inEdge);
            while (dv.inDegree != 0) graph.removeEdge(dv.inEdge.get(0));
            //don't remove path's last vertex's outEdges
            if (++l != path.getEdges().size()) {
                if (dv.outDegree != 0)
                    backupEdges.addAll(dv.outEdge);
                while (dv.outDegree != 0) graph.removeEdge(dv.outEdge.get(0));
            }
            if (graph.getDemandList().contains(dv.id)) {
                backupDemands.add(dv.id);
                graph.removeFromDemandList(dv.id);
            }
        }
    }

    private void restoreEdgesInPath() {

        for (int eid : backupEdges) {
            graph.restoreEdge(eid);
        }
        for (int vid : backupDemands) {
            graph.restoreToDemandList(vid);
        }
    }

    //支持TreeMap根据value排序
    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
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
        TracingBackRoute rbt = new TracingBackRoute(graph);
        if (rbt.searchRoute()) {
            PathPrinter.printMappedEdges(graph, rbt.getValidPath());
            PathPrinter.printMappedVertices(graph, rbt.getValidPath());
        } else {
            System.out.println("---------- No Path ----------");
            PathPrinter.printMappedEdges(graph, rbt.getValidPath());
            PathPrinter.printMappedVertices(graph, rbt.getValidPath());
        }

        //recheck
        graph = new Graph(graphContent, conditionContent);
        System.out.println("Soution is: " + RouteValidChecker.isValidSolution(graph, rbt.getValidPath()));
    }
}
