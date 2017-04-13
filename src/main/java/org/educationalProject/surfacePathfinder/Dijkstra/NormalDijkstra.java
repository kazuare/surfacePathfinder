package org.educationalProject.surfacePathfinder.Dijkstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class NormalDijkstra extends Dijkstra{
	
	public Point findMin(HashMap<Point,Route> routes){
		Point minId = null;
		
		for(Point node : notVisited){
			if(minId == null)
				minId = node;
			else if(routes.get(minId).length > routes.get(node).length)
				minId = node;
		}
		
		return minId;
		
	}
}




