#include "graph.h"

Graph::Graph(char ** topo, int edge_num, char *demand, int demand_num) {
    vertexNum = 0;
    edgeNum = 0;
    
    char line[MAX_LINE_LEN];
    int linkID, sourceID, destinationID, cost, oldID;
    int data_loc=0; // 数据域，0记录linkID，1记录sourceID，以此类推记录四个数据
    char *pch;
    for(int i=0; i<edge_num; i++) {
        strcpy(line, topo[i]);
        pch = strtok(line,",");
        while(pch != NULL)
        {
            switch(data_loc) {
                case 0:
                    linkID = atoi(pch);
                    break;
                case 1:
                    oldID = atoi(pch);
                    if(vertexMap.count(oldID)) 
                        sourceID = vertexMap[oldID];
                    else
                        sourceID = addVertex(oldID);
                    break;
                case 2:
                    oldID = atoi(pch);
                    if(vertexMap.count(oldID))
                        destinationID = vertexMap[oldID];
                    else
                        destinationID = addVertex(oldID);
                    break;
                default:
                    cost = atoi(pch);
            }
			data_loc++;
            pch = strtok(NULL, ",");
        }
        data_loc = 0;
        //cout << "Graph: addEdge " << linkID << '\t' << sourceID << '\t' << destinationID << '\t' << cost<< endl; ///test
		addEdge(linkID, sourceID, destinationID, cost);
	}
   
    strcpy(line, demand);
    pch = strtok(line, ",|");
    while(pch != NULL) {
        switch(data_loc) {
            case 0:
                oldID = atoi(pch);
				srcID = vertexMap[oldID];
                break;
            case 1:
                oldID = atoi(pch);
                dstID = vertexMap[oldID];
                break;
            default:
                oldID = atoi(pch);
                vertexTable[vertexMap[oldID]].mustpass = 1;
                demands.set(vertexMap[oldID]);
                demandList.push_back(vertexMap[oldID]);      //only for preHandle()
        }
		data_loc++;
        pch = strtok(NULL, ",|");
    }
    
    preHandle();
    
/*
    disp(); /// test
    int edgeID=-1; /// test
    cout << getEdge(2,3,edgeID) << endl; /// test
    cout << edgeID << endl; /// test
    addEdge(7,2,1,1); ///test
    cout << getEdge(2,1,edgeID) << endl; /// test
    cout << edgeID << endl; /// test
    disp(); ///test
    cout << getSrcID() << '\t' << getDstID() << '\t' << getVertexNum() << '\t' << getEdgeNum() << '\t' << endl; /// test
    cout << (getVertexTable()+1)->oldID << '\t' << (getEdgeTable()+2)->oldID << endl; /// test */
}

bool Graph::getEdge(int sourceID, int destinationID, int &ID) {
	if(sourceID >= vertexNum || destinationID >= vertexNum)
		return 0;
	vector<int> *outEdges = &(vertexTable[sourceID].outEdge);
	for(vector<int>::iterator it=outEdges->begin(); it!=outEdges->end(); it++) {
		if(edgeTable[*it].destinationID == destinationID) {
			ID = *it;
            //cout << "getEdge: ID " << ID << endl; ///test
			return 1;
		}
	}
	return 0;
}

int Graph::addVertex(int oldID) {
	vertexMap.insert(pair<int, int>(oldID, vertexNum));
	vertexTable[vertexNum].oldID = oldID;
    vertexTable[vertexNum].ID = vertexNum;
	return vertexNum++;
}

int Graph::addEdge(int oldID, int sourceID, int destinationID, int cost) {
	int ID;
	if(getEdge(sourceID, destinationID, ID)) {
		if(cost < edgeTable[ID].cost) {
            edgeMap.erase(edgeTable[ID].oldID);
            edgeMap.insert(pair<int, int>(oldID, ID));
            edgeTable[ID].oldID = oldID;
            edgeTable[ID].cost = cost;
		}
		return ID;
	} else {
		edgeMap.insert(pair<int, int>(oldID, edgeNum));
		edgeTable[edgeNum].oldID = oldID;
        edgeTable[edgeNum].ID = edgeNum;
        edgeTable[edgeNum].cost = cost;
        edgeTable[edgeNum].sourceID = sourceID;
        edgeTable[edgeNum].destinationID = destinationID;
		vertexTable[sourceID].outDegree++;
		vertexTable[sourceID].outEdge.push_back(edgeNum);
		vertexTable[destinationID].inDegree++;
		vertexTable[destinationID].inEdge.push_back(edgeNum);
		return edgeNum++;
	}
}

void Graph::reverseGraph() {
    int tmp;
    vector<int> tmp2;

    tmp = srcID;
    srcID = dstID;
    dstID = tmp;
    
    for (int i = 0; i < vertexNum; i++) {
        tmp = vertexTable[i].inDegree;
        vertexTable[i].inDegree = vertexTable[i].outDegree;
        vertexTable[i].outDegree = tmp;
        
        
        tmp2 = vertexTable[i].inEdge;
        vertexTable[i].inEdge = vertexTable[i].outEdge;
        vertexTable[i].outEdge = tmp2;
    }
    
    for (int i = 0; i < edgeNum; i++) {
        tmp = edgeTable[i].sourceID;
        edgeTable[i].sourceID = edgeTable[i].destinationID;
        edgeTable[i].destinationID = tmp;
    }
}

bool Graph::preHandle(){
	Edge *edge = NULL;
    
    //delete srcID inEdges
	Vertex *srcVtx = &vertexTable[srcID];
    for(int i = 0; i < srcVtx->inDegree; i++){
        edge = &edgeTable[srcVtx->inEdge[i]];
        edge->valid = false;
    }
    
	//delete dstID outEdges
	Vertex *dstVtx = &vertexTable[dstID];
    for (int i = 0; i < dstVtx->outDegree; i++) {
        edge = &edgeTable[dstVtx->outEdge[i]];
        edge->valid = false;
    }
    
	// 1) v in V' if v.inDegree == 1 (only one path from u->v(u in V))
    // then delete u's outEdges remain only u->v outEdge
    // 2) v in V' if v.outDegree == 1 (only one path from v->u(u in V))
    // then delete u's inEdges remain only v->u inEdge
//	Vertex *u, *v;
//	bool finished = false;
//	int di = 0;
//	while(true){
//		finished = true;
//		int vid = demandList[di];
//		v = &vertexTable[vid];
//        int eid;
//        
//		if(vertexValidInDegree(v, eid)== 1){
//            finished = false;
//            u = &vertexTable[edgeTable[eid].sourceID];
//            for (int j = 0; j < u->outDegree; j++) {
//                edge = &edgeTable[u->outEdge[j]];
//                edge->valid = false;
//            }
//            edge = &edgeTable[eid];
//            edge->valid = true;
//		}
//        
//		if(vertexValidOutDegree(v, eid) == 1){
//            finished = false;
//            u = &vertexTable[edgeTable[eid].destinationID];
//            for (int j = 0; j < u->inDegree; j++) {
//                edge = &edgeTable[u->inEdge[j]];
//                edge->valid = false;
//            }
//            edge = &edgeTable[eid];
//            edge->valid = true;
//		}
//        
//		if(++di == demandList.size()){
//			if(finished) break;
//			di = 0;
//		}
//	}
//
	// delete u in V(except src and dst) which:
	// u.inDegree = 0 || u.outDegree = 0
//	int uid = 0;
//	while(true) {
//		finished = true;
//		if(uid == srcID || uid == dstID) {
//			uid++;
//			continue;
//		}
//		u = &vertexTable[uid];
//        int eid;
//		if((vertexValidInDegree(u, eid) == 0 && vertexValidOutDegree(u, eid) != 0) ||
//           (vertexValidOutDegree(u, eid) == 0 && vertexValidInDegree(u, eid) != 0))
//        {
//			finished = false;
//            for (int j = 0; j < u->inDegree; j++) {
//                edge = &edgeTable[u->inEdge[j]];
//                edge->valid = false;
//            }
//            for (int j = 0; j < u->outDegree; j++) {
//                edge = &edgeTable[u->outEdge[j]];
//                edge->valid = false;
//            }
//		}
//		if(++uid == vertexNum){
//			if(finished) break;
//			uid = 0;
//		}
//	}
	return true;
}

int Graph::vertexValidInDegree(Vertex *v, int &firstValidEid) {
    Edge *edge;
    unsigned short cnt = 0;
    for (int i = 0; i < v->inDegree; i++) {
        edge = &edgeTable[v->inEdge[i]];
        if (edge->valid) {
            if (cnt == 0) {
                firstValidEid = edge->ID;
            }
            cnt++;
        }
    }
    return cnt;
}

int Graph::vertexValidOutDegree(Vertex *v, int &firstValidEid) {
    Edge *edge;
    unsigned short cnt = 0;
    for (int i = 0; i < v->outDegree; i++) {
        edge = &edgeTable[v->outEdge[i]];
        if (edge->valid) {
            if (cnt == 0) {
                firstValidEid = edge->ID;
            }
            cnt++;
        }
    }
    return cnt;
}

