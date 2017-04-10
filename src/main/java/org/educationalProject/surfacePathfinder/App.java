package org.educationalProject.surfacePathfinder;

import java.util.List;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class App {

	public static void main(String[] args) {
		try {
			long startTime;
			long endTime;
			
			startTime = System.nanoTime();
			Vector<Vector2D> points = ObjFileParser.getPoints("C:\\digdes\\map.obj");
			endTime = System.nanoTime();
			System.out.println("Reading is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			
			startTime = System.nanoTime();
			List<Triangle2D> triangles = Triangulator.triangulate(points);
			endTime = System.nanoTime();
			System.out.println("Triangulation is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			
			startTime = System.nanoTime();
			SimpleWeightedGraph<Integer,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles);
			endTime = System.nanoTime();
			System.out.println("Graph building is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			
			startTime = System.nanoTime();
			AStarShortestPath<Integer,DefaultWeightedEdge> astar = 
					new AStarShortestPath<Integer,DefaultWeightedEdge>(
						graph,
						new ALTAdmissibleHeuristic<Integer,DefaultWeightedEdge>(graph,graph.vertexSet())
					);
			endTime = System.nanoTime();
			System.out.println("Euristic building is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			
			startTime = System.nanoTime();
			GraphPath path = astar.getPath((int)(Math.random()*points.size()), (int)(Math.random()*points.size()));
			List nodes = path.getVertexList();
			endTime = System.nanoTime();
			System.out.println("Pathfinding is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			
			Vector<Vector2D> pathCoords = new Vector<Vector2D>();
			for(int i = 0; i < nodes.size(); i++)
				pathCoords.add(points.get((int) nodes.get(i)));
			MapVisualizer visualizer = new MapVisualizer();
			visualizer.run(triangles,pathCoords);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
