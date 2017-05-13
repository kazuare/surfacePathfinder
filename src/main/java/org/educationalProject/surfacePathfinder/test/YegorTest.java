package org.educationalProject.surfacePathfinder.test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.educationalProject.surfacePathfinder.*;
import org.educationalProject.surfacePathfinder.timing.NanoClock;
import org.educationalProject.surfacePathfinder.timing.TicTocException;
import org.educationalProject.surfacePathfinder.twoTierAStar.TwoTierAStar;
import org.educationalProject.surfacePathfinder.visualization.ColorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.PathVisualizer;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.onlineTriangulation.ModifiedJdiemke;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.GraphVisualizer;

public class YegorTest {

    //addition punishment in our distances
    static final double ALTITUDE_MULTIPLIER = 4;
    //is used to determine whether the edge is "bad" and should not be included
    // 0 == no exclusion at all
    static final double COS_THRESHOLD = 0;
    static final double TRIANGULATION_RADIUS = 0.75; //this value seems to work good.
    
    public static void setup(){
    	EdgeWeighter.setParams(ALTITUDE_MULTIPLIER);
    	EdgeValidator.setParams(COS_THRESHOLD);
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
			//showDijkstra(clock, graph1, a, b);
			
			System.out.println("end");			
		}catch(IOException e){
			System.out.println("Problem with file reading accured!");
		}catch(TicTocException e){
			System.out.println("There is a problem with time measuring code. toc is executed before tic.");
		}
		
	}
    public static void defaultResultVisualizing(WeightedGraph<Point, DefaultWeightedEdge> graph, List<Point> nodes, String windowName){
    	DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
		vis.setData(graph, nodes);
		SwingWindow.start(vis, 700, 700, windowName);
    }
    
    public static void showAStar(NanoClock clock, WeightedGraph<Point, DefaultWeightedEdge> graph, Point a, Point b) throws TicTocException{
    	showAStar(clock, graph, a, b, "Astar");
    }
    
    public static void showDijkstra(NanoClock clock, WeightedGraph<Point, DefaultWeightedEdge> graph, Point a, Point b) throws TicTocException{
    	showDijkstra(clock, graph, a, b, "Dijkstra");
    }
    
    public static void showAStar(NanoClock clock, WeightedGraph<Point, DefaultWeightedEdge> graph, Point a, Point b, String windowName) throws TicTocException{
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
		
		defaultResultVisualizing(graph, nodes, windowName);
    }
    
    public static void showDijkstra(NanoClock clock, WeightedGraph<Point, DefaultWeightedEdge> graph, Point a, Point b, String windowName) throws TicTocException{
    	double resultingTime;
    	clock.tic();
    	DijkstraShortestPath<Point,DefaultWeightedEdge> dijkstra = 
				new DijkstraShortestPath<Point,DefaultWeightedEdge>(graph);
		List<Point> nodes = dijkstra.getPath(a, b).getVertexList();
		resultingTime = clock.tocd();
		System.out.println("Dijkstra algo is finished, phase duration is: " + resultingTime);
		System.out.println("Path length is: " + dijkstra.getPath(a, b).getWeight());
		
		defaultResultVisualizing(graph, nodes, windowName);
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

            clock.tic();
            
            // Points triangulating
            List<Triangle2D> triangles = Triangulator.triangulate(points);       
            // Triangles -> graph convertion. Some edges are deleted if they have high altitude delta
            SimpleWeightedGraph<Point,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles, COS_THRESHOLD, ALTITUDE_MULTIPLIER);
            
            resultingTime = clock.tocd();
            System.out.println("Graph building is finished, phase duration is: " + resultingTime);

            clock.tic();
            //Finding the shortest path
            AStarShortestPath<Point,DefaultWeightedEdge> astar =
                    new AStarShortestPath<Point,DefaultWeightedEdge>(
                            graph,
                            new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
                    );  
                     
            Point a = (Point)points.get((int)(Math.random()*points.size()));
            Point b = (Point)points.get((int)(Math.random()*points.size()));
            
            List<Point> nodes = astar.getPath(a, b).getVertexList();
            resultingTime = clock.tocd();
            System.out.println("Pathfinding is finished, phase duration is: " + resultingTime);

            System.out.println("Path length is: " + astar.getPath(a, b).getWeight());
            //Visualizing
            DecolorizedMapVisualizer vis1 = new DecolorizedMapVisualizer();
            vis1.setData(graph, nodes);
            SwingWindow.start(vis1, 700, 700, "full triangulation map");

            //PathVisualizer vis2 = new PathVisualizer();
            //vis2.setData(nodes, points);
            //SwingWindow.start(vis2, 800, vis2.calculateWindowHeight(800), "А*");

            clock.tic();
            
            GraphProxy graph2 = new GraphProxy(
            	1.5*TRIANGULATION_RADIUS, 
            	(ArrayList<Point>)(ArrayList<? extends Vector2D>)points, 
            	"ModifiedJdiemke"
            );
            AStarShortestPath<Point,DefaultWeightedEdge> astar2 =
            		new AStarShortestPath<Point,DefaultWeightedEdge>(
                            graph2,
                            new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
                    );
            List<Point> nodes2 = astar2.getPath(a, b).getVertexList();
            
            resultingTime = clock.tocd();
            System.out.println("partial triangulation A* is finished, phase duration is: " + resultingTime);
            System.out.println("Path length is: " + astar2.getPath(a, b).getWeight());
            DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
            vis.setData(graph2, nodes2);
            SwingWindow.start(vis, 700, 700, "modified jdiemke map");
            
            clock.tic();
            TwoTierAStar twotier = new TwoTierAStar(points, (int)(points.size()*0.2), 30, COS_THRESHOLD, ALTITUDE_MULTIPLIER, TRIANGULATION_RADIUS);
            List<Point> nodes3 = twotier.findPath(a,b);
            resultingTime = clock.tocd();
            System.out.println("two tier A* is finished, phase duration is: " + resultingTime);
            TwoTierAStar.analyzeResult(nodes3, graph, a, b);
            
            
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
