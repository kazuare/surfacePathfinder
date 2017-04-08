package org.educationalProject.surfacePathfinder;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;

public class TrianglesToGraphConverter {
	public static SimpleWeightedGraph<Integer,DefaultWeightedEdge> convert(List<Triangle2D> triangles){
		
		SimpleWeightedGraph<Integer,DefaultWeightedEdge> graph = new SimpleWeightedGraph<Integer,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(Triangle2D triangle : triangles){
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
			
			
			if(!graph.containsVertex(a.id))
				graph.addVertex(a.id);
			if(!graph.containsVertex(b.id))
				graph.addVertex(b.id);
			if(!graph.containsVertex(c.id))
				graph.addVertex(c.id);
			
			if(!graph.containsEdge(a.id, b.id)){
				DefaultWeightedEdge e = graph.addEdge(a.id,b.id);
				graph.setEdgeWeight(e, Math.abs(a.alt-b.alt));
			}
			if(!graph.containsEdge(a.id, c.id)){
				DefaultWeightedEdge e = graph.addEdge(a.id,c.id);
				graph.setEdgeWeight(e, Math.abs(a.alt-c.alt));
			}
			if(!graph.containsEdge(c.id, b.id)){
				DefaultWeightedEdge e = graph.addEdge(c.id,b.id);
				graph.setEdgeWeight(e, Math.abs(c.alt-b.alt));
			}
		}
		
		return graph;
	}
}
