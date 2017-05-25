package org.educationalProject.surfacePathfinder.path;


import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelAStarPathFind {
    protected WeightedGraph<Point, DefaultWeightedEdge> graph;
    protected WeightedGraph<Point, DefaultWeightedEdge> graphFromSource;
    protected WeightedGraph<Point, DefaultWeightedEdge> graphFromDestination;
    protected Point source;
    protected Point destination;
    protected List<Point> shortestPath;
    protected double lengthOfPath = -1.0;

    public List<Point> getShortestPath(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                       Point source, Point destination){
        initialize(graph, source, destination);
        try {
            findPath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shortestPath;
    }
    protected void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph,
                              Point source, Point destination){
        this.graph = graph;
        GraphProxy graphProxySource = new GraphProxy(
                1.5*0.75,
                new ArrayList<Point>(graph.vertexSet()),
                "ModifiedJdiemke"
        );
        GraphProxy graphProxyDestination = new GraphProxy(
                1.5*0.75,
                new ArrayList<Point>(graph.vertexSet()),
                "ModifiedJdiemke"
        );
        this.graphFromSource = graphProxySource;
        this.graphFromDestination = graphProxyDestination;

        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            Point v1 = graph.getEdgeSource(edge);
            Point v2 = graph.getEdgeTarget(edge);
            graphFromSource.addEdge(v1, v2, edge);
            graphFromDestination.addEdge(v1, v2, edge);
        }

        this.source = source;
        this.destination = destination;
    }
    private void FinalMerge() {
        for (DefaultWeightedEdge edge : graphFromSource.edgeSet()) {
            Point v1 = graphFromSource.getEdgeSource(edge);
            Point v2 = graphFromSource.getEdgeTarget(edge);
            graph.addEdge(v1, v2, edge);
        }
        for (DefaultWeightedEdge edge : graphFromDestination.edgeSet()) {
            Point v1 = graphFromDestination.getEdgeSource(edge);
            Point v2 = graphFromDestination.getEdgeTarget(edge);
            graph.addEdge(v1, v2, edge);
        }
    }
    protected void findPath() throws InterruptedException {
        ArrayList<Point> pathForward = new ArrayList<Point>();
        ArrayList<Point> pathReverse = new ArrayList<Point>();
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        Point stopPointForward = new Point(0.0, 0.0, 0.0);
        Point stopPointReverse = new Point(0.0, 0.0, 0.0);
        CopyOnWriteArrayList<VisitedVertex> visitedForward = new CopyOnWriteArrayList<VisitedVertex>();
        CopyOnWriteArrayList<VisitedVertex> visitedReverse = new CopyOnWriteArrayList<VisitedVertex>();

        Runnable partSource = new AStarThread(graphFromSource, source, destination,
                pathForward, stopFlag, stopPointForward, visitedForward);
        Runnable partDestination = new AStarThread(graphFromDestination, destination, source,
                pathReverse, stopFlag, stopPointReverse, visitedReverse);
        Runnable stopRunnable = new NewStopPointSearcher(stopFlag, stopPointForward, stopPointReverse,
                visitedForward, visitedReverse);

        Thread threadSource = new Thread(partSource);
        Thread threadDestination = new Thread(partDestination);
        Thread stopThread = new Thread(stopRunnable);
        threadSource.start();
        threadDestination.start();
        stopThread.start();

        threadSource.join();
        threadDestination.join();

        System.out.println(pathForward.size());
        System.out.println(pathReverse.size());

        Collections.reverse(pathReverse);
        shortestPath = new ArrayList<Point>();
        for (int i = 0; i < pathForward.size(); i++)
            shortestPath.add(pathForward.get(i));

        for (int i = 0; i < pathReverse.size(); i++)
            shortestPath.add(pathReverse.get(i));

        for (int i = 1; i < shortestPath.size(); i++){
            DefaultWeightedEdge e = graphFromSource.getEdge(shortestPath.get(i - 1), shortestPath.get(i));
            if (e != null)
                lengthOfPath += (double) graphFromSource.getEdgeWeight(e);
            else {
                e = graphFromDestination.getEdge(shortestPath.get(i - 1), shortestPath.get(i));
                lengthOfPath += (double) graphFromDestination.getEdgeWeight(e);
            }
        }
    }

    public Double getLengthOfPath() {
        return lengthOfPath;
    }
}
