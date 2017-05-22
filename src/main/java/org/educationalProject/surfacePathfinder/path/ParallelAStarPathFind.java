package org.educationalProject.surfacePathfinder.path;


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
    protected WeightedGraph<Point, DefaultWeightedEdge> graph;
    protected WeightedGraph<Point, DefaultWeightedEdge> graphFromSource;
    protected WeightedGraph<Point, DefaultWeightedEdge> graphFromDestination;
    protected Point source;
    protected Point destination;
    protected List<Point> shortestPath;
    protected double lengthOfPath;
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
        ArrayList<Point> pathFromSource = new ArrayList<Point>();
        ArrayList<Point> pathFromDestination = new ArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodesSource = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodesDestination = new CopyOnWriteArrayList<Point>();
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        AtomicInteger stopPointSource = new AtomicInteger(-1);
        AtomicInteger stopPointDestination = new AtomicInteger(-1);

        Runnable partSource = new AStarThread(graphFromSource, source, destination,
                settledNodesSource, pathFromSource, stopPointSource, stopFlag);
        Runnable partDestination = new AStarThread(graphFromDestination, destination, source,
                settledNodesDestination, pathFromDestination, stopPointDestination, stopFlag);
        Runnable stopRunnable = new StopPointSearcher(stopPointSource, stopPointDestination, stopFlag,
                settledNodesSource, settledNodesDestination);

        Thread threadSource = new Thread(partSource);
        Thread threadDestination = new Thread(partDestination);
        Thread stopThread = new Thread(stopRunnable);
        threadSource.start();
        threadDestination.start();
        stopThread.start();

        threadSource.join();
        threadDestination.join();

        Collections.reverse(pathFromDestination);
        shortestPath = new ArrayList<Point>();

        for (int i = 0; i < pathFromSource.size() - 1; i++){
            shortestPath.add(pathFromSource.get(i));
        }

        for (int i = 0; i < pathFromDestination.size(); i++) {
            shortestPath.add(pathFromDestination.get(i));
        }

        /*DefaultWeightedEdge e = graphFromDestination.getEdge(shortestPath.get(i - 1), shortestPath.get(i));
        lengthOfPath += (double) graphFromDestination.getEdgeWeight(e);*/

        //FinalMerge();
    }

    public Double getLengthOfPath() {
        if (shortestPath == null)
            return 0.0;

        return lengthOfPath;
    }
}
