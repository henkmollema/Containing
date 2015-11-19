#pragma once

#include<vector>
#include<algorithm>

#include"node.h"

using namespace std;

class road_map
{
private:
	vector<node*> m_nodes;
	void reset_nodes();
	vector<node> get_copy();
	bool node_is_blocked(int);

public:
	struct node_base
	{
	public:
		vector2 position;
		vector<int> connections;

		node_base(vector2 position, vector<int> connections)
		{
			this->position = position;
			this->connections = connections;
		}
	};
        
        road_map();
	road_map(vector<node_base>);
	~road_map();
        
        int size();
	vector<int> get_path(node*, node*, float);
	vector<int> get_path(int, int, float);
};

