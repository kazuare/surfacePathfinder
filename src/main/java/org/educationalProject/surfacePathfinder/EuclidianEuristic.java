package org.educationalProject.surfacePathfinder;

import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class EuclidianEuristic<V> implements AStarAdmissibleHeuristic<V>{
	SimpleWeightedGraph<Point,DefaultWeightedEdge> graph;
	
	public EuclidianEuristic(SimpleWeightedGraph<Point,DefaultWeightedEdge> graph){
		this.graph = graph;
	}
	
	@Override
	public double getCostEstimate(V sourceVertex, V targetVertex) {
		Point a = (Point)sourceVertex;
		Point b = (Point)targetVertex;  
		double xDistance = a.x-b.x;
		double yDistance = a.y-b.y;
		double altDistance = a.alt-b.alt;
		
		return Math.sqrt(xDistance*xDistance + yDistance*yDistance + altDistance*altDistance);
	}

}
