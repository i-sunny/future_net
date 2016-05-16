/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import com.routesearch.model.Edge;
import com.routesearch.model.Graph;
import com.routesearch.model.Path;

public final class Route
{
    /**
     * 你需要完成功能的入口
     * 
     * @author XXX
     * @since 2016-3-4
     * @version V1
     */
    public static String searchRoute(String graphContent, String condition)
    {
        Graph graph = new Graph(graphContent, condition);
        BellmanFordRoute bfr = new BellmanFordRoute(graph);
        Path path = bfr.searchRoute();
        if (path == null){
            return "NA";
        } else {
            Edge edge;
            StringBuffer sb = new StringBuffer();
            for (int eid : path.getEdges()) {
                edge = graph.getEdgeByID(eid);
                sb.append(edge.oldID).append("|");
            }
            sb.replace(sb.length() - 1, sb.length(), "");
            return sb.toString();
        }
    }

}