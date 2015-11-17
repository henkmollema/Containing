#include <cstddef>
#include <iostream>

#include "node.h"



node::node()
{
    this->m_id = -1;
    this->m_position = zero;
    this->m_connections = vector<node*>();
    this->value = 0.0f;
    this->parent = NULL;
}

node::node(int id, vector2 position)
{
    this->m_id = id;
    this->m_position = position;
    this->m_connections = vector<node*>();
    this->value = 0.0f;
    this->parent = NULL;
}

node::~node()
{
    //for (vector<node*>::iterator it{ m_connections.begin() }; it != m_connections.end(); ++it) { delete (*it); }
    //if (parent != NULL) { delete parent; }
}

int node::id()
{
    return m_id;
}

vector2 node::get_position()
{
    return vector2(m_position.x, m_position.y);
}

void node::add_connection(node* n)
{
    m_connections.push_back(n);
}

vector<node*> node::get_connections()
{
    cout << "currently in node::get::connections()" << endl;
    cout << "m_connections[0] : " << m_connections[0] << endl;
    return this->m_connections; // falls on its face here;
}

vector<node*> node::get_connections(int i, int j)
{
    cout << "currently in node::get::connections(int, int)" << endl;
    cout << "of m_nodes[" << i << "] " << "m_connections[" << j << "] : " << m_connections[0] << endl;
    return this->m_connections; // falls on its face here;
}

bool node::is_occupied_at(float t)
{
    // get all agv
    // check if node is occupied
    return false;
}
