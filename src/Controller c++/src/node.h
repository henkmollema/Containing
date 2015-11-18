#pragma once

#include<vector>

#include"vector2.h"

using namespace std;

class node
{
private:
	int m_id;
	vector2 m_position;
	vector<node*> m_connections;

public:
	node();
	node(int id, vector2 position);
	~node();

	node* parent;
	float value;

	int id();
	vector2 get_position();
	void add_connection(node* n);
        vector<node*> get_connections();
	vector<node*> get_connections(int, int);
	bool is_occupied_at(float t);
};

