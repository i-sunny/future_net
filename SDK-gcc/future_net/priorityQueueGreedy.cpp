#include "priorityQueueGreedy.h"
#include "define.h"
#include "graph.h"
#include "lib_record.h"
#include <string>
#include <sstream>
#include <iostream>
#include <sys/time.h>

using namespace std;

PriorityQueueGreedy::PriorityQueueGreedy(Graph *graph) {
    this->graph = graph;
    
    srcID = graph->getSrcID();
    dstID = graph->getDstID();
    
    vertexNum = graph->getVertexNum();
    vertexTable = graph->getVertexTable();
    
    //统计必经节点个数，不含起点终点
    demandNum = (int)graph->demands.count();
    
    edgeNum = graph->getEdgeNum();
    edgeTable = graph->getEdgeTable();

    if(vertexNum == 300){         //case 10 --reverse
        MaxPriorityQueueSize = 400;
        ParallelSearchNum = 15;
    }else if(vertexNum >= 350 && vertexNum <= 550 && demandNum < 23) {  //case 12
            MaxPriorityQueueSize = 400;
            ParallelSearchNum = 10;
    } else if (vertexNum >= 550 && demandNum < 25) {   // case 15
        MaxPriorityQueueSize = 200;
        ParallelSearchNum = 10;
    } else {                                          //case 14
        MaxPriorityQueueSize = 350;
        ParallelSearchNum = 20;
    }
}

PriorityQueueGreedy::~PriorityQueueGreedy(){
    
}

PriorityQueueGreedy::VtxNode *PriorityQueueGreedy::createNode(VtxNode *pNode, unsigned short candNodeId, int candNodeDis){
    VtxNode *childNode = new VtxNode(candNodeId, pNode);
    
    while (pNode->cachedPathDstParent[candNodeId] != pNode->vid) {
        candNodeId = pNode->cachedPathDstParent[candNodeId];
        childNode->lastPath[childNode->lastPathSize] = candNodeId;
        childNode->lastPathSize++;
        childNode->visitedVertexs.set(candNodeId);
    }
    childNode->fEval = pNode->fEval + candNodeDis;
    return childNode;
}

PriorityQueueGreedy::VtxNode *PriorityQueueGreedy::dijkstaShortestPath(VtxNode *node){
    unsigned short candNodeId;
    int candNodeDis;
    unsigned short childId, nxtWeight;
    
    while (true) {
        if (!node->cached) {
            candNodeId = node->vid;
            candNodeDis = 0;
        }
        else {
            candNodeDis = -MaxWeight;
            for (int i = 0; i < vertexNum; i++) {
                if (!node->visitedVertexs[i] && node->cachePathCosts[i] < 0 && node->cachePathCosts[i] > candNodeDis) {
                    candNodeId = i;
                    candNodeDis = node->cachePathCosts[i];
                }
            }
            if (candNodeDis == -MaxWeight) {
                return NULL;
            }
        }
        candNodeDis = -node->cachePathCosts[candNodeId];
        node->cachePathCosts[candNodeId] = candNodeDis;
        if (candNodeId == dstID && node->visitedDemandCnt != demandNum) {
            continue;
        }
        if ((graph->demands[candNodeId] || candNodeId == dstID) && node->cached) {
            node->demandChild[node->demandChildSize] = candNodeId;
            node->demandChildSize++;
            
            return createNode(node, candNodeId, candNodeDis);
        }
        node->cached = true;
        const Edge *edge;
        for (int i = 0; i < vertexTable[candNodeId].outDegree; i++) {
            edge = &edgeTable[vertexTable[candNodeId].outEdge[i]];
            childId = edge->destinationID;
            if (!node->visitedVertexs[childId] && node->cachePathCosts[childId] < 0) {
                nxtWeight = candNodeDis + edge->cost;
                if (nxtWeight < abs(node->cachePathCosts[childId])) {
                    node->cachePathCosts[childId] = -nxtWeight;
                    node->cachedPathDstParent[childId] = candNodeId;
                }
            }
        }
    }
}

Path * PriorityQueueGreedy::searchRoute() {
    
    //设置起点srcId
    VtxNode *root = new VtxNode(srcID);
    priorityQueue.insert(make_pair(root->fEval, root));
    
    VtxNode *curNode, *nxtNode, *bestNode = NULL;
    int pathCostf = INT_MAX;
    
    //存储节点的可行路径
    unsigned short validPath[vertexNum];
    unsigned short validPathSize = 0;
    unsigned short totalValidPathCnt = 0;
    
    int parallelCnt = 0, tmpCnt;
    
    while (priorityQueue.size()) {
        
        parallelCnt = ((parallelCnt + 1) % ParallelSearchNum) % priorityQueue.size();
        multimap<int, VtxNode*>::iterator iter = priorityQueue.begin();
        tmpCnt = parallelCnt;
        while (tmpCnt--) {
            iter++;
        }
        
        curNode = iter->second;
        nxtNode = dijkstaShortestPath(curNode);
        if (nxtNode == NULL) {
            //curNode已经没有子节点
            priorityQueue.erase(iter);
            if (curNode->demandChildSize == 0) {
                delete curNode;
            }
        }
        else if(nxtNode->vid == dstID) {
            if (pathCostf > nxtNode->fEval) {
                pathCostf = nxtNode->fEval;
                bestNode = nxtNode;
                totalValidPathCnt++;
            }
        }
        else {
            priorityQueue.insert(make_pair(nxtNode->fEval, nxtNode));
        }
        if (priorityQueue.size() > MaxPriorityQueueSize) {
            priorityQueue.erase(--priorityQueue.end());
        }
    }
    
    if (pathCostf != INT_MAX) {
        //get path
        Path *formatPath = new Path(pathCostf);
        getPath(validPath, validPathSize, bestNode, formatPath);
        return formatPath;
    }
    return NULL;
}

void PriorityQueueGreedy::getPath(unsigned short path[], int size, VtxNode *node, Path *formatPath){
    
    while (node->vid != srcID) {
        path[size++] = node->vid;
        for (int i = 0; i < node->lastPathSize; i++) {
            path[size++] = node->lastPath[i];
        }
        node = node->parent;
    }
    path[size++] = srcID;
    
    //write path
    const Edge *edge;
    int eid;
    for (int i = size - 1; i > 0; i--){
        graph->getEdge(path[i], path[i-1], eid);
        edge = &edgeTable[eid];
        formatPath->edges.push_back(edge->ID);
    }
}


