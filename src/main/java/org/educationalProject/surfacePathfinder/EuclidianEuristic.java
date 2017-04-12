package org.educationalProject.surfacePathfinder;

import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class EuclidianEuristic<V> implements AStarAdmissibleHeuristic<V>{
	private final static double COST_MULTIPLIER = 25;
	@Override
	public double getCostEstimate(V sourceVertex, V targetVertex) {
		Point a = (Point)sourceVertex;
		Point b = (Point)targetVertex;  
		
		return Math.abs(a.alt-b.alt) * COST_MULTIPLIER +
				Math.sqrt(
					(a.x-b.x)*(a.x-b.x)+
					(a.y-b.y)*(a.y-b.y)+
					(a.alt-b.alt)*(a.alt-b.alt)
				);
	}

}
