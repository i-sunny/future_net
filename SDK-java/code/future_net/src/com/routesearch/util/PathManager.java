package com.routesearch.util;

import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunny on 4/9/16.
 */
public class PathManager {

    private List<PNode> nodeList;
    final private Graph graph;

    public PathManager(Graph graph) {
        this.graph = graph;
        nodeList = new ArrayList<PNode>();
    }

    //创建一条新路径的起点，返回新路径ID
    public PNode addPath(int vid, int edgeWeight){
        PNode node = new PNode(graph.getVertexNum());
//        node.vid = vid;
        node.eid = -1;
        node.parent = null;   //parent null
        node.weight = edgeWeight;
//        node.length = 0;
//        node.mustPassNum = 0;
        node.visitedVtx.set(vid);

        nodeList.add(node);
        return node;
    }

    //在一条路径末尾增加一条新的边，返回新路径的ID
    public PNode addNode(PNode parentPath, int vid, Edge edge) {
        PNode node = new PNode(graph.getVertexNum());
//        node.vid = vid;
        node.eid = edge.id;
        node.parent = parentPath;
        node.weight = node.parent.weight + edge.cost;
//        node.length = node.parent.length + 1;
//        if (mustPass){
//            node.mustPassNum = (short) (node.parent.mustPassNum + 1);
//        }
        node.visitedVtx = (BitSet) node.parent.visitedVtx.clone();
        node.visitedVtx.set(vid);

        nodeList.add(node);
        return node;
    }

    //判断原路径增加一条边后是否即为目标路径
    public boolean isPrefix(PNode parentPath, PNode path) {
        return path.parent == parentPath;
    }

    //判断路径中是否经过某个节点
    public boolean isPassed(PNode node, int vid) {
        return node.visitedVtx.get(vid);
    }

    //根据PNode获取路径
    public Path getPath(PNode node) {
        Path path = new Path();
        while (node != null && node.parent != null) {
            path.addEdge(node.eid, graph.getEdgeByID(node.eid).cost);
            node = node.parent;
        }
        Collections.reverse(path.getEdges());
        return path;
    }

    public static class PNode {
        public PNode parent;
        public int eid;
//        public int vid;
        public int weight;
//        public int length;
//        public short mustPassNum;
        public BitSet visitedVtx;

        public PNode(int vertexNum) {
            visitedVtx = new BitSet(vertexNum);
        }
    }

}
