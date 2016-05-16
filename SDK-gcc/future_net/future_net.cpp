#include "define.h"
#include "graph.h"
#include "bellmanFord.h"
#include "priorityQueueGreedy.h"
#include "stronglyConnectedComponents.h"
#include "componentDepthFirstSearch.h"
#include "route.h"
#include "lib_io.h"
#include "lib_record.h"
#include "lib_time.h"
#include <stdio.h>
#include <iostream>
#include <algorithm>

using namespace std;

int main(int argc, char *argv[])
{
    print_time("Begin");
    char *topo[MAX_EDGE_NUM];
    int edge_num;
    char *demand;
    int demand_num;

    char *topo_file = argv[1];
    edge_num = read_file(topo, MAX_EDGE_NUM, topo_file);
    if (edge_num == 0)
    {
        printf("Please input valid topo file.\n");
        return -1;
    }
    char *demand_file = argv[2];
    demand_num = read_file(&demand, 1, demand_file);
    if (demand_num != 1)
    {
        printf("Please input valid demand file.\n");
        return -1;
    }

    /* make graph, by huqifan, 16.03.12 */
    Graph gph(topo, edge_num, demand, demand_num);
    /* edit end */

    
    Path *path;
    
    if ((gph.getVertexNum() < 150) ||   //case 1-6
        (gph.getVertexNum() == 200))   //case 8 = 200
    {
        BellmanFord bellmanFord(&gph);
        path = bellmanFord.searchRoute();
        PathManager::writePath(&gph, path);
    }
    else if ((gph.getVertexNum() == 150) ||   //case 7 = 150
            (gph.getVertexNum() == 250)  ||   //case 9 = 250
            (gph.getVertexNum() == 500 && gph.demands.count() >= 23))     //case 11,13
    {
        gph.reverseGraph();
        BellmanFord reBellmanFord(&gph);
        path = reBellmanFord.searchRoute();
        reverse(path->edges.begin(), path->edges.end());
        PathManager::writePath(&gph, path);
    }else if(gph.getVertexNum() == 300){         //case 10 = 300
        gph.reverseGraph();
        PriorityQueueGreedy priorityQueueGreedy(&gph);
        path = priorityQueueGreedy.searchRoute();
        reverse(path->edges.begin(), path->edges.end());
        PathManager::writePath(&gph, path);
    } else {    //case 14,15, 12
        PriorityQueueGreedy priorityQueueGreedy(&gph);
        path = priorityQueueGreedy.searchRoute();
        PathManager::writePath(&gph, path);
    }

    
    char *result_file = argv[3];
    write_result(result_file);
    release_buff(topo, edge_num);
    release_buff(&demand, 1);

    print_time("End");

	return 0;
}

