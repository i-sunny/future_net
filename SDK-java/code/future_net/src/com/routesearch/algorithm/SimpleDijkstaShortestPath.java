package com.routesearch.algorithm;

import com.filetool.util.FileUtil;
import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import javax.print.DocFlavor;
import java.util.*;

/**
 * Created by sunny on 16/3/16.
 */
public class SimpleDijkstaShortestPath {

    // distance of (src to each vertex)
    private static int[] dis;

    private static void shortestPath(Graph graph, int srcID) {

        dis = new int[graph.getVertexNum()];
        Arrays.fill(dis, Integer.MAX_VALUE);
        dis[srcID] = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(11, new Comparator<Integer>() {
            @Override
            public int compare(Integer v1, Integer v2) {
                return dis[v1] < dis[v2] ? -1 : dis[v1] > dis[v2] ? 1 : 0;
            }
        });

        queue.add(srcID);

        int vid;
        while (!queue.isEmpty()) {
            vid = queue.poll();
            int uid, cost;
            Edge edge;
            for (int edgeID : graph.getVertexByID(vid).outEdge) {
                edge = graph.getEdgeByID(edgeID);
                uid = edge.destinationID;
                //Relax
                cost = dis[vid] + edge.cost;
                if (dis[uid] > cost) {
                    dis[uid] = cost;
                    queue.add(uid);
                }
            }
        }
    }

    public static int getCost(Graph graph, int srcID, int dstID, boolean reRun) {
        if (reRun)
            shortestPath(graph, srcID);
        return dis[dstID];
    }

    public static boolean isReachable(Graph graph, int srcID, int dstID, boolean reRun) {
        if (reRun)
            shortestPath(graph, srcID);
        return dis[dstID] != Integer.MAX_VALUE;
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
        int srcID = 2;
        for (int i = 0; i < graph.getVertexNum(); i++) {
            System.out.println(srcID + "->" + i + ": " + SimpleDijkstaShortestPath.isReachable(graph, srcID, i, true));
        }
    }
}
