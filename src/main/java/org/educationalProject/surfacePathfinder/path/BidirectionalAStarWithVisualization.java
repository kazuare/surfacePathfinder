package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BidirectionalAStarWithVisualization extends BidirectionalAStar {
    protected MainDemoWindow demo;

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
        ArrayList<Point> pathForward = new ArrayList<Point>();
        ArrayList<Point> pathReverse = new ArrayList<Point>();
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        Point stopPointForward = new Point(0.0, 0.0, 0.0);
        Point stopPointReverse = new Point(0.0, 0.0, 0.0);
        CopyOnWriteArrayList<VisitedVertex> visitedForward = new CopyOnWriteArrayList<VisitedVertex>();
        CopyOnWriteArrayList<VisitedVertex> visitedReverse = new CopyOnWriteArrayList<VisitedVertex>();

        Runnable partSource = new AStarThreadWithVisualization(graphForward, source, destination,
                pathForward, stopFlag, stopPointForward, visitedForward, demo, 1);
        Runnable partDestination = new AStarThreadWithVisualization(graphReverse, destination, source,
                pathReverse, stopFlag, stopPointReverse, visitedReverse, demo, 2);
        Runnable stopRunnable = new StopPointSearcher(stopFlag, stopPointForward, stopPointReverse,
                visitedForward, visitedReverse);

        Thread threadSource = new Thread(partSource);
        Thread threadDestination = new Thread(partDestination);
        Thread stopThread = new Thread(stopRunnable);
        threadSource.start();
        threadDestination.start();
        stopThread.start();

        threadSource.join();
        threadDestination.join();

        retrievePath(pathForward, pathReverse);
        demo.setPath(shortestPath);
    }

}

