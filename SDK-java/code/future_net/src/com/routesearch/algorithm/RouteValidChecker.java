package com.routesearch.algorithm;

import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;

import java.util.*;

/**
 * Created by sunny on 16/3/16.
 */

/**
 * 设判定经过V’所有点的路径是否存在为 RVC={true,false}
 * 1）首先考虑DAG,进行拓扑排序, 如果RVC=true,则V‘中拓扑排序靠前的点(设u, 即V’中 postVisit较大的点)
 * 和V‘中拓扑排序靠后的点v,必定满足u->v存在路径。
 * 2)任意图G都可转换成DAG of StrongConnectedComponents, 在强连通内部点看成一个整体(内部点设为相同postVisit)。
 * 综上, RVC=true的必要条件是, V'中的点按拓扑排序升序后(即dfs(src) postVisit降序),任意相邻两点(u,v)存在u->v路径
 * 注意:对于存在环的图,dfs必须是src作为第一个遍历点
 */

public class RouteValidChecker {

    public static boolean rvc(Graph graph) {
        ComponentDepthFirstSearch cdfs = new ComponentDepthFirstSearch(graph);
        int[] postVisit = cdfs.dfs(graph.getSrcID());

        //由于postVisit取值唯一,因此可以作为key
        //map of [postVisit, vid]
        Map<Integer, Integer> postMap = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer v1, Integer v2) {
                return v2.compareTo(v1);
            }
        });
        for (int vid = 0; vid < postVisit.length; vid++) {
            if (graph.getDemandList().contains(vid)) {
                if (postVisit[vid] <= 0)
                    return false;
                postMap.put(postVisit[vid], vid);
            }
        }
        postMap.put(postVisit[graph.getDstID()], graph.getDstID());

        int from = -1, to;
        Iterator<Integer> iter = postMap.keySet().iterator();

        //graph src->u which u is the most left in Topological Sort
        if (iter.hasNext()) {
            from = graph.getSrcID();
            to = postMap.get(iter.next());
            if (!cdfs.isReachable(from, to))
                return false;
            from = to;
        }

        //demandList Valid test
        while (iter.hasNext()) {
            to = postMap.get(iter.next());
            if (!cdfs.isReachable(from, to))
                return false;
            from = to;
        }

        return true;
    }

    //check the solution for whole graph
    public static boolean isValidSolution(Graph graph, Path path) {
        List<Integer> pathEdges = path.getEdges();
        Set<Integer> vertices = new HashSet<Integer>();
        Set<Integer> demand = new HashSet<Integer>(graph.getDemandList());
        Edge edge;
        int vid;
        int weight = 0;
        for (int i = 0; i < pathEdges.size(); i++) {
            edge = graph.getEdgeByID(pathEdges.get(i));
            weight += edge.cost;
            if (i == 0 && edge.sourceID != graph.getSrcID()) return false;
            vid = edge.destinationID;
            if (!vertices.add(vid)) {
                System.out.println("顶点重复出现: " + vid);
                return false;
            }
            if (i == pathEdges.size() - 1 && vid != graph.getDstID()) return false;
            if (demand.contains(edge.destinationID)) {
                demand.remove(edge.destinationID);
            }
        }
        if (!demand.isEmpty()){
            System.out.println("没有经过V'中点所有点: " + demand);
            return false;
        }
        System.out.println("Weight: " + weight);
        return true;
    }
}
