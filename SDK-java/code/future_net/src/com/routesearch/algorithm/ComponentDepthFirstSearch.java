package com.routesearch.algorithm;

import com.filetool.util.FileUtil;
import com.routesearch.model.Graph;
import com.routesearch.util.PreHandler;

import java.util.List;
import java.util.Set;

/**
 * Created by sunny on 16/3/18.
 */
public class ComponentDepthFirstSearch {

    private Graph graph;
    private List<Set<Integer>> components;
    private boolean[] visited;
    private boolean[] componented;
    private int[] preVisit;
    private int[] postVisit;
    private int visitCnt;

    public ComponentDepthFirstSearch(Graph graph) {
        this.graph = graph;
    }

    private void clear() {
        int n = graph.getVertexNum();
        visited = new boolean[n];
        componented = new boolean[n];
        preVisit = new int[n];
        postVisit = new int[n];
        visitCnt = 0;
        components = new StronglyConnectedComponents(graph).scc(-1);
    }

    public int[] dfs() {
        clear();
        for (int vid = 0; vid < graph.getVertexNum(); vid++) {
            if (!visited[vid])
                explore(vid);
        }
        return postVisit;
    }

    //只遍历startId作为起点的路径
    public int[] dfs(int startId) {
        clear();
        explore(startId);
        return postVisit;
    }

    //if have a path from src to dst
    public boolean isReachable(int from, int to) {
        clear();
        explore(from);
        return postVisit[to] != 0;
    }

    private void explore(int vid) {
        visited[vid] = true;
        //set componented to all vertices in same component
        int ci = -1;    //component index
        if (!componented[vid]) {
            Set<Integer> comp;
            for (ci = 0; ci < components.size(); ci++) {
                comp = components.get(ci);
                if (comp.contains(vid)) {
                    for (int uid : comp) {
                        componented[uid] = true;
                    }
                    break;
                }
            }
            preVisit[vid] = visitCnt++;
        }

        for (int edgeID : graph.getVertexByID(vid).outEdge) {
            int uid = graph.getEdgeByID(edgeID).destinationID;
            if (!visited[uid])
                explore(uid);
        }

        if (ci != -1) {
            postVisit[vid] = visitCnt++;
            for (int uid : components.get(ci)) {
                postVisit[uid] = postVisit[vid];
            }
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
//        PreHandler.phr(graph);
        ComponentDepthFirstSearch cdfs = new ComponentDepthFirstSearch(graph);
        cdfs.dfs(graph.getSrcID());
    }
}
