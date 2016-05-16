package com.routesearch.route;

import com.filetool.util.FileUtil;
import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;
import com.routesearch.model.Vertex;
import com.routesearch.util.PathManager;
import com.routesearch.util.PathPrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 4/3/16.
 */
public class BellmanFordRoute extends AbstractRoute{

    //到达必经节点的边的权值惩罚值，需大于所有边权重之和,在取负
    private static final int PENALTY_WEIGHT = -100000;
    //每个节点存储的最多候选路径条数
    private static int candPathNum = 50;

    private PathManager pathManager;
    private VertexInfo[] vertexInfos;

    private final int srcId;
    private final int dstId;
    private final int vertexNum;

    private static class VertexInfo {
        public int maxWeight;
        public int minWeight;
        public List<PathManager.PNode> paths;

        public VertexInfo() {
            maxWeight = Integer.MAX_VALUE;
            minWeight = Integer.MAX_VALUE;
            paths = new ArrayList<PathManager.PNode>();
        }

    }

    public BellmanFordRoute(Graph graph) {
        this.graph = graph;
        srcId = graph.getSrcID();
        dstId = graph.getDstID();
        vertexNum = graph.getVertexNum();

        // set penalty for demands
        List<Integer> demands = graph.getDemandList();
        for (int vid : demands){
            Vertex v = graph.getVertexByID(vid);
            for (int eid : v.inEdge) {
                Edge edge = graph.getEdgeByID(eid);
                edge.cost += PENALTY_WEIGHT;
            }
        }

        // set candPathNum
        if(graph.getVertexNum() <= 80){
            candPathNum = 50;
        } else if(graph.getVertexNum() <= 100){
            candPathNum *= 6;
        } else if(graph.getVertexNum() <= 200) {
            candPathNum *= 5;
        } else if(graph.getVertexNum() <= 350) {
            candPathNum = 200;    //case 9, 10
        }else {
            candPathNum = 200;    //case 11,12,13,14,15
        }

        pathManager = new PathManager(graph);

        vertexInfos = new VertexInfo[graph.getVertexNum()];
        for (int i = 0; i < vertexNum; i++){
            vertexInfos[i] = new VertexInfo();
        }
    }

    @Override
    public Path searchRoute() {

        VertexInfo[] nextVertexInfos = new VertexInfo[graph.getVertexNum()];
        for (int i = 0; i < vertexNum; i++) {
            nextVertexInfos[i] = new VertexInfo();
        }

        //设置起点
        vertexInfos[srcId].maxWeight = 0;
        vertexInfos[srcId].minWeight = 0;
        vertexInfos[srcId].paths.add(pathManager.addPath(srcId, 0));

        nextVertexInfos[srcId].maxWeight = 0;
        nextVertexInfos[srcId].minWeight = 0;
        nextVertexInfos[srcId].paths.add(vertexInfos[srcId].paths.get(0));

        boolean updated;
        Edge edge;
        for (int i = 1; i <= vertexNum - 1; i++) {
            updated = false;
            for (int eid = 0; eid < graph.getEdgeNum(); eid++) {
                edge = graph.getEdgeByID(eid);
                if (!edge.valid)
                    continue;
                if (vertexInfos[edge.sourceID].maxWeight == Integer.MAX_VALUE) {
                    continue;
                }
                if (vertexInfos[edge.sourceID].minWeight + edge.cost < vertexInfos[edge.destinationID].maxWeight) {
                    updatePaths(vertexInfos, nextVertexInfos, edge);
                    updated = true;
                }
            }
            if (!updated)
                break;

            for (int j = 0; j < vertexNum; j++){
                vertexInfos[j] = nextVertexInfos[j];
            }
        }

        int demandSize = graph.getDemandList().size();
        if (vertexInfos[dstId].minWeight > PENALTY_WEIGHT * demandSize
                && vertexInfos[dstId].minWeight < PENALTY_WEIGHT * (demandSize - 1)) {
            PathManager.PNode node = vertexInfos[dstId].paths.get(0);
            return pathManager.getPath(node);
        }
        return null;
    }


    private boolean updatePaths(VertexInfo[] curInfo, VertexInfo[] nextInfo, Edge edge) {

        boolean updated = false;
        int src = edge.sourceID, dst = edge.destinationID;
        VertexInfo srcInfo = curInfo[src], dstInfo = nextInfo[dst];

        List<PathManager.PNode> mergedPaths = new ArrayList<PathManager.PNode>();
        PathManager.PNode newPath;
        int pathCnt = 0;
        int i = 0, j = 0;

        //新增加的路径和原有路径归并排序取前maxPaths个
        while (pathCnt < candPathNum) {

            if (i == srcInfo.paths.size() && j == dstInfo.paths.size()){
                break;
            } else if (i == srcInfo.paths.size()) {
                mergedPaths.add(dstInfo.paths.get(j));
                j++;
                pathCnt++;
            } else if (pathManager.isPassed(srcInfo.paths.get(i), dst)){
                //路径已经经过该点
                i++;
                continue;
            } else if (j == dstInfo.paths.size()) {
                newPath = pathManager.addNode(srcInfo.paths.get(i), dst, edge);
                mergedPaths.add(newPath);
                i++;
                pathCnt++;
                updated = true;
            } else if (pathManager.isPrefix(srcInfo.paths.get(i), dstInfo.paths.get(j))){
                //如果需要添加的路径已经存在
                i++;
                continue;
            } else if (srcInfo.paths.get(i).weight + edge.cost < dstInfo.paths.get(j).weight) {
                newPath = pathManager.addNode(srcInfo.paths.get(i), dst, edge);
                mergedPaths.add(newPath);
                i++;
                pathCnt++;
                updated = true;
            } else {
                mergedPaths.add(dstInfo.paths.get(j));
                j++;
                pathCnt++;
            }
        }

        if (updated){
            dstInfo.paths = mergedPaths;
            dstInfo.minWeight = mergedPaths.get(0).weight;
            dstInfo.maxWeight = mergedPaths.get(mergedPaths.size() - 1).weight;
        }

        return updated;
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
        BellmanFordRoute bfr = new BellmanFordRoute(graph);
        Path path = bfr.searchRoute();
        path.setWeight(path.getWeight() - PENALTY_WEIGHT * graph.getDemandList().size());
        PathPrinter.printEdges(graph, path);
    }
}
