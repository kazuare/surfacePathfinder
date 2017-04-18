package org.educationalProject.surfacePathfinder;

import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
/**
* Euclidian euristic for jgrapht. Only works for Point class
*/
public class EuclidianEuristic<V> implements AStarAdmissibleHeuristic<V>{
	@Override
	public double getCostEstimate(V sourceVertex, V targetVertex) {
		Point a = (Point)sourceVertex;
		Point b = (Point)targetVertex;  
		//we must use the same formula as in graph constructor
		return Math.sqrt(
					(a.x-b.x)*(a.x-b.x)+
					(a.y-b.y)*(a.y-b.y)+
					(a.alt-b.alt)*(a.alt-b.alt)
				);
	}

}
