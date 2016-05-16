#ifndef componentDepthFirstSearch_h
#define componentDepthFirstSearch_h

#include <stdio.h>
#include "set"
#include "graph.h"

class ComponentDepthFirstSearch {
public:
    ComponentDepthFirstSearch(Graph *graph);
    ~ComponentDepthFirstSearch();
    
    static void test(Graph *graph);
    
private:
    Graph *graph;
    const Vertex *vertexTable;
    const Edge *edgeTable;
    int vertexNum;
    
    std::bitset<MAX_VERTEX_NUM> visited;
    std::bitset<MAX_VERTEX_NUM> componented;
    
    vector<std::set<int> > *components;
    
    int visitCnt;
    
    int *preVisit;
    int preVisitSize;
    
    int *postVisit;
    int postVisitSize;
    
    //function
    void clear();
    int* dfs();
    int* dfs(int startId);
    void explore(int vid);
    bool isReachable(int from, int to);
    
    
};

#endif /* componentDepthFirstSearch_h */
