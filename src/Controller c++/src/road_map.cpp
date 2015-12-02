#include <limits>
#include <iostream>

#include "road_map.h"
#define HIGH_NUMBER 1000.0f
#define G_MULTIPLIER 10.0f


void road_map::reset_nodes()
{
}

vector<node> road_map::get_copy()
{
    vector<node> temp_nodes{ vector<node>(0) };
    for (unsigned int i{ 0 }; i < m_nodes.size(); ++i)
    {
        int id{ m_nodes[i]->id() };
        vector2 pos{ m_nodes[i]->get_position() };
        temp_nodes.push_back(node(id, pos));
    }
    for (unsigned int i{ 0 }; i < m_nodes.size(); ++i)
    {
        int tester = 0;
        // falls flat on its face at the fifth call to this
        int test = m_nodes[i]->get_connections().size();
        for (unsigned int j{ 0 }; j < test; ++j)
        {
            tester = m_nodes[i]->get_connections(i, j)[j]->id();
            temp_nodes[i].add_connection(&temp_nodes[tester]);
        }
    }

    return temp_nodes;
}

bool road_map::node_is_blocked(int i)
{
    return false;
}

road_map::road_map()
{
    
}

road_map::size()
{
    return m_nodes.size();
}

road_map::road_map(vector<node_base> n)
{
    m_nodes = vector<node*>(0);
    for (unsigned int i{ 0 }; i < n.size(); ++i)
    { // set nodes
        m_nodes.push_back(new node(i, n[i].position));
    }
    for (unsigned int i{ 0 }; i < n.size(); ++i)
    {
        for (unsigned int j{ 0 }; j < n[i].connections.size(); ++j)
        { // set connections
            m_nodes[i]->add_connection(m_nodes[n[i].connections[j]]);
        }
    }
}

road_map::~road_map()
{
    for (vector<node*>::iterator it{ m_nodes.begin() }; it != m_nodes.end(); ++it) { delete (*it); }
}

vector<int> road_map::get_path(node* from, node* to, float speed)
{ // https://docs.google.com/presentation/d/1UPEAdPqTFxVzcrofAqkVdkAGtbOZBEWF_SMrrcEyVxs/edit?usp=sharing
	// nodes
    vector<node > nodes       { get_copy()       };
    vector<node*> open_list   { vector<node*>(0) };
    vector<node*> closed_list { vector<node*>(0) };
    
    // clone target and from
    node* current{ &nodes[from->id()] };

    while (1)
    {

        // move to visited
        open_list.erase(std::remove(open_list.begin(), open_list.end(), current), open_list.end());
        closed_list.push_back(current);

        if (current->id() == to->id())
        { // Path completed
            vector<int> path{ vector<int>(0) };
            while (current != NULL)
            { // set path
                path.push_back(current->id());
                if (current->id() == from->id()) break;
                current = current->parent;
            }
            //delete selected;
            //delete target;
            //delete current;
            //for (vector<node*>::iterator it{ open_list.begin() }; it != open_list.end(); ++it) { delete (*it); }
            //for (vector<node*>::iterator it{ closed_list.begin() }; it != closed_list.end(); ++it) { delete (*it); }
            return path;
        }

        for (unsigned int i{ 0 }; i < current->get_connections().size(); ++i)
        { // expand to neighbours
            node* selected{ current->get_connections()[i] };

            // the new value the neighour gets
            float new_value{ current->value + vector2::distance(current->get_position(), selected->get_position()) };

            if (selected->value <= 0.0f /*not visited*/|| selected->value >= new_value /*path longer than current*/)
            { // set new value and parent
                selected->parent = current;
                selected->value = new_value;

                if (find(open_list.begin(), open_list.end(), selected) == open_list.end())
                { // neighbour not yet in open list so add it to the list
                    open_list.push_back(selected);
                }
            }
            //delete selected;
        }

        if (open_list.size() < 1)
        { // everything visited, no path
            //delete target;
            //delete current;
            //for (vector<node*>::iterator it{ open_list.begin() }; it != open_list.end(); ++it) { delete (*it); }
            //for (vector<node*>::iterator it{ closed_list.begin() }; it != closed_list.end(); ++it) { delete (*it); }

            return vector<int>(0);
        }

        // get from open list the lowest
        unsigned int lowest_i{ 0 };
        float lowest_v{ numeric_limits<float>().max() };
        for (unsigned int i{ lowest_i }; i < open_list.size(); ++i)
        {
            float temp_value{ open_list[i]->value + (G_MULTIPLIER * vector2::distance(open_list[i]->get_position(), to->get_position())) };
            if (temp_value < lowest_v)
            {
                lowest_v = temp_value;
                lowest_i = i;
            }
        }

        current = open_list[lowest_i];
    }
}

vector<int> road_map::get_path(int from, int to, float speed)
{
    return get_path(m_nodes[from], m_nodes[to], speed);
}
