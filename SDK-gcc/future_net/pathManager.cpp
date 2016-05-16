#include "define.h"
#include "pathManager.h"
#include "lib_record.h"
#include <sstream>

using namespace std;

PathManager::PathManager(Graph *graph, int capacity)
{
    this->graph = graph;
    this->capacity = capacity;
    nodeList = new node[capacity];
    size = 0;
}

PathManager::~PathManager()
{
    delete[] nodeList;
}

int PathManager::addPath(const int vertexID, const int edgeWeight)
{
    if (size == capacity)
    {
        enlarge();
    }
//    nodeList[size].vertexID = vertexID;
    nodeList[size].edgeID = -1;
    nodeList[size].parentID = -1;
    nodeList[size].totalWeight = edgeWeight;
//    nodeList[size].length = 0;
//    nodeList[size].mustPassNum = 0;
    nodeList[size].visitedVertexs.set(vertexID);

    ++size;
    return size - 1;
}

int PathManager::addNode(const int pathID, const int vertexID, const int edgeID, const int edgeWeight, const bool mustPass)
{
    if (size == capacity)
    {
        enlarge();
    }
    nodeList[size].edgeID = edgeID;
//    nodeList[size].vertexID = vertexID;
    nodeList[size].parentID = pathID;
    nodeList[size].totalWeight = nodeList[pathID].totalWeight + edgeWeight;
//    nodeList[size].length = nodeList[pathID].length + 1;
//    nodeList[size].mustPassNum = mustPass ? nodeList[size].mustPassNum + 1 : nodeList[size].mustPassNum;
    nodeList[size].visitedVertexs = nodeList[pathID].visitedVertexs;
    nodeList[size].visitedVertexs.set(vertexID);

    ++size;
    return size - 1;
}

bool PathManager::isPrefix(const int srcPathID, const int dstPathID) const
{
    return nodeList[dstPathID].parentID == srcPathID;
}

bool PathManager::isPassed(const int pathID, const int vertexID) const
{
    //int currentNode = pathID;
    //while (currentNode != -1)
    //{
    //    if (nodeList[currentNode].vertexID == vertexID)
    //    {
    //        return true;
    //    }
    //    else
    //    {
    //        currentNode = nodeList[currentNode].parentID;
    //    }
    //}
    //return false;
    return nodeList[pathID].visitedVertexs[vertexID];
}

bool PathManager::getPath(const int pathID, int *path, int &length) const
{
    int currentNode = pathID;
    length = (int)nodeList[currentNode].visitedVertexs.count() - 1;
    for (int i = length - 1; i >= 0; --i)
    {
        path[i] = nodeList[currentNode].edgeID;
        currentNode = nodeList[currentNode].parentID;
    }
    return true;
}

bool PathManager::getPath(const int pathID, Path *path) const
{
    int currentNode = pathID;
    int length = (int)nodeList[currentNode].visitedVertexs.count() - 1;
    for (int i = 0; i < length; i++)
    {
        path->edges.insert(path->edges.begin(), nodeList[currentNode].edgeID);
        currentNode = nodeList[currentNode].parentID;
    }
    return true;
}

void PathManager::writePath(const int pathID) const
{
    int *path = new int[MAX_VERTEX_NUM];
    int length;

    getPath(pathID, path, length);
    
    const Edge *edge;
    for (int i = 0; i < length; ++i)
    {
        edge = &graph->edgeTable[path[i]];
//        cout << edge->oldID << " ";
        record_result(edge->oldID);
    }
//    cout << endl;
    delete[] path;
}

void PathManager::writePath(Graph *graph, Path *path)
{
    const Edge *edge;
    for (int i = 0; i < path->edges.size(); i++)
    {
        edge = &graph->edgeTable[path->edges[i]];
        cout << edge->oldID << " ";
        record_result(edge->oldID);
    }
    cout << endl;
}

string PathManager::toString(const int pathID) const
{
    int *path = new int[MAX_VERTEX_NUM];
    int length;

    getPath(pathID, path, length);

    stringstream ss;
    ss << "pathID = " << pathID << "\t" << "weight = " << nodeList[pathID].totalWeight << "\t";

    for (int i = 0; i < length; ++i)
    {
        ss << path[i] << ", ";
    }
    ss << "\n";
    return ss.str();
}

int PathManager::getWeight(const int pathID) const
{
    return nodeList[pathID].totalWeight;
}

void PathManager::enlarge()
{
    node *newList = new node[2 * capacity];
    for (int i = 0; i < size; ++i)
    {
        newList[i] = nodeList[i];
    }
    delete[] nodeList;
    nodeList = newList;
    capacity *= 2;
}