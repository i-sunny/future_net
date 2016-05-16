package com.routesearch.algorithm;

import com.filetool.util.FileUtil;
import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;

import java.util.*;

/**
 * Created by sunny on 16/3/16.
 */
public class DijkstaShortestPath {

    private Graph graph;
    private int srcID;       // 单源源点
    private int[] dis;       // distance of (src to each vertex)
    private int[] pred;      // pred[v]表示s->v最短路径上的v的前继点

    public DijkstaShortestPath(Graph graph, int srcID) {
        this.graph = graph;
        this.srcID = srcID;
        int n = graph.getVertexNum();
        dis = new int[n];
        Arrays.fill(dis, Integer.MAX_VALUE);
        dis[srcID] = 0;

        pred = new int[n];
        Arrays.fill(pred, -1);
        pred[srcID] = srcID;         //自己的前缀是自己
    }

    public DijkstaShortestPath shortestPath() {
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
                    pred[uid] = vid;
                    queue.add(uid);
                }
            }
        }
        return this;
    }

    public Path getShortestPath(int dstID) {
        if (pred[dstID] == -1) return null;    //没有可达路径
        List<Integer> vertices = new LinkedList<Integer>(), edges = new ArrayList<Integer>();
        Edge edge;

        int j = dstID;
        while (j != srcID) {
            vertices.add(j);
            j = pred[j];
        }
        vertices.add(srcID);
        Collections.reverse(vertices);
        for (int i = 0; i < vertices.size() - 1; i++) {
            edge = graph.getEdge(vertices.get(i), vertices.get(i + 1));
            edges.add(edge.id);
        }
        return new Path(edges, dis[dstID]);
    }

    public int getCost(int dstID) {
        return dis[dstID];
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
        int src = 6;
        DijkstaShortestPath dsp = new DijkstaShortestPath(graph, src);
        dsp.shortestPath();
        for (int i = 0; i < graph.getVertexNum(); i++) {
            System.out.println(dsp.getShortestPath(i));
        }
    }
}
