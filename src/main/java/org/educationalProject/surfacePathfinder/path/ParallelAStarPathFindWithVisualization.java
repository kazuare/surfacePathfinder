package org.educationalProject.surfacePathfinder.path;


import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class ParallelAStarPathFindWithVisualization extends ParallelAStarPathFind {
    private MainDemoWindow demo;

    public List<Point> getShortestPath(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                       Point source, Point destination, MainDemoWindow demo) {


        initialize(graph, source, destination, demo);
        try {
            findPath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shortestPath;
    }

    protected void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph,
                              Point source, Point destination, MainDemoWindow demo) {
        this.demo = demo;
        super.initialize(graph, source, destination);
    }

    protected void findPath() throws InterruptedException {
        ArrayList<Point> pathFromSource = new ArrayList<Point>();
        ArrayList<Point> pathFromDestination = new ArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodesSource = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodesDestination = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArraySet<EdgeWithVertexes> edgesSource = new CopyOnWriteArraySet<EdgeWithVertexes>();
        CopyOnWriteArraySet<EdgeWithVertexes> edgesDestination = new CopyOnWriteArraySet<EdgeWithVertexes>();
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        AtomicInteger stopPointSource = new AtomicInteger(-1);
        AtomicInteger stopPointDestination = new AtomicInteger(-1);

        Runnable partSource = new AStarThreadWithVisualization(graphFromSource, source, destination,
                settledNodesSource, pathFromSource, stopPointSource, stopFlag, edgesSource);
        Runnable partDestination = new AStarThreadWithVisualization(graphFromDestination, destination, source,
                settledNodesDestination, pathFromDestination, stopPointDestination, stopFlag, edgesDestination);

        Runnable stopRunnable = new StopPointSearcherWithVisualization(graph,
                edgesSource, edgesDestination,
                stopPointSource, stopPointDestination, stopFlag,
                settledNodesSource, settledNodesDestination, demo);

        Thread threadSource = new Thread(partSource);
        Thread threadDestination = new Thread(partDestination);
        Thread stopThread = new Thread(stopRunnable);
        threadSource.start();
        threadDestination.start();
        stopThread.start();

        threadSource.join();
        threadDestination.join();
        stopThread.interrupt();
        Collections.reverse(pathFromDestination);
        shortestPath = new ArrayList<Point>();
        shortestPath.add(pathFromSource.get(0));
        for (int i = 1; i < pathFromSource.size() - 1; i++){
            shortestPath.add(pathFromSource.get(i));
            DefaultWeightedEdge e = graphFromSource.getEdge(shortestPath.get(i - 1), shortestPath.get(i));
            lengthOfPath += (double) graphFromSource.getEdgeWeight(e);
        }
        shortestPath.add(pathFromDestination.get(0));
        for (int i = 1; i < pathFromDestination.size(); i++) {
            shortestPath.add(pathFromDestination.get(i));
            DefaultWeightedEdge e = graphFromDestination.getEdge(shortestPath.get(i - 1), shortestPath.get(i));
            lengthOfPath += (double) graphFromDestination.getEdgeWeight(e);
        }
    }
}

