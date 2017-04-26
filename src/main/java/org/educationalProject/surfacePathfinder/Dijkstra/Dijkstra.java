package org.educationalProject.surfacePathfinder.Dijkstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public abstract class Dijkstra {
	private Set<Point> usedNodes;
	protected LinkedHashSet<Point> notVisited;
	protected HashMap<Point, HashSet<IdAndLength>> neighbours;
	
	public boolean notVisited(Point id){
		return notVisited.contains(id);
	}
	public void setVisited(Point currentNode){
		notVisited.remove(currentNode);
	}
	
	public abstract Point findMin(HashMap<Point,Route> routes);
	
	public void init(WeightedGraph<Point, DefaultWeightedEdge> g){
		//count all nodes
		usedNodes = g.vertexSet();
		notVisited = new LinkedHashSet<Point>();
		neighbours = new HashMap<Point, HashSet<IdAndLength>>();
		
			
		//filling [total nodes] set, [visited nodes] sets, [neighbour nodes] sets
		for(Point x : usedNodes){
			notVisited.add(x);
			neighbours.put(x, new HashSet<IdAndLength>());
			for (DefaultWeightedEdge y: g.edgesOf(x)){	
				if(g.getEdgeTarget(y) != x)
					neighbours.get(x).add(
						new IdAndLength(g.getEdgeTarget(y), g.getEdgeWeight(y))
					);
				if(g.getEdgeSource(y) != x)
					neighbours.get(x).add(
						new IdAndLength(g.getEdgeSource(y), g.getEdgeWeight(y))
					);
			}
		}
	}
	public abstract void process(Point currentNode, HashMap<Point,Route> routes);
	
	
	public HashMap<Point, Route> run(Point start){
		
		HashMap<Point,Route> routes = new HashMap<Point,Route>();
		
		for(Point usedNode : usedNodes){
			Route r = new Route();
			if(!usedNode.equals(start))
				r.length = Double.POSITIVE_INFINITY;
			else
				r.length = 0;
			routes.put(usedNode, r);
		}
		
		Point currentNode = start;
		routes.get(currentNode).append(currentNode);
		int count = 0;
		while(currentNode != null){
			++count;
			if(count%800 == 0)System.out.println(count);
			process(currentNode, routes);
			setVisited(currentNode);
			currentNode = findMin(routes);
		}
				
		return routes;
	}
}



