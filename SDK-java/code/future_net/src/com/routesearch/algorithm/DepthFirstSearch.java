package com.routesearch.algorithm;

import com.filetool.util.FileUtil;
import com.routesearch.model.Graph;

/**
 * Created by sunny on 16/3/16.
 */
public final class DepthFirstSearch {

    private Graph graph;
    private boolean[] visited;
    private int[] preVisit;
    private int[] postVisit;
    private int visitCnt;

    public DepthFirstSearch(Graph graph) {
        this.graph = graph;
    }

    private void clear() {
        int n = graph.getVertexNum();
        visited = new boolean[n];
        preVisit = new int[n];
        postVisit = new int[n];
        visitCnt = 0;
    }

    public int[] dfs(){
        clear();
        for (int vid = 0; vid < graph.getVertexNum(); vid++) {
            if (!visited[vid])
                explore(vid);
        }
        return postVisit;
    }

    //只遍历startId作为起点的路径
    public int[] dfs(int startId){
        clear();
        explore(startId);
        return postVisit;
    }

    //if have a path from src to dst
    public boolean isReachable(int from, int to){
        clear();
        explore(from);
        return postVisit[to] != 0;
    }

    private void explore(int vid){
        visited[vid] = true;
        preVisit[vid] = visitCnt++;
        int uid;
        for (int edgeID : graph.getVertexByID(vid).outEdge){
            uid = graph.getEdgeByID(edgeID).destinationID;
            if (!visited[uid])
                explore(uid);
        }
        postVisit[vid] = visitCnt++;
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
        DepthFirstSearch dfs = new DepthFirstSearch(graph);
        dfs.dfs();
    }
}

