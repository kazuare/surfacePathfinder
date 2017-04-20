package org.educationalProject.surfacePathfinder.test;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.educationalProject.surfacePathfinder.*;
import org.educationalProject.surfacePathfinder.Dijkstra.Dijkstra;
import org.educationalProject.surfacePathfinder.Dijkstra.HillDijkstra;
import org.educationalProject.surfacePathfinder.Dijkstra.Route;
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


public class YegorTest {

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
            Vector<Vector2D> points = ObjFileParser.getPoints("C:\\digdes\\map.obj");
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

            clock.tic();
            DijkstraShortestPath<Point,DefaultWeightedEdge> alternative = new DijkstraShortestPath<Point,DefaultWeightedEdge>(graph);
            List<Point> dijkstraNodes = alternative.getPath(a, b).getVertexList();
            resultingTime = clock.tocd();
            System.out.println("Alternative algo is finished, phase duration is: " + resultingTime);

            clock.tic();
            Dijkstra x = new HillDijkstra();
            x.init(graph);
            //x.init(graphWithoutPenalty);
            HashMap<Point, Route> routes = x.run(a);
            List<Point> r = routes.get(b).vertices;
            resultingTime = clock.tocd();
            System.out.println("Alternative 2 algo is finished, phase duration is: " + resultingTime);

            //Checking algorithm correctness
            boolean equal = true;
            if(r.size()==dijkstraNodes.size()){
                for(int i = 0; i < dijkstraNodes.size(); i++)
                    if(!dijkstraNodes.get(i).equals(r.get(i)))
                        equal = false;
            }else{
                equal = false;
            }
            System.out.println("alternative and alternative 2 are equal: " + (""+equal).toUpperCase());

            //Visualizing
            ColorizedMapVisualizer vis1 = new ColorizedMapVisualizer();
            vis1.setData(triangles, nodes, graph);
            SwingWindow.start(vis1, 800, 600, "traditional dijkstra map");

            //Visualizing
            ColorizedMapVisualizer vis2 = new ColorizedMapVisualizer();
            vis2.setData(triangles, r, graph);
            SwingWindow.start(vis2, 800, 600, "hill dijkstra map");

            PathVisualizer vis3 = new PathVisualizer();
            vis3.setData(nodes, points);
            SwingWindow.start(vis3, 800, vis3.calculateWindowHeight(800), "traditional dijkstra");

            PathVisualizer vis4 = new PathVisualizer();
            vis4.setData(r, points);
            SwingWindow.start(vis4, 800, vis4.calculateWindowHeight(800), "hill dijkstra");

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
