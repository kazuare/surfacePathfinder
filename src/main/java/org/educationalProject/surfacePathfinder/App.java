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
			
			Vector<Vector2D> points = ObjFileParser.getPoints("C:\\digdes\\map.obj");
			System.out.println("Reading is finished");
			List<Triangle2D> triangles = Triangulator.triangulate(points);
			System.out.println("Triangulation is finished");
			SimpleWeightedGraph<Integer,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles);
			System.out.println("Graph building is finished");
			AStarShortestPath<Integer,DefaultWeightedEdge> astar = 
					new AStarShortestPath<Integer,DefaultWeightedEdge>(
						graph,
						new ALTAdmissibleHeuristic<Integer,DefaultWeightedEdge>(graph,graph.vertexSet())
					);
			GraphPath path = astar.getPath((int)(Math.random()*points.size()), (int)(Math.random()*points.size()));
			List nodes = path.getVertexList();
			System.out.println("Pathfinding is finished");
			Vector<Vector2D> pathCoords = new Vector<Vector2D>();
			for(int i = 0; i < nodes.size(); i++)
				pathCoords.add(points.get((int) nodes.get(i)));
			System.out.println("Id to coords mapping is finished");
			MapVisualizer visualizer = new MapVisualizer();
			visualizer.run(triangles,pathCoords);
			System.out.println("Visualizing is finished");
			
			
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
