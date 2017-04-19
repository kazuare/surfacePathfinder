package org.educationalProject.surfacePathfinder.Dijkstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class OnlineNormalDijkstra extends OnlineDijkstra{
	public void process(Point currentNode, HashMap<Point,Route> routes){
		//System.out.println(currentNode + " is being  processed");
		HashSet<IdAndLength> currentNeighbours = neighbours.get(currentNode);
		//System.out.println("neighbours count: " + currentNeighbours.size());
		if(currentNeighbours == null) return;
		for(IdAndLength neighbour : currentNeighbours)
			if(notVisited(neighbour.id))
				if(routes.get(neighbour.id).length > routes.get(currentNode).length + neighbour.length){
					Route newRoute = routes.get(currentNode).copy();
					newRoute.append(neighbour.id);
					newRoute.length = routes.get(currentNode).length + neighbour.length; 
					routes.put(neighbour.id, newRoute);			
				}
			
	}
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




