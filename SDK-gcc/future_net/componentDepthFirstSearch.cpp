#include "componentDepthFirstSearch.h"
#include "stronglyConnectedComponents.h"

ComponentDepthFirstSearch::ComponentDepthFirstSearch(Graph *graph) {
    this->graph = graph;
    vertexTable = graph->getVertexTable();
    edgeTable = graph->getEdgeTable();
    vertexNum = graph->getVertexNum();
    
    preVisit = new int[vertexNum];
    preVisitSize = vertexNum;
    
    postVisit = new int[vertexNum];
    postVisitSize = vertexNum;
    
}

void ComponentDepthFirstSearch::clear() {
    visited.reset();
    componented.reset();

//    memset(preVisit, 0, vertexNum * sizeof(int));
//    memset(postVisit, 0, vertexNum * sizeof(int));
    visitCnt = 0;
    components = (new StronglyConnectedComponents(graph))->scc();
}

int* ComponentDepthFirstSearch::dfs() {
    clear();
    for (int vid = 0; vid < vertexNum; vid++) {
        if (!visited[vid])
            explore(vid);
    }
    return postVisit;
}

//只遍历startId作为起点的路径
int* ComponentDepthFirstSearch::dfs(int startId) {
    clear();
    explore(startId);
    return postVisit;
}

//if have a path from src to dst
bool ComponentDepthFirstSearch::isReachable(int from, int to) {
    clear();
    explore(from);
    return postVisit[to];
}

void ComponentDepthFirstSearch::explore(int vid) {
    visited.set(vid);
    //set componented to all vertices in same component
    int ci = -1;    //component index
    if (!componented[vid]) {
        std::set<int> comp;
        for (ci = 0; ci < components->size(); ci++) {
            comp = (*components)[ci];
            if (comp.find(vid) != comp.end()) {
                std::set<int>::iterator it;
                for (it = comp.begin(); it != comp.end(); it++) {
                    componented.set(*it);
                }
                break;
            }
        }
        preVisit[vid] = visitCnt++;
    }
    
    int edgeID;
    for (int i = 0; i < vertexTable[vid].outEdge.size(); i++) {
        edgeID = vertexTable[vid].outEdge[i];
        int uid = edgeTable[edgeID].destinationID;
        if (!visited[uid])
            explore(uid);
    }
    
    if (ci != -1) {
        postVisit[vid] = visitCnt++;
        std::set<int>::iterator it;
        for (it = (*components)[ci].begin(); it != (*components)[ci].end(); it++) {
            postVisit[*it] = postVisit[vid];
        }
    }
}

void ComponentDepthFirstSearch::test(Graph *graph) {
    ComponentDepthFirstSearch *cdfs = new ComponentDepthFirstSearch(graph);
    cdfs->dfs(graph->getSrcID());
    for (int i = 0; i < cdfs->vertexNum; i++) {
        cout << cdfs->postVisit[i] << " ";
    }
    cout << endl;
}


