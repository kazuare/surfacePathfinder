package org.educationalProject.surfacePathfinder;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
/**
* takes in some triangles, adds all the satisfactory triangle edges to the graph
*/
public class TrianglesToGraphConverter {
	public static void setParams(double threshold, double multiplier){
		EdgeValidator.setParams(threshold);
		EdgeWeighter.setParams(multiplier);
	}
	
	private static double edgeWeight(Point a, Point b){
		return EdgeWeighter.edgeWeight(a, b);
	}
	
	/**
	* determines if the edge is satisfactory (also returns false is the edge is already in graph)
	*/
	private static boolean judgeEdge(Point a, Point b, SimpleWeightedGraph<Point,DefaultWeightedEdge> graph){
		if(graph.containsEdge(a, b))
			return false;
		return EdgeValidator.judge(a,b);
	}
	public static SimpleWeightedGraph<Point,DefaultWeightedEdge> convert(List<Triangle2D> triangles, double threshold, double multiplier){
		
		setParams(threshold,multiplier);
		
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
