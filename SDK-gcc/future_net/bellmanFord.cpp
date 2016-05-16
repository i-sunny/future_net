#include "bellmanFord.h"
#include "define.h"
#include "graph.h"
#include "lib_record.h"
#include <string>
#include <sstream>
#include <iostream>

using namespace std;

BellmanFord::BellmanFord(Graph *graph)
{
    this->graph = graph;
    
    pathManager = new PathManager(graph);

    srcID = graph->getSrcID();
    dstID = graph->getDstID();
    
     //统计必经节点个数，不含起点终点
    mustPassNum = (int)graph->demands.count();

    vertexNum = graph->getVertexNum();
    vertexTable = graph->getVertexTable();

    edgeNum = graph->getEdgeNum();
    edgeTable = graph->getEdgeTable();

	maxPaths = 50;

    //设置新权重
    edgeWeights = new int[edgeNum];
    for (int i = 0; i < edgeNum; ++i)
    {
        if (edgeTable[i].destinationID == srcID || edgeTable[i].destinationID == dstID)
        {
            edgeWeights[i] = edgeTable[i].cost;
        }
        else if (vertexTable[edgeTable[i].destinationID].mustpass)
            edgeWeights[i] = edgeTable[i].cost - penaltyWeight;
        else
            edgeWeights[i] = edgeTable[i].cost;
    }

    vertexInfos = new VertexInfo[vertexNum];
}

BellmanFord::~BellmanFord()
{
    delete[]edgeWeights;
    delete[]vertexInfos;
}

Path *BellmanFord::searchRoute()
{
    VertexInfo *newVertexInfos = new VertexInfo[vertexNum];

    //初始化起点
    vertexInfos[srcID].maxWeight = 0;
    vertexInfos[srcID].minWeight = 0;
    vertexInfos[srcID].paths.push_back(pathManager->addPath(srcID, 0 ));

    newVertexInfos[srcID].maxWeight = 0;
    newVertexInfos[srcID].minWeight = 0;
    newVertexInfos[srcID].paths.push_back(vertexInfos[srcID].paths[0]);

    bool changed = false;
    for (int i = 0; i < vertexNum - 1; ++i)
    {
        changed = false;
        for (int j = 0; j < edgeNum; ++j)
        {
            if (!edgeTable[j].valid)
                continue;
            
            int edgeSrc = edgeTable[j].sourceID;
            int edgeDst = edgeTable[j].destinationID;
            if (vertexInfos[edgeSrc].maxWeight == INT_MAX)
            {
                continue;
            }
            else if (vertexInfos[edgeSrc].minWeight + edgeWeights[j]< vertexInfos[edgeDst].maxWeight)
            {
                updatePaths(vertexInfos, newVertexInfos, edgeSrc, j);
                changed = true;
            }
        }
        if (!changed)
        {
            break;
        }
        for (int j = 0; j < vertexNum; ++j)
        {
            vertexInfos[j] = newVertexInfos[j];
        }
    }

    delete[]newVertexInfos;

    if (vertexInfos[dstID].minWeight > -penaltyWeight * mustPassNum
        && vertexInfos[dstID].minWeight < -penaltyWeight * (mustPassNum - 1))
    {
        int pathWeight = pathManager->getWeight(vertexInfos[dstID].paths[0]) + penaltyWeight * mustPassNum;
//        cout << "weight = " << pathWeight << "\n";
        
        Path *path = new Path(pathWeight);
        pathManager->getPath(vertexInfos[dstID].paths[0], path);
        return path;
    }
    else
    {
        return NULL;
    }

}

bool BellmanFord::updatePaths(VertexInfo *vertexInfos, VertexInfo *newVertexInfosint, int srcID, int edgeID)
{
    int dstID = edgeTable[edgeID].destinationID;
    VertexInfo *src = &vertexInfos[srcID];
    VertexInfo *dst = &newVertexInfosint[dstID];
    const Edge *edge = &edgeTable[edgeID];

    int count = 0;
    int i = 0;
    int j = 0;

    //设为静态成员以减少构造和析构次数
    static vector<int> newPaths;

    //新增加的路径和原有路径归并排序取前maxPaths个
    bool updated = false;
    while (count < maxPaths)
    {
        int newPathID;

        if (i == src->paths.size() && j == dst->paths.size())
        {
            break;
        }
        else if (i == src->paths.size())
        {
            newPaths.push_back(dst->paths[j]);
            ++j;
            ++count;
        }
        //路径已经过该节点
        else if (pathManager->isPassed(src->paths[i], dstID))
        {
            ++i;
            continue;
        }
        else if (j == dst->paths.size())
        {
            newPathID = pathManager->addNode(src->paths[i], dstID, edgeID, edgeWeights[edgeID], vertexTable[dstID].mustpass);
            newPaths.push_back(newPathID);
            ++i;
            ++count;
            updated = true;
        }
        //如果要添加路径已经存在
        else if (pathManager->isPrefix(src->paths[i], dst->paths[j]))
        {
            ++i;
            continue;
        }
        else if (pathManager->getWeight(src->paths[i]) + edgeWeights[edgeID] < pathManager->getWeight(dst->paths[j]))
        {
            newPathID = pathManager->addNode(src->paths[i], dstID, edgeID, edgeWeights[edgeID], vertexTable[dstID].mustpass);
            newPaths.push_back(newPathID);
            ++i;
            ++count;
            updated = true;
        }
        else
        {
            newPaths.push_back(dst->paths[j]);
            ++j;
            ++count;
        }
    }
    if (updated)
    {
        dst->paths = newPaths;
    }
    dst->minWeight = pathManager->getWeight(newPaths[0]);
    dst->maxWeight = pathManager->getWeight(newPaths.back());

    newPaths.clear();
    return updated;
}

//在解得基础上进行局部调整优化
//void BellmanFord::afterEffect(Path *path) {
//    //相邻两个目标节点的cost中对最大的cost进行优化
//    int maxCost = -1, macCostSrc, maxCostDst;
//    int tmpCost = -1, tmpCostSrc, tmpCostDst;
//    
//    const Edge *edge;
//    const Vertex *v, u;
//    for (int i = 0; i <  path->edges.size(); i++) {
//        edge = &edgeTable[path->edges[i]];
//        v = &vertexTable[edge->sourceID];
//        if (v->mustpass) {
//            tmpCostSrc = v->ID;
//            tmpCost = 0;
//        }
//        u = &vertexTable[edge->destinationID];
//    }
//    
//}

BellmanFord::VertexInfo::VertexInfo(int initSize)
{
    maxWeight = INT_MAX;
    minWeight = INT_MAX;
    paths.reserve(initSize);
}
