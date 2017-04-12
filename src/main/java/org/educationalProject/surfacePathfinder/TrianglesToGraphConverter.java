package org.educationalProject.surfacePathfinder;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
/**
* takes in some triangles, adds all the satisfactory triangle edges to the graph
*/
public class TrianglesToGraphConverter {
	private final static double COS_THRESHOLD = 0.5;
	private final static double COST_MULTIPLIER = 25;
	private static double edgeWeight(Point a, Point b){
		return Math.abs(a.alt-b.alt) * COST_MULTIPLIER +
				Math.sqrt(
					(a.x-b.x)*(a.x-b.x)+
					(a.y-b.y)*(a.y-b.y)+
					(a.alt-b.alt)*(a.alt-b.alt)
				);
	}
	/**
	* determines if the edge is satisfactory (also returns false is the edge is already in graph)
	*/
	private static boolean judgeEdge(Point a, Point b, SimpleWeightedGraph<Point,DefaultWeightedEdge> graph){
		if(graph.containsEdge(a, b))
			return false;
		if(
			Math.sqrt(
				(a.x-b.x)*(a.x-b.x)+
				(a.y-b.y)*(a.y-b.y)
			)
			/
			Math.sqrt(
				(a.x-b.x)*(a.x-b.x)+
				(a.y-b.y)*(a.y-b.y)+
				(a.alt-b.alt)*(a.alt-b.alt)
			)
			<= 
			COS_THRESHOLD
		)
			return false;
		return true;
	}
	public static SimpleWeightedGraph<Point,DefaultWeightedEdge> convert(List<Triangle2D> triangles){
		
		SimpleWeightedGraph<Point,DefaultWeightedEdge> graph = new SimpleWeightedGraph<Point,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(Triangle2D triangle : triangles){
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
			if(!graph.containsVertex(a))
				graph.addVertex(a);
			if(!graph.containsVertex(b))
				graph.addVertex(b);
			if(!graph.containsVertex(c))
				graph.addVertex(c);
			
			if(judgeEdge(a, b, graph)){
				DefaultWeightedEdge e = graph.addEdge(a, b);
				graph.setEdgeWeight(e, edgeWeight(a, b));
			}
			if(judgeEdge(a, c, graph)){
				DefaultWeightedEdge e = graph.addEdge(a, c);
				graph.setEdgeWeight(e, edgeWeight(a, c));
			}
			if(judgeEdge(c, b, graph)){
				DefaultWeightedEdge e = graph.addEdge(c, b);
				graph.setEdgeWeight(e, edgeWeight(c, b));
			}
		}
		
		return graph;
	}
}
