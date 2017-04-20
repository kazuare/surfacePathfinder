package org.educationalProject.surfacePathfinder.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.educationalProject.surfacePathfinder.*;
import org.educationalProject.surfacePathfinder.path.AStarPathFind;
import org.educationalProject.surfacePathfinder.path.DijkstraPathFind;
import org.educationalProject.surfacePathfinder.timing.*;
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

public class SerejaTest {

    //addition punishment in our distances
    static final double ALTITUDE_MULTIPLIER = 16;
    //is used to determene whether the edge is "bad" and should not be included
    static final double COS_THRESHOLD = 0.7;

    public void test() {
        try{
            double resultingTime;
            // We will used this clock to measure time through all the phases
            NanoClock clock = new NanoClock();

            // Obj file reading
            clock.tic();
            Vector<Vector2D> points = ObjFileParser.getPoints("/home/merlin/DigDes/map.obj");
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


          /*  //Finding the shortest path
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
            System.out.println("A* from jgrapht is finished, phase duration is: " + resultingTime);*/


            double middle = 0.0;
            double middleJgrapht = 0.0;
            double middleSereja = 0.0;
            int errors = 0;
            int incorrect = 0;
            double jgraphtLength = 0.0;
            double serejaLength = 0.0;
            List<Point> dijkstraNodes;
            List<Point> dijkstraNodes1;
            Point a = (Point) points.get((int) (Math.random() * points.size()));
            Point b = (Point) points.get((int) (Math.random() * points.size()));

            DijkstraShortestPath<Point, DefaultWeightedEdge> alternative = new DijkstraShortestPath<Point, DefaultWeightedEdge>(graph);
            dijkstraNodes = alternative.getPath(a, b).getVertexList();

            //  System.out.println("\nJgrapht: " + jgraphtResult);


            DijkstraPathFind dijkstraPathFind = new DijkstraPathFind();
            dijkstraNodes1 = dijkstraPathFind.getShortestPath(graph, a, b);

            for (int i = 0; i < 1000; i++) {
                double jgraphtResult;
                double serejaResult;
                try {
                    a = (Point) points.get((int) (Math.random() * points.size()));
                    b = (Point) points.get((int) (Math.random() * points.size()));
                    clock.tic();
                    dijkstraNodes = alternative.getPath(a, b).getVertexList();
                    jgraphtResult = clock.tocd();
                    //  System.out.println("\nJgrapht: " + jgraphtResult);

                    clock.tic();
                    dijkstraNodes1 = dijkstraPathFind.getShortestPath(graph, a, b);
                    serejaResult = clock.tocd();
                    //    System.out.println("Sereja:  " + serejaResult);
                    middle += serejaResult - jgraphtResult;
                    middleJgrapht += jgraphtResult;
                    middleSereja += serejaResult;
                    boolean equal = true;
                    if (dijkstraNodes1.size() == dijkstraNodes.size()) {
                        for (int j = 0; j < dijkstraNodes.size(); j++)
                            if (!dijkstraNodes.get(j).equals(dijkstraNodes1.get(j)))
                                equal = false;
                    } else {
                        equal = false;
                    }
                    if (!equal)
                        errors++;

                } catch (NullPointerException e) {
                    incorrect++;
                    continue;
                }
                /*clock.tic();
                AStarPathFind aStarPathFind = new AStarPathFind();
                List<Point> aStarNodes = aStarPathFind.getShortestPath(graph, a, b);
                resultingTime = clock.tocd();
                System.out.println("Sereja`s A* is finished, phase duration is: " + resultingTime);*/
            }

            for (int i = 0; i < dijkstraNodes.size() - 1; i++){
                DefaultWeightedEdge e = graph.getEdge(dijkstraNodes.get(i), dijkstraNodes.get(i + 1));
                jgraphtLength += (double)graph.getEdgeWeight(e);
            }

            for (int i = 0; i < dijkstraNodes1.size() - 1; i++){
                DefaultWeightedEdge e = graph.getEdge(dijkstraNodes1.get(i), dijkstraNodes1.get(i + 1));
                serejaLength += (double)graph.getEdgeWeight(e);
            }
            System.out.println("Error in seconds: " + middle / 1000.0);
            System.out.println("Incorrect input: " + incorrect + " / 1000");
            System.out.println("Errors in path: " + errors);
            System.out.println("Jgrapht middle: " + middleJgrapht / 1000.0);
            System.out.println("Sereja  middle: " + middleSereja / 1000.0);

            /*//Visualizing
            ColorizedMapVisualizer vis1 = new ColorizedMapVisualizer();
            vis1.setData(triangles, dijkstraNodes, graph);
            SwingWindow.start(vis1, 800, 600, "Jgrapht`s Dijkstra map");

            //Visualizing
            ColorizedMapVisualizer vis2 = new ColorizedMapVisualizer();
            vis2.setData(triangles, dijkstraNodes1, graph);
            SwingWindow.start(vis2, 800, 600, "Sereja`s Dijkstra map");

            PathVisualizer vis3 = new PathVisualizer();
            vis3.setData(dijkstraNodes, points);
            SwingWindow.start(vis3, 800, vis3.calculateWindowHeight(800), "Jgrapht`s Dijkstra");

            PathVisualizer vis4 = new PathVisualizer();
            vis4.setData(dijkstraNodes1, points);
            SwingWindow.start(vis4, 800, vis4.calculateWindowHeight(800), " Sereja`s Dijkstra");*/

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
