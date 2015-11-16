#pragma once

#include <cmath>

struct vector2
{
public:
	float x;
	float y;

	vector2()
	{ // constructor
		this->x = 0;
		this->y = 0;
	}
	vector2(float x, float y)
	{ // constructor
		this->x = x;
		this->y = y;
	}

	float sqr_magnitude()
	{
		return x * x + y * y;
	}
	float magnitude()
	{
		return sqrt(sqr_magnitude());
	}

	#define zero vector2(0.0f, 0.0f);

	static float sqr_distance(const vector2 a, const vector2 b)
	{
		vector2 temp = a - b;
		return temp.sqr_magnitude();
	}
	static float distance(const vector2 a, const vector2 b)
	{
		return sqrt(sqr_distance(a, b));
	}

	vector2& operator += (const vector2& a)
	{
		(*this).x += a.x;
		(*this).y += a.y;
		return *this;
	}
	vector2& operator -= (const vector2& a)
	{
		(*this).x -= a.x;
		(*this).y -= a.y;
		return *this;
	}
	vector2& operator *= (const float& a)
	{
		(*this).x *= a;
		(*this).y *= a;
		return *this;
	}
	vector2& operator /= (const float& a)
	{
		(*this).x /= a;
		(*this).y /= a;
		return *this;
	}

	friend vector2 operator + (vector2 a, const vector2& b)
	{
		a += b;
		return a;
	}
	friend vector2 operator - (vector2 a, const vector2& b)
	{
		a -= b;
		return a;
	}
	friend vector2 operator * (vector2 a, const float& b)
	{
		a *= b;
		return a;
	}
	friend vector2 operator / (vector2 a, const float& b)
	{
		a /= b;
		return a;
	}
};