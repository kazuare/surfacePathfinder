package org.educationalProject.surfacePathfinder.path;


import io.github.jdiemke.triangulation.Vector2D;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelAStarPathFind {
    private WeightedGraph<Point, DefaultWeightedEdge> graph;
    private WeightedGraph<Point, DefaultWeightedEdge> graphSource;
    private WeightedGraph<Point, DefaultWeightedEdge> graphDestination;
    private int sizePathSource;
    private int sizePathDestination;
    private Point source;
    private Point destination;
    private List<Point> shortestPath;

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
    private void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph,
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
        this.graphSource = graphProxySource;
        this.graphDestination = graphProxyDestination;

        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            Point v1 = graph.getEdgeSource(edge);
            Point v2 = graph.getEdgeTarget(edge);
            graphSource.addEdge(v1, v2, edge);
            graphDestination.addEdge(v1, v2, edge);
        }

        this.source = source;
        this.destination = destination;
    }
    private void Merge() {
        System.out.println(graph.edgeSet().size());
        for (DefaultWeightedEdge edge : graphSource.edgeSet()) {
            Point v1 = graphSource.getEdgeSource(edge);
            Point v2 = graphSource.getEdgeTarget(edge);
            graph.addEdge(v1, v2, edge);
        }
        for (DefaultWeightedEdge edge : graphDestination.edgeSet()) {
            Point v1 = graphDestination.getEdgeSource(edge);
            Point v2 = graphDestination.getEdgeTarget(edge);
            graph.addEdge(v1, v2, edge);
        }
        System.out.println(graph.edgeSet().size());
    }
    private void findPath() throws InterruptedException {
        ArrayList<Point> path1 = new ArrayList<Point>();
        ArrayList<Point> path2 = new ArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodes1 = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodes2 = new CopyOnWriteArrayList<Point>();
        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger stopPoint1 = new AtomicInteger(-1);
        AtomicInteger stopPoint2 = new AtomicInteger(-1);

        Runnable partSource = new AStarThread(graphSource, source, destination,
                settledNodes1, path1, stopPoint1, stop);
        Runnable partDestination = new AStarThread(graphDestination, destination, source,
                settledNodes2, path2, stopPoint2, stop);
        Runnable stopRunnable = new StopPointSearcher(stopPoint1, stopPoint2, stop,
                settledNodes1, settledNodes2);

        Thread threadSource = new Thread(partSource);
        Thread threadDestination = new Thread(partDestination);
        Thread stopThread = new Thread(stopRunnable);
        threadSource.start();
        threadDestination.start();
        stopThread.start();
        
        threadSource.join();
        threadDestination.join();

        Collections.reverse(path2);
        shortestPath = new ArrayList<Point>();
        sizePathSource = path1.size();
        sizePathDestination = path2.size();
        for (int i = 0; i < sizePathSource - 1; i++)
            shortestPath.add(path1.get(i));
        for (int i = 0; i < sizePathDestination; i++)
            shortestPath.add(path2.get(i));
        Merge();
    }

    public Double getLengthOfPath() {
        Double length = 0.0;
        if (shortestPath == null)
            return 0.0;

        for (int i = 0; i < sizePathSource - 2; i++) {
            DefaultWeightedEdge e = graphSource.getEdge(shortestPath.get(i), shortestPath.get(i + 1));
            length += (double) graphSource.getEdgeWeight(e);
        }
        for (int i = 0; i < sizePathDestination - 1; i++) {
            DefaultWeightedEdge e = graphDestination.getEdge(shortestPath.get(i), shortestPath.get(i + 1));
            length += (double) graphDestination.getEdgeWeight(e);
        }
        return length;
    }
}
