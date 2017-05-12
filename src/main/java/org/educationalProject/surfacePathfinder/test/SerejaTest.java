package org.educationalProject.surfacePathfinder.test;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.educationalProject.surfacePathfinder.*;
import org.educationalProject.surfacePathfinder.path.AStarPathFind;
import org.educationalProject.surfacePathfinder.path.ParallelAStarPathFind;
import org.educationalProject.surfacePathfinder.timing.NanoClock;
import org.educationalProject.surfacePathfinder.timing.TicTocException;
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
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;


public class SerejaTest {

    //addition punishment in our distances
    static final double ALTITUDE_MULTIPLIER = 4;
    //is used to determine whether the edge is "bad" and should not be included
    static final double COS_THRESHOLD = 0.5;
    static final double TRIANGULATION_RADIUS = 0.75; //this value seems to work good.


    public static void setup(){
        EdgeWeighter.setParams(ALTITUDE_MULTIPLIER);
    }


    //addition punishment in our distances
//    static final double ALTITUDE_MULTIPLIER = 16;
    //is used to determene whether the edge is "bad" and should not be included
  //  static final double COS_THRESHOLD = 0.7;

    public void test1() {
        try{
            double resultingTime;
            // We will used this clock to measure time through all the phases
            NanoClock clock = new NanoClock();

            // Obj file reading
            clock.tic();
            ArrayList<Vector2D> points = ObjFileParser.getPoints("/home/merlin/DigDes/map.obj");
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

            double jgraphtResult;
            double serejaResult;
            double middle = 0.0;
            double jgraphtMiddle = 0.0;
            double serejaMiddle = 0.0;
            int errors = 0;
            int correct = 0;
            double jgraphtLength = 0.0;
            double serejaLength = 0.0;
            List<Point> nodes = new ArrayList<Point>();
            List<Point> aStarNodes = new ArrayList<Point>();

            for (int i = 0; i < 1; i++) {

                try {
                    clock.tic();

                    Point a = (Point) points.get((int) (Math.random() * points.size()));
                    Point b = (Point) points.get((int) (Math.random() * points.size()));
                    nodes = astar.getPath(a, b).getVertexList();
                    jgraphtResult = clock.tocd();
                    //System.out.println("\nJgrapht A*: " + jgraphtResult);
                    jgraphtMiddle += jgraphtResult;

                    clock.tic();

                    ParallelAStarPathFind aStarPathFind = new ParallelAStarPathFind();
                    aStarNodes = aStarPathFind.getShortestPath(graph, a, b);
                    serejaResult = clock.tocd();
                    // System.out.println("Sereja  A*: " + serejaResult);
                    serejaMiddle += serejaResult;
                    System.out.println(nodes.size() + "    " + aStarNodes.size());
                    for (int j = 0; j < nodes.size() - 1; j++) {
                        DefaultWeightedEdge e = graph.getEdge(nodes.get(j), nodes.get(j + 1));
                        jgraphtLength += (double) graph.getEdgeWeight(e);
                        if (j < aStarNodes.size()) {
                            double errPath = 0;
                            errPath += Math.abs(nodes.get(j).x - aStarNodes.get(j).x);
                            errPath += Math.abs(nodes.get(j).y - aStarNodes.get(j).y);
                            errPath += Math.abs(nodes.get(j).alt - aStarNodes.get(j).alt);
                            System.out.println("error " + j + "   " + errPath);
                        }
                    }

                    for (int j = 0; j < aStarNodes.size() - 1; j++) {
                        DefaultWeightedEdge e = graph.getEdge(aStarNodes.get(j), aStarNodes.get(j + 1));
                        serejaLength += (double) graph.getEdgeWeight(e);
                    }
                    if (aStarNodes.get(0).equals(a) && aStarNodes.get(aStarNodes.size() - 1).equals(b))
                        System.out.println("It`s ok");
                    System.out.println("sereja length:" + serejaLength);
                    System.out.println("jgrapht length:" + jgraphtLength);

                    if (Math.abs(serejaLength - jgraphtLength) < 1e-1 )
                        correct++;

                } catch (NullPointerException e){
                    errors++;
                    continue;
                }
            }

            System.out.println("Error in seconds: " + middle / 1000.0);
            System.out.println("Correct length: " + correct + " / 1000");
            System.out.println("Errors in path: " + errors);
            System.out.println("Sereja  middle: " + serejaMiddle / 1000.0);
            System.out.println("Jgrapht middle: " + jgraphtMiddle / 1000.0);
            //System.out.println("Jgrapht length: " + jgraphtLength);
            //System.out.println("Sereja  length: " + serejaLength);
            //Visualizing
            /*ColorizedMapVisualizer vis1 = new ColorizedMapVisualizer();
            vis1.setData(triangles, nodes, graph);
            SwingWindow.start(vis1, 800, 600, "Jgrapht`s A* map");

            //Visualizing
            ColorizedMapVisualizer vis2 = new ColorizedMapVisualizer();
            vis2.setData(triangles, aStarNodes, graph);
            SwingWindow.start(vis2, 800, 600, "Sereja`s A* map");

            PathVisualizer vis3 = new PathVisualizer();
            vis3.setData(nodes, points);
            SwingWindow.start(vis3, 800, vis3.calculateWindowHeight(800), "Jgrapht`s A*");

            PathVisualizer vis4 = new PathVisualizer();
            vis4.setData(aStarNodes, points);
            SwingWindow.start(vis4, 800, vis4.calculateWindowHeight(800), " Sereja`s A*");*/

            System.out.println("end");
        }catch(IOException e){
            System.out.println("Problem with file reading accured!");
        }catch(TicTocException e){
            System.out.println("There is a problem with time measuring code. toc is executed before tic.");
        } catch (NotEnoughPointsException e) {
            System.out.println("Not enough points to triangulate");
        }

    }

    public static void test(){
        try{
            double resultingTime;
            // We will used this clock to measure time through all the phases
            NanoClock clock = new NanoClock();

            // Obj file reading
            clock.tic();
            ArrayList<Point> realPoints = ObjFileParser.getPoints2("/home/merlin/DigDes/map.obj");
            resultingTime = clock.tocd();
            System.out.println("Reading is finished, phase duration is: " + resultingTime);
            System.out.println("N is: " + realPoints.size());

            GraphProxy graph = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "UnsafeJdiemkeTriangulator");
            //GraphProxy graph1 = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "DomainBasedJdiemkeTriangulator");
            //GraphProxy graph2 = new GraphProxy(TRIANGULATION_RADIUS, realPoints, "GreedyTriangulator");



            for(int i = 0; i < 1; i++){
                Point a = realPoints.get((int)(Math.random()*realPoints.size()));
                Point b = realPoints.get((int)(Math.random()*realPoints.size()));
                System.out.println("\n#" + i);
                showAStar(clock, graph, a, b, "Unsafe triangulation");

            }

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
                        new EuclidianEuristic<Point>()
                );
        List<Point> jgraphtNodes = jgraphtAStar.getPath(a, b).getVertexList();
        resultingTime = clock.tocd();
        System.out.println("Jgrapht algo is finished, phase duration is: " + resultingTime);
        System.out.println("Path length is: " + jgraphtAStar.getPath(a, b).getWeight());

        clock.tic();
        ParallelAStarPathFind aStarPathFind = new ParallelAStarPathFind();
        List<Point> serejaNodes = aStarPathFind.getShortestPath(graph, a, b);
        resultingTime = clock.tocd();
        System.out.println("Sereja algo is finished, phase duration is: " + resultingTime);
        System.out.println("Path length is: " + aStarPathFind.getLengthOfPath());




        //Visualizing
        DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
        vis.setData(graph, jgraphtNodes);
        SwingWindow.start(vis, 700, 700, "Jgrapht");
        DecolorizedMapVisualizer vis1 = new DecolorizedMapVisualizer();
        vis1.setData(graph, serejaNodes);
        SwingWindow.start(vis1, 700, 700, "Sereja");
    }
}