package org.educationalProject.surfacePathfinder;

import java.util.List;
import java.util.Vector;

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
			double resultingTime;
			
			NanoClock clock = new NanoClock();
			
			clock.tic();
			Vector<Vector2D> points = ObjFileParser.getPoints("C:\\digdes\\map.obj");
			resultingTime = clock.tocd();
			System.out.println("Reading is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			List<Triangle2D> triangles = Triangulator.triangulate(points);
			resultingTime = clock.tocd();
			System.out.println("Triangulation is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			SimpleWeightedGraph<Integer,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles);
			resultingTime = clock.tocd();
			System.out.println("Graph building is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			AStarShortestPath<Integer,DefaultWeightedEdge> astar = 
					new AStarShortestPath<Integer,DefaultWeightedEdge>(
						graph,
						new ALTAdmissibleHeuristic<Integer,DefaultWeightedEdge>(graph,graph.vertexSet())
					);
			resultingTime = clock.tocd();
			System.out.println("Euristic building is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			Integer a = (int)(Math.random()*points.size());
			Integer b = (int)(Math.random()*points.size());
			List<Integer> nodes = astar.getPath(a, b).getVertexList();
			resultingTime = clock.tocd();
			System.out.println("Pathfinding is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			DijkstraShortestPath<Integer,DefaultWeightedEdge> alternative = new DijkstraShortestPath<Integer,DefaultWeightedEdge>(graph);
			List<Integer> dijkstraNodes = alternative.getPath(a, b).getVertexList();
			resultingTime = clock.tocd();
			System.out.println("Alternative algo is finished, phase duration is: " + resultingTime);
			
			boolean equal = true;
			for(int i = 0; i < dijkstraNodes.size(); i++)
				if(!dijkstraNodes.get(i).equals(nodes.get(i)))
					equal = false;
			System.out.println("alternative and main are equal: " + equal);
			
			Vector<Vector2D> pathCoords = new Vector<Vector2D>();
			for(int i = 0; i < nodes.size(); i++)
				pathCoords.add(points.get((int) nodes.get(i)));
			
			MapVisualizer visualizer;
			
			visualizer = new DecolorizedMapVisualizer();
			visualizer.setData(triangles, pathCoords);
			visualizer.visualize();
			
			System.out.println("end");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
