package org.educationalProject.surfacePathfinder.test;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.educationalProject.surfacePathfinder.*;
import org.educationalProject.surfacePathfinder.Dijkstra.Dijkstra;
import org.educationalProject.surfacePathfinder.Dijkstra.HillDijkstra;
import org.educationalProject.surfacePathfinder.Dijkstra.Route;
import org.educationalProject.surfacePathfinder.path.AStarPathFind;
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
import org.educationalProject.surfacePathfinder.Dijkstra.OnlineNormalDijkstra;
import org.educationalProject.surfacePathfinder.onlineTriangulation.DomainBasedJdiemkeTriangulator;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GreedyTriangulator;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.GraphVisualizer;

public class SerejaTest {

    //addition punishment in our distances
    static final double ALTITUDE_MULTIPLIER = 4;
    //is used to determine whether the edge is "bad" and should not be included
    static final double COS_THRESHOLD = 0.5;
    static final double TRIANGULATION_RADIUS = 0.75; //this value seems to work good.


    public static void setup(){
        EdgeWeighter.setParams(ALTITUDE_MULTIPLIER);
    }
    public static void test(){
        try{
            double resultingTime;
            // We will used this clock to measure time through all the phases
            NanoClock clock = new NanoClock();

            // Obj file reading
            clock.tic();
            Vector<Point> realPoints = ObjFileParser.getPoints2("/home/merlin/DigDes/map.obj");
            resultingTime = clock.tocd();
            System.out.println("Reading is finished, phase duration is: " + resultingTime);
            System.out.println("N is: " + realPoints.size());

            GraphProxy graph = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "UnsafeJdiemkeTriangulator");
            GraphProxy graph1 = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "DomainBasedJdiemkeTriangulator");
            GraphProxy graph2 = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "GreedyTriangulator");

            Point a = realPoints.get((int)(Math.random()*realPoints.size()));
            Point b = realPoints.get((int)(Math.random()*realPoints.size()));

            showAStar(clock, graph, a, b, "Unsafe triangulation");
            //graph.visualizeDebug();
            //showDijkstra(clock, graph1, a, b);

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

    public static void showAStar(NanoClock clock, GraphProxy graph, Point a, Point b, String windowName) throws TicTocException{
        double resultingTime;
        clock.tic();
        AStarShortestPath<Point, DefaultWeightedEdge> jgraphtAStar =
                new AStarShortestPath<Point,DefaultWeightedEdge>(
                        graph,
                        new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
                );
        List<Point> jgraphtNodes = jgraphtAStar.getPath(a, b).getVertexList();
        resultingTime = clock.tocd();
        System.out.println("Jgrapht algo is finished, phase duration is: " + resultingTime);
        System.out.println("Path length is: " + jgraphtAStar.getPath(a, b).getWeight());

        clock.tic();
        AStarPathFind aStarPathFind = new AStarPathFind();
        List<Point> serejaNodes = aStarPathFind.getShortestPath(graph, a, b);
        resultingTime = clock.tocd();
        System.out.println("Sereja algo is finished, phase duration is: " + resultingTime);
        System.out.println("Path length is: " + aStarPathFind.getLengthOfPath());


        //Visualizing
        DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
        vis.setData(graph, jgraphtNodes);
        SwingWindow.start(vis, 700, 700, "Jgrapht");
        DecolorizedMapVisualizer vis1 = new DecolorizedMapVisualizer();
        vis1.setData(graph, jgraphtNodes);
        SwingWindow.start(vis, 700, 700, "Sereja");
    }
}