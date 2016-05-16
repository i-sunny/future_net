#ifndef _PriorityQueueGreedy_H
#define _PriorityQueueGreedy_H

#include <stdio.h>
#include "graph.h"
#include "pathManager.h"

static int vertexNum;
static int demandNum;

class PriorityQueueGreedy {
public:
    PriorityQueueGreedy(Graph *graph);
    ~PriorityQueueGreedy();
    
    Path *searchRoute();
    
private:
    class VtxNode {
    public:
        int vid;
        VtxNode *parent;                              // 上一个demand中的节点
        std::bitset<MAX_VERTEX_NUM> visitedVertexs;   // 路径上路过的所有点
        
        unsigned short *lastPath;                     // parent节点到该节点的路径
        unsigned short lastPathSize;
        
        int *cachePathCosts;                          // 缓存该节点出发的最短路径，避免重复求最短路径
        bool cached;                                  // 是否已经缓存
        unsigned short *cachedPathDstParent;
        
        int fEval;                                    // 评估函数
        int visitedDemandCnt;                         // 访问过的demand中节点的计数
        
        unsigned short *demandChild;
        unsigned short demandChildSize;               // demand child个数
        
        VtxNode(int vid, VtxNode *parent = NULL){
            if (parent != NULL) {
                visitedDemandCnt = parent->visitedDemandCnt + 1;
                visitedVertexs = parent->visitedVertexs;
            } else {
                visitedDemandCnt = 0;
            }
            this->vid = vid;
            visitedVertexs.set(vid);
            this->parent = parent;
            
            lastPath = new unsigned short[vertexNum];
            lastPathSize = 0;
            
            cachePathCosts = new int[vertexNum];
            cached = false;
            
            memset(cachePathCosts, 128, vertexNum * sizeof(int));
            cachePathCosts[vid] = 0;
            
            cachedPathDstParent = new unsigned short[vertexNum];
            
            demandChild = new unsigned short[demandNum];
            demandChildSize = 0;
            
            fEval = 0;
        }
    };
    
    Graph *graph;
    const int MaxWeight = MAX_WEIGHT;
    int MaxPriorityQueueSize = 350;
    int ParallelSearchNum = 20;
    
    int srcID;
    int dstID;
    
    const Vertex *vertexTable;
    multimap<int, VtxNode*> priorityQueue;
    
    int edgeNum;
    const Edge *edgeTable;

    VtxNode *dijkstaShortestPath(VtxNode *node);
    VtxNode *createNode(VtxNode *pNode, unsigned short bestNodeId, int bestNodeDis);
    unsigned long getTimestamp();
    void getPath(unsigned short path[], int size, VtxNode *node, Path *formatPath);

};
#endif /* PriorityQueueGreedy_H */
