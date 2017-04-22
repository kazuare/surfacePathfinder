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
            int incorrect = 0;
            double jgraphtLength = 0.0;
            double serejaLength = 0.0;
            for (int i = 0; i < 1000; i++) {

                try {
                    clock.tic();

                    Point a = (Point) points.get((int) (Math.random() * points.size()));
                    Point b = (Point) points.get((int) (Math.random() * points.size()));
                    List<Point> nodes = astar.getPath(a, b).getVertexList();
                    jgraphtResult = clock.tocd();
                    //System.out.println("\nJgrapht A*: " + jgraphtResult);
                    jgraphtMiddle += jgraphtResult;

                    clock.tic();
                    AStarPathFind aStarPathFind = new AStarPathFind();
                    List<Point> aStarNodes = aStarPathFind.getShortestPath(graph, a, b);
                    serejaResult = clock.tocd();
                   // System.out.println("Sereja  A*: " + serejaResult);
                    serejaMiddle += serejaResult;

                    for (int j = 0; j < nodes.size() - 1; j++) {
                        DefaultWeightedEdge e = graph.getEdge(nodes.get(j), nodes.get(j + 1));
                        jgraphtLength += (double) graph.getEdgeWeight(e);
                    }

                    for (int j = 0; j < aStarNodes.size() - 1; j++) {
                        DefaultWeightedEdge e = graph.getEdge(aStarNodes.get(j), aStarNodes.get(j + 1));
                        serejaLength += (double) graph.getEdgeWeight(e);
                    }
                    if (serejaLength - jgraphtLength < 0.5)
                        incorrect++;

                } catch (NullPointerException e){
                    errors++;
                    continue;
                }
            }

            //System.out.println("Error in seconds: " + middle / 1000.0);
            System.out.println("Correct length: " + incorrect + " / 1000");
            System.out.println("Errors in path: " + errors);
            System.out.println("Sereja  middle: " + serejaMiddle / 1000.0);
            System.out.println("Jgrapht middle: " + jgraphtMiddle / 1000.0);
            //System.out.println("Jgrapht length: " + jgraphtLength);
            //System.out.println("Sereja  length: " + serejaLength);
          /*  //Visualizing
            ColorizedMapVisualizer vis1 = new ColorizedMapVisualizer();
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
}
