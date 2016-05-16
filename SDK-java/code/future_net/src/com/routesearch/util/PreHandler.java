package com.routesearch.util;

import com.routesearch.algorithm.RouteValidChecker;
import com.routesearch.model.Graph;
import com.routesearch.model.Vertex;

/**
 * Created by sunny on 16/3/17.
 */
public class PreHandler {

    //graph pre-handle
    public static boolean phr(Graph graph) {

        // delete srcID inEdges
        int srcId = graph.getSrcID();
        Vertex srcVtx = graph.getVertexByID(srcId);
        while (srcVtx.inDegree != 0) graph.removeEdge(srcVtx.inEdge.get(0));

        // delete dstID outEdges
        int dstId = graph.getDstID();
        Vertex dstVtx = graph.getVertexByID(dstId);
        while (dstVtx.outDegree != 0) graph.removeEdge(dstVtx.outEdge.get(0));

        // 1) v in V' if v.inDegree == 1 (only one path from u->v(u in V))
        // then delete u's outEdges remain only u->v outEdge
        // 2) v in V' if v.outDegree == 1 (only one path from v->u(u in V))
        // then delete u's inEdges remain only v->u inEdge
        Vertex u, v;
        boolean finished;
        int i = 0;
        while (true) {
            finished = true;
            int vid = graph.getDemandList().get(i);
            v = graph.getVertexByID(vid);
            if (v.inDegree == 1) {
                finished = false;
                int eid = v.inEdge.get(0);
                u = graph.getVertexByID(graph.getEdgeByID(eid).sourceID);
                while (u.outDegree != 0) graph.removeEdge(u.outEdge.get(0));
                graph.restoreEdge(eid);
            }
            if (v.outDegree == 1) {
                finished = false;
                int eid = v.outEdge.get(0);
                u = graph.getVertexByID(graph.getEdgeByID(eid).destinationID);
                while (u.inDegree != 0) graph.removeEdge(u.inEdge.get(0));
                graph.restoreEdge(eid);
            }

            if (++i == graph.getDemandList().size()) {
                if (finished) break;
                i = 0;
            }
        }

        // delete u in V(except src and dst) which:
        // u.inDegree = 0 || u.outDegree = 0
        int uid = 0;
        while (true) {
            finished = true;
            if (uid == srcId || uid == dstId){
                uid++;
                continue;
            }
            u = graph.getVertexByID(uid);
            if ((u.inDegree == 0 && u.outDegree != 0 )|| (u.outDegree == 0 && u.inDegree != 0)) {
                if (graph.getDemandList().contains(uid)) {
                    System.out.println("failure in preHandle()");
                    return false;
                } else {
                    finished = false;
                    while (u.inDegree != 0) graph.removeEdge(u.inEdge.get(0));
                    while (u.outDegree != 0) graph.removeEdge(u.outEdge.get(0));
                }
            }
            if (++uid == graph.getVertexNum()) {
                if (finished) break;
                else uid = 0;
            }
        }
        return true;
    }
}
