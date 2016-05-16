#ifndef _BELLMANFORD_H
#define _BELLMANFORD_H

#include "define.h"
#include "graph.h"
#include "pathManager.h"
#include <vector>
#include <set>

class BellmanFord
{
public:
    //每个节点最多保留的路径数
    int maxPaths = 50;
    //到达必经节点的边的权值惩罚值，需大于所有边权重之和
    const int penaltyWeight = MAX_WEIGHT;

    BellmanFord(Graph *graph);
    ~BellmanFord();
    Path *searchRoute();
    
private:
    class VertexInfo
    {
    public:
        int maxWeight;
        int minWeight;
        std::vector<int> paths;

        VertexInfo(int initSize = 10);
    };

    Graph *graph;
    PathManager *pathManager;

    int srcID;
    int dstID;

    int vertexNum;
    const Vertex *vertexTable;
    //比经节点数，不包括起点终点
    int mustPassNum;

    int edgeNum;
    const Edge *edgeTable;
    //减去penaltyWeight后的新权重
    int *edgeWeights;

    int route[MAX_VERTEX_NUM];
    int routeLength;

    VertexInfo *vertexInfos;

    BellmanFord(const BellmanFord &bellmanFord) {}

    //更新从一个顶点到另一个顶点的路径
    //返回是否有路径更新
    bool updatePaths(VertexInfo *vertexInfos, VertexInfo *newVertexInfos, int srcID, int edgeID);
    void afterEffect(Path *path);
};



#endif
