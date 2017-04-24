package org.educationalProject.surfacePathfinder.onlineTriangulation;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public interface OnlineTriangulator {
	/**
	 * creates new graph based on the points given
	 **/
	public SimpleWeightedGraph<Point,DefaultWeightedEdge> init(Point start);
	/**
	 * adds new edges to the graph based on new points given
	 * for triangulation to be always correct new area need to intersect with some previous ones.
	 **/
	public SimpleWeightedGraph<Point,DefaultWeightedEdge> update(Point start);
	
		
}
