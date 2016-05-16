package com.routesearch.algorithm;

import com.filetool.util.FileUtil;
import com.routesearch.model.Graph;
import com.routesearch.util.PreHandler;

import java.util.*;

/**
 * Created by sunny on 16/3/16.
 */
public final class StronglyConnectedComponents {

    private Graph graph;
    private boolean[] visited;
    private Stack<Integer> stack;
    private int visitCnt;
    private int[] preVisit;
    private List<Set<Integer>> components;

    public StronglyConnectedComponents(Graph graph) {
        this.graph = graph;
    }

    private void clear() {
        int n = graph.getVertexNum();
        visited = new boolean[n];
        stack = new Stack<Integer>();
        visitCnt = 0;
        preVisit = new int[n];
        components = new ArrayList<Set<Integer>>();
    }

    public List<Set<Integer>> scc() {
        clear();
        for (int vid = 0; vid < graph.getVertexNum(); vid++) {
            if (!visited[vid])
                dfs(vid);
        }
        return components;
    }

    private void dfs(int vid) {
        preVisit[vid] = visitCnt++;
        visited[vid] = true;
        stack.add(vid);
        boolean isComponentRoot = true;

        int uid;
        for (int edgeID : graph.getVertexByID(vid).outEdge) {
            uid = graph.getEdgeByID(edgeID).destinationID;
            if (!visited[uid])
                dfs(uid);
            if (preVisit[vid] > preVisit[uid]) {
                preVisit[vid] = preVisit[uid];
                isComponentRoot = false;
            }
        }

        if (isComponentRoot) {
            HashSet<Integer> component = new HashSet<Integer>();
            while (true) {
                int x = stack.pop();
                component.add(x);
                preVisit[x] = Integer.MAX_VALUE;
                if (x == vid)
                    break;
            }
            components.add(component);
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
        StronglyConnectedComponents scc = new StronglyConnectedComponents(graph);
        System.out.println(scc.scc());
    }

}
