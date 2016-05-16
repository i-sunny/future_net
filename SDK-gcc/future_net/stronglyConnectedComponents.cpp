#include "stronglyConnectedComponents.h"

StronglyConnectedComponents::StronglyConnectedComponents(Graph *graph) {
    this->graph = graph;
    vertexTable = graph->getVertexTable();
    edgeTable = graph->getEdgeTable();
    vertexNum = graph->getVertexNum();
    
    preVisit = new int[vertexNum];
    preVisitSize = vertexNum;
}

void StronglyConnectedComponents::clear() {
    visited.reset();
    while (!stack.empty()) {
        stack.pop();
    }
    visitCnt = 0;
//    memset(preVisit, 0, vertexNum * sizeof(int));
    
    components.clear();
}

vector<set<int> > *StronglyConnectedComponents::scc() {
    clear();
    for (int vid = 0; vid < vertexNum; vid++) {
        if (!visited[vid]) {
            dfs(vid);
        }
    }
    return &components;
}

void StronglyConnectedComponents::dfs(int vid) {
    preVisit[vid] = visitCnt++;
    visited.set(vid);
    stack.push(vid);
    bool isComponentRoot = true;
    
    int uid, eid;
    for (int i = 0; i < vertexTable[vid].outEdge.size(); i++) {
        eid = vertexTable[vid].outEdge[i];
        uid = edgeTable[eid].destinationID;
        if (!visited[uid]) {
            dfs(uid);
        }
        if (preVisit[vid] > preVisit[uid]) {
            preVisit[vid] = preVisit[uid];
            isComponentRoot = false;
        }
    }
    
    if (isComponentRoot) {
        std::set<int> component;
        while (true) {
            int x = stack.top();
            stack.pop();
            component.insert(x);
            preVisit[x] = INT_MAX;
            if (x == vid)
                break;
        }
        components.push_back(component);
    }
}

void StronglyConnectedComponents::test(Graph *graph) {
    
    StronglyConnectedComponents *scc = new StronglyConnectedComponents(graph);
    scc->scc();
    
    for (int i = 0; i < scc->components.size(); i++) {
        set<int>::iterator iter;
        for (iter = scc->components[i].begin(); iter != scc->components[i].end(); iter++) {
            cout << *iter << " ";
        }
        cout << endl;
    }
}

