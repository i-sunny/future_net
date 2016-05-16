package com.routesearch.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunny on 16/3/15.
 */
public class Graph {

    private int srcID;
    private int dstID;
    private List<Integer> demandList = new ArrayList<Integer>();

    private int vertexNum = 0;
    private int edgeNum = 0;

    //Vertex old ID映射ID，map[VID]复杂度logV, map.count(VID)复杂度logV
    private Map<Integer, Integer> vertexMap = new HashMap<Integer, Integer>();
    private List<Vertex> vertexList = new ArrayList<Vertex>();
    private Map<Integer, Integer> edgeMap = new HashMap<Integer, Integer>();
    private List<Edge> edgeList = new ArrayList<Edge>();

    public int getSrcID() {
        return srcID;
    }

    public void setSrcID(int srcID) {
        this.srcID = srcID;
    }

    public int getDstID() {
        return dstID;
    }

    public void setDstID(int dstID) {
        this.dstID = dstID;
    }

    public int getEdgeNum() {
        return edgeNum;
    }

    public List<Integer> getDemandList() {
        return demandList;
    }

    public int getVertexNum() {
        return vertexNum;
    }

    public final List<Vertex> getVertexList() {
        return vertexList;
    }

    public final List<Edge> getEdgeList() {
        return edgeList;
    }

    public Vertex getVertexByID(int id) {
        return vertexList.get(id);
    }

    public Edge getEdgeByID(int id) {
        return edgeList.get(id);
    }

    // 由顶点找边，复杂度1
    public Edge getEdge(int sourceID, int destinationID) {
        for (int eid : getVertexByID(sourceID).outEdge) {
            Edge edge = getEdgeByID(eid);
            if (edge.destinationID == destinationID) {
                return edge;
            }
        }
        return null;
    }

    // 添加Vertex old ID映射ID，返回映射后的ID，复杂度1
    private int addVertex(int oldID) {
        Vertex vertex = new Vertex(oldID, vertexNum, false, 0, 0);
        vertexMap.put(oldID, vertexNum);
        vertexList.add(vertex);
        return vertexNum++;
    }

    // 添加Edge old ID映射ID，复杂度1
    private int addEdge(int oldID, int sourceID, int destinationID, int cost) {
        Edge edge;
        //两点同向存在多条边 取最小的一条
        if ((edge = getEdge(sourceID, destinationID)) != null) {
            if (cost < edge.cost) {
                edgeMap.remove(edge.oldID);
                edgeMap.put(oldID, edge.id);
                edge.oldID = oldID;
                edge.cost = cost;
            }
            return edge.id;
        } else {
            edge = new Edge(oldID, edgeNum, sourceID, destinationID, cost);
            edgeMap.put(oldID, edgeNum);
            edgeList.add(edge);
            Vertex sv = getVertexByID(sourceID), dv = getVertexByID(destinationID);
            sv.outDegree++;
            sv.outEdge.add(edgeNum);
            dv.inDegree++;
            dv.inEdge.add(edgeNum);
            return edgeNum++;
        }
    }

    public void removeEdge(int edgeID) {
        Edge edge = getEdgeByID(edgeID);
        Vertex sv = getVertexByID(edge.sourceID), dv = getVertexByID(edge.destinationID);
        sv.outDegree--;
        sv.outEdge.remove((Integer) edgeID);
        dv.inDegree--;
        dv.inEdge.remove((Integer) edgeID);
    }

    public void restoreEdge(int edgeID) {
        Edge edge = getEdgeByID(edgeID);
        Vertex sv = getVertexByID(edge.sourceID), dv = getVertexByID(edge.destinationID);
        if (!sv.outEdge.contains(edgeID)) {
            sv.outDegree++;
            sv.outEdge.add(edgeID);
        }
        if (!dv.inEdge.contains(edgeID)) {
            dv.inDegree++;
            dv.inEdge.add(edgeID);
        }
    }

    public void removeFromDemandList(int vid) {
        demandList.remove((Integer) vid);
    }

    public void restoreToDemandList(int vid) {
        if (!demandList.contains(vid)) {
            demandList.add(vid);
        }
    }

    public Graph(String topo, String demand) {

        int linkID, oldSourceID, sourceID, oldDestinationID, destinationID, cost;

        //处理topo
        String[] lines = topo.split("\\n"), tmp;
        for (String line : lines) {
            tmp = line.split(",");
            linkID = Integer.valueOf(tmp[0]);
            oldSourceID = Integer.valueOf(tmp[1]);
            sourceID = vertexMap.containsKey(oldSourceID) ? vertexMap.get(oldSourceID) : addVertex(oldSourceID);
            oldDestinationID = Integer.valueOf(tmp[2]);
            destinationID = vertexMap.containsKey(oldDestinationID) ? vertexMap.get(oldDestinationID) : addVertex(oldDestinationID);
            cost = Integer.valueOf(tmp[3]);

            addEdge(linkID, sourceID, destinationID, cost);
        }

        //处理demand
        tmp = demand.split("\\n")[0].split(",");
        srcID = vertexMap.get(Integer.valueOf(tmp[0]));
        vertexList.get(srcID).mustPass = true;
        dstID = vertexMap.get(Integer.valueOf(tmp[1]));
        vertexList.get(dstID).mustPass = true;

        int id;
        tmp = tmp[2].split("\\|");
        for (String old : tmp) {
            id = vertexMap.get(Integer.valueOf(old));
            demandList.add(id);
            vertexList.get(id).mustPass = true;
        }
    }
}
