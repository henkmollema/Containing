#include<iostream>
#include<string>

#include"vector2.h"
#include"road_map.h"
#include"node.h"

using namespace std;


void pause() {
	string __s;
	getline(cin, __s);
}
void print(string s)
{
	cout << s << endl;
}
int main() {

	road_map roadmap = road_map({
		road_map::node_base(vector2(0.0f, 0.0f), { 1, 2 }),
		road_map::node_base(vector2(1.0f, 0.0f), { 0, 2 }),
		road_map::node_base(vector2(1.0f, 1.0f), { 0, 1, 3, 4 }),
		road_map::node_base(vector2(2.0f, 2.0f), { 2, 4 }),
		road_map::node_base(vector2(0.0f, 4.0f), { 2, 3 })
	});


	vector<int> __t = roadmap.get_path(0, 4, 5.0f);
	for (unsigned int i{ 0 }; i < __t.size(); ++i)
	{
		cout << to_string(__t[i]) << endl;
	}

	pause();

	return EXIT_SUCCESS;
}


