package org.educationalProject.surfacePathfinder;

import java.util.List;
import java.util.Vector;

import org.educationalProject.surfacePathfinder.timing.NanoClock;
import org.educationalProject.surfacePathfinder.visualization.AwtWindow;
import org.educationalProject.surfacePathfinder.visualization.ColorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.PathVisualizer;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
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
			SimpleWeightedGraph<Point,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles);
			resultingTime = clock.tocd();
			System.out.println("Graph building is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			AStarShortestPath<Point,DefaultWeightedEdge> astar = 
					new AStarShortestPath<Point,DefaultWeightedEdge>(
						graph,
						new EuclidianEuristic<Point>()
					);
			resultingTime = clock.tocd();
			System.out.println("Euristic building is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			Point a = (Point)points.get((int)(Math.random()*points.size()));
			Point b = (Point)points.get((int)(Math.random()*points.size()));
			List<Point> nodes = astar.getPath(a, b).getVertexList();
			resultingTime = clock.tocd();
			System.out.println("Pathfinding is finished, phase duration is: " + resultingTime);
			
			clock.tic();
			DijkstraShortestPath<Point,DefaultWeightedEdge> alternative = new DijkstraShortestPath<Point,DefaultWeightedEdge>(graph);
			List<Point> dijkstraNodes = alternative.getPath(a, b).getVertexList();
			resultingTime = clock.tocd();
			System.out.println("Alternative algo is finished, phase duration is: " + resultingTime);
			
			boolean equal = true;
			for(int i = 0; i < dijkstraNodes.size(); i++)
				if(!dijkstraNodes.get(i).equals(nodes.get(i)))
					equal = false;
			System.out.println("alternative and main are equal: " + equal);
			
		
			DecolorizedMapVisualizer vis1 = new DecolorizedMapVisualizer();
			vis1.setData(triangles, nodes, graph);
			SwingWindow.start(vis1, 800, 600);

			ColorizedMapVisualizer vis2 = new ColorizedMapVisualizer();
			vis2.setData(triangles, nodes, graph);
			SwingWindow.start(vis2, 800, 600);
			
			PathVisualizer vis3 = new PathVisualizer();
			vis3.setData(nodes, points);
			SwingWindow.start(vis3, 1000, 400);
		
			System.out.println("end");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
