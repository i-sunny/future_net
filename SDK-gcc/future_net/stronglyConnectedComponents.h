#ifndef stronglyConnectedComponents_h
#define stronglyConnectedComponents_h

#include <stdio.h>
#include <stack>
#include <set>
#include "graph.h"
#include "define.h"

class StronglyConnectedComponents {
public:
    StronglyConnectedComponents(Graph *graph);
    ~StronglyConnectedComponents();
    
    vector<set<int> > *scc();
    static void test(Graph *graph);
    
private:
    Graph *graph;
    const Vertex *vertexTable;
    const Edge *edgeTable;
    int vertexNum;
    
    std::bitset<MAX_VERTEX_NUM> visited;
    std::stack<int> stack;
    int visitCnt;
    
    int *preVisit;
    int preVisitSize;
    
    vector<std::set<int> > components;
    
    //function
    void clear();
    void dfs(int vid);
};
#endif /* stronglyConnectedComponents_h */
