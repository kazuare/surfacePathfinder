package org.educationalProject.surfacePathfinder.test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.educationalProject.surfacePathfinder.*;
import org.educationalProject.surfacePathfinder.timing.NanoClock;
import org.educationalProject.surfacePathfinder.timing.TicTocException;
import org.educationalProject.surfacePathfinder.visualization.ColorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.PathVisualizer;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;

public class YegorTest {

    //addition punishment in our distances
    static final double ALTITUDE_MULTIPLIER = 4;
    //is used to determine whether the edge is "bad" and should not be included
    // 0 == no exclusion at all
    static final double COS_THRESHOLD = 0;
    static final double TRIANGULATION_RADIUS = 0.75; //this value seems to work good.
    
    public static void setup(){
    	EdgeWeighter.setParams(ALTITUDE_MULTIPLIER);
    }
    
    public static void partialTriangulationExample(){
		try{
			double resultingTime;
			// We will used this clock to measure time through all the phases
			NanoClock clock = new NanoClock();
			
			// Obj file reading
			clock.tic();
			ArrayList<Point> realPoints = ObjFileParser.getPoints2("C:\\digdes\\map.obj");
			resultingTime = clock.tocd();
			System.out.println("Reading is finished, phase duration is: " + resultingTime);
			System.out.println("N is: " + realPoints.size());
			
			GraphProxy graph = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "UnsafeJdiemkeTriangulator");
			GraphProxy graph1 = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "UnsafeJdiemkeTriangulator");
			
			Point a = realPoints.get((int)(Math.random()*realPoints.size()));
			Point b = realPoints.get((int)(Math.random()*realPoints.size()));

			showAStar(clock, graph, a, b);
			graph.visualizeDebug();
			showDijkstra(clock, graph1, a, b);
			
			System.out.println("end");			
		}catch(IOException e){
			System.out.println("Problem with file reading accured!");
		}catch(TicTocException e){
			System.out.println("There is a problem with time measuring code. toc is executed before tic.");
		}
		
	}
    
    public static void showAStar(NanoClock clock, GraphProxy graph, Point a, Point b) throws TicTocException{
    	showAStar(clock, graph, a, b, "Astar");
    }
    public static void showDijkstra(NanoClock clock, GraphProxy graph, Point a, Point b) throws TicTocException{
    	showDijkstra(clock, graph, a, b, "Dijkstra");
    }
    
    public static void showAStar(NanoClock clock, GraphProxy graph, Point a, Point b, String windowName) throws TicTocException{
    	double resultingTime;
    	clock.tic();
		AStarShortestPath<Point,DefaultWeightedEdge> astar = 
				new AStarShortestPath<Point,DefaultWeightedEdge>(
					graph,
					new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
				);
		List<Point> nodes = astar.getPath(a, b).getVertexList();
		resultingTime = clock.tocd();
		System.out.println("Astar algo is finished, phase duration is: " + resultingTime);
		System.out.println("Path length is: " + astar.getPath(a, b).getWeight());
		//Visualizing
		DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
		vis.setData(graph, nodes);
		SwingWindow.start(vis, 700, 700, windowName);
    }
    
    public static void showDijkstra(NanoClock clock, GraphProxy graph, Point a, Point b, String windowName) throws TicTocException{
    	double resultingTime;
    	clock.tic();
    	DijkstraShortestPath<Point,DefaultWeightedEdge> dijkstra = 
				new DijkstraShortestPath<Point,DefaultWeightedEdge>(graph);
		List<Point> nodes = dijkstra.getPath(a, b).getVertexList();
		resultingTime = clock.tocd();
		System.out.println("Dijkstra algo is finished, phase duration is: " + resultingTime);
		System.out.println("Path length is: " + dijkstra.getPath(a, b).getWeight());
		
		//Visualizing
		DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
		vis.setData(graph, nodes);
		SwingWindow.start(vis, 700, 700, windowName);
    }
    
    
    public static void fullTriangulationExample(){
    	try{
            double resultingTime;
            // We will used this clock to measure time through all the phases
            NanoClock clock = new NanoClock();

            // Obj file reading
            clock.tic();
            ArrayList<Vector2D> points = ObjFileParser.getPoints("C:\\digdes\\map.obj");
            resultingTime = clock.tocd();
            System.out.println("Reading is finished, phase duration is: " + resultingTime);

            // Points triangulating
            clock.tic();
            List<Triangle2D> triangles = Triangulator.triangulate(points);
            resultingTime = clock.tocd();
            System.out.println("Triangulation is finished, phase duration is: " + resultingTime);

            // Triangles -> graph convertion. Some edges are deleted if they have high altitude delta
            clock.tic();
            SimpleWeightedGraph<Point,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles, COS_THRESHOLD, ALTITUDE_MULTIPLIER);
            resultingTime = clock.tocd();
            System.out.println("Graph building is finished, phase duration is: " + resultingTime);

            //Finding the shortest path
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

            //Visualizing
            ColorizedMapVisualizer vis1 = new ColorizedMapVisualizer();
            vis1.setData(triangles, nodes, graph);
            SwingWindow.start(vis1, 800, 600, "А* map");

            PathVisualizer vis2 = new PathVisualizer();
            vis2.setData(nodes, points);
            SwingWindow.start(vis2, 800, vis2.calculateWindowHeight(800), "А*");

            System.out.println("end");
        }catch(IOException e){
            System.out.println("Problem with file reading accured!");
        }catch(TicTocException e){
            System.out.println("There is a problem with time measuring code. toc is executed before tic.");
        } catch (NotEnoughPointsException e) {
            System.out.println("Not enough points to triangulate");
        }
    }
}
