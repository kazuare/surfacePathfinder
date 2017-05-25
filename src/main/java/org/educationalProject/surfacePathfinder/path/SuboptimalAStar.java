package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class SuboptimalAStar extends BidirectionalAStarWithVisualization {
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
        ConcurrentHashMap<Point, Double> distanceSource = new ConcurrentHashMap<Point, Double>();
        ConcurrentHashMap<Point, Double> distanceDestination = new ConcurrentHashMap<Point, Double>();
        ArrayList<Point> pathFromSource = new ArrayList<Point>();
        ArrayList<Point> pathFromDestination = new ArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodesSource = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodesDestination = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArrayList<Point> intersection = new CopyOnWriteArrayList<Point>();
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        AtomicInteger stopPointSource = new AtomicInteger(-1);
        AtomicInteger stopPointDestination = new AtomicInteger(-1);

        Runnable partSource = new SuboptimalAStarThread(graphForward, source, destination,
                settledNodesSource, pathFromSource, stopPointSource, stopFlag, distanceSource, demo, 1);
        Runnable partDestination = new SuboptimalAStarThread(graphReverse, destination, source,
                settledNodesDestination, pathFromDestination, stopPointDestination, stopFlag, distanceDestination, demo, 2);

        Runnable stopRunnable = new SuboptimalStopPointSearcher(stopPointSource, stopPointDestination, stopFlag,
                settledNodesSource, settledNodesDestination, intersection, distanceSource, distanceDestination);

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
        shortestPath.add(pathFromSource.get(0));
        for (int i = 1; i < pathFromSource.size() - 1; i++){
            shortestPath.add(pathFromSource.get(i));
        }
        shortestPath.add(pathFromDestination.get(0));
        for (int i = 1; i < pathFromDestination.size(); i++) {
            shortestPath.add(pathFromDestination.get(i));
        }
        demo.setPath(shortestPath);
    }
}
