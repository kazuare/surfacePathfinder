package org.educationalProject.surfacePathfinder;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;

public class TrianglesToGraphConverter {
	public static double edgeWeight(Point a, Point b){
		return Math.abs(a.alt-b.alt) +
				Math.sqrt(
					(a.x-b.x)*(a.x-b.x)+
					(a.y-b.y)*(a.y-b.y)+
					(a.alt-b.alt)*(a.alt-b.alt)
				);
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
			
			if(!graph.containsEdge(a, b)){
				DefaultWeightedEdge e = graph.addEdge(a, b);
				graph.setEdgeWeight(e, edgeWeight(a, b));
			}
			if(!graph.containsEdge(a, c)){
				DefaultWeightedEdge e = graph.addEdge(a, c);
				graph.setEdgeWeight(e, edgeWeight(a, c));
			}
			if(!graph.containsEdge(c, b)){
				DefaultWeightedEdge e = graph.addEdge(c, b);
				graph.setEdgeWeight(e, edgeWeight(c, b));
			}
		}
		
		return graph;
	}
}
