#include "math.h"
#include "stdio.h"
#include "Aria.h"
#include "list"

using namespace std;

struct Ponto
{
	double x, y;
};

struct Node
{
	double h, g, f;
	Ponto p;
};

float distancia(Ponto *p1, Ponto *p2)
{
	/* RIP Arauto */
	float res = sqrt(pow((p1->x - p2->x), 2) + pow((p1y - p2y), 2));
	return res;
}

void pathFinder()
{
	
}

int main()
{	
	Ponto chegada;
	
	return 0;
}