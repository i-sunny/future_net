#ifndef _GRAPH_H
#define _GRAPH_H

#include <iostream>
#include <map>
#include <algorithm>
#include <vector>
#include <cstring>
#include <climits>
#include <bitset>
#include "stdlib.h"
#include "define.h"
using namespace std;

struct Vertex;

struct Edge {
	int oldID;
	int ID;
	int sourceID;
	int destinationID;
	int cost;
    bool valid;
    Edge():valid(true) {}
	Edge(int oid, int id, int sID, int dID, int c):
		oldID(oid), ID(id), sourceID(sID), destinationID(dID), cost(c), valid(true) {}
};

struct Vertex {
	int oldID;
	int ID;
	bool mustpass;
	int inDegree;
	int outDegree;
	vector<int> inEdge; // Edge ID
	vector<int> outEdge; // Edge ID
	Vertex() { inEdge.reserve(8); outEdge.reserve(8); }
	Vertex(int oid, int id, bool mp=0, int idg=0, int odg=0):
		oldID(oid), ID(id), mustpass(mp), inDegree(idg), outDegree(odg) {}
};

struct Path {
    int weight;
    vector<int> edges;
    Path(int w = 0):weight(w){}
};

class Graph {
public:
	Graph(char ** topo, int edge_num, char *demand, int demand_num);
    
    bitset<MAX_VERTEX_NUM> demands;
    map<int, int> vertexMap; // Vertex old ID映射ID，map[VID]复杂度logV, map.count(VID)复杂度logV
    Vertex vertexTable[MAX_VERTEX_NUM]; // 保存Vertex，table[VID]复杂度1
    map<int, int> edgeMap; // Edge old ID映射ID
    Edge edgeTable[MAX_EDGE_NUM]; // 保存Edge，table[EID]复杂度1
    
	int getSrcID() { return srcID; }
	int getDstID() { return dstID; }
	int getVertexNum() { return vertexNum; }
	int getEdgeNum() { return edgeNum; }
	const Vertex* getVertexTable() { return vertexTable; } // 返回不能修改的顶点数组
	const Edge* getEdgeTable() { return edgeTable; } // 返回不能修改的边数组
	bool getEdge(int sourceID, int destinationID, int &ID); // 由顶点找边，1:找到，0:未找到，复杂度1
    void reverseGraph();

private:
	int srcID;
	int dstID;
	int vertexNum;
	int edgeNum;
	vector<int> demandList;

	int addVertex(int oldID); // 添加Vertex old ID映射ID，返回映射后的ID，复杂度1
	int addEdge(int oldID, int sourceID, int destinationID, int cost); // 添加Edge old ID映射ID，复杂度1
    
    bool preHandle();
    int vertexValidInDegree(Vertex *v, int &firstValidEid);
    int vertexValidOutDegree(Vertex *v, int &firstValidEid);
		// 返回映射后的ID
    /*
    void disp() {
        cout << "This is a graph.\n";
        for(int i=0; i<vertexNum; i++) {
            cout << vertexTable[i].oldID << '\t' << vertexTable[i].ID << '\t' << vertexTable[i].mustpass << '\t' << vertexTable[i].inDegree << '\t' << vertexTable[i].outDegree << endl;
            for(vector<int>::iterator it=vertexTable[i].inEdge.begin(); it!=vertexTable[i].inEdge.end(); it++)
                cout << "% ->\t" << edgeTable[*it].sourceID << '\t' << edgeTable[*it].cost << '\t' << edgeTable[*it].oldID << '\t' << edgeTable[*it].ID << endl;
            for(vector<int>::iterator it=vertexTable[i].outEdge.begin(); it!=vertexTable[i].outEdge.end(); it++)
                cout << "-> %\t" << edgeTable[*it].destinationID << '\t' << edgeTable[*it].cost << '\t' << edgeTable[*it].oldID << '\t' << edgeTable[*it].ID << endl;
        }
    } /// test */
};

#endif
