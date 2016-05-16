#ifndef _PATHMANAGER_H
#define _PATHMANAGER_H

#include <string>

#include "graph.h"

class PathManager
{
public:
    const Graph *graph;
    
    struct node
    {
        int parentID;
        int edgeID;
//        int vertexID;
        int totalWeight;
//        int length;
//        int mustPassNum;
        std::bitset<MAX_VERTEX_NUM> visitedVertexs;
    };
    PathManager(Graph *graph, const int capacity = 4096);
    ~PathManager();

    //创建一条新路径的起点，返回新路径ID
    int addPath(const int vertexID, const int totalWeight = 0);

    //在一条路径末尾增加一条新的边，返回新路径的ID
    int addNode(const int pathID, const int vertexID, const int edgeID, const int edgeWeight, const bool mustPass);

    //判断原路径增加一条边后是否即为目标路径
    bool isPrefix(const int srcPathID, const int dstPathID) const;

    //判断路径中是否经过某个节点
    bool isPassed(const int pathID, const int vertexID) const;

    //根据路径ID获取路径
    bool getPath(const int pathID, int *path, int &length) const;
    bool getPath(const int pathID, Path *path) const;

    //输出路径
    void writePath(const int pathID) const;
    static void writePath(Graph *graph, Path *path);

    //返回路径信息
    std::string toString(const int pathID) const;

    //返回路径权重
    int getWeight(const int pathID) const;
private:
    node *nodeList;
    int capacity;
    int size;

    void enlarge();
    PathManager(const PathManager &pathManager) {}
};

#endif _PATHMANAGER_H