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
	node(int, vector2);
	~node();

	node* parent;
	float value;

	int id();
	vector2 get_position();
	void add_connection(node*);
        vector<node*> get_connections();
        void set_occupied_at(float, bool);
	bool is_occupied_at(float);
};

