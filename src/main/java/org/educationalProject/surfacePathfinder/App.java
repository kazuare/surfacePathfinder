package org.educationalProject.surfacePathfinder;

import java.util.List;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
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
			Integer a = (int)(Math.random()*points.size());
			Integer b = (int)(Math.random()*points.size());
			GraphPath path = astar.getPath(a, b);
			List nodes = path.getVertexList();
			endTime = System.nanoTime();
			System.out.println("Pathfinding is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			
			startTime = System.nanoTime();
			DijkstraShortestPath<Integer,DefaultWeightedEdge> alternative = new DijkstraShortestPath<Integer,DefaultWeightedEdge>(graph);
			List dijkstraNodes = alternative.getPath(a, b).getVertexList();
			endTime = System.nanoTime();
			System.out.println("Alternative algo is finished, phase duration is: " + (endTime - startTime)/1000000000.0);
			boolean equal = true;
			for(int i = 0; i < dijkstraNodes.size(); i++)
				if(!dijkstraNodes.get(i).equals(nodes.get(i)))
					equal = false;
			System.out.println("alternative and main are equal: " + equal);
			
			
			
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
