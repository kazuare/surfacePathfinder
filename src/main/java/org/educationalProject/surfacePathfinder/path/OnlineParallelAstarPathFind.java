package org.educationalProject.surfacePathfinder.path;


import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OnlineParallelAstarPathFind extends ParallelAStarPathFind{
    private WeightedGraph<Point, DefaultWeightedEdge> graph1;
    private WeightedGraph<Point, DefaultWeightedEdge> graph2;
    private int sizePath1;
    private int sizePath2;
    public List<Point> getShortestPath(WeightedGraph<Point, DefaultWeightedEdge> graph1,
                                       WeightedGraph<Point, DefaultWeightedEdge> graph2,
                                       Point source, Point destination){
        initialize(graph1, graph2, source, destination);
        try {
            findPath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shortestPath;
    }
    private void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph1,
                            WeightedGraph<Point, DefaultWeightedEdge> graph2,
                            Point source, Point destination){

        this.graph1 = graph1;
        this.graph2 = graph2;
        this.source = source;
        this.destination = destination;
    }

    private void findPath() throws InterruptedException {
        ArrayList<Point> path1 = new ArrayList<Point>();
        ArrayList<Point> path2 = new ArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodes1 = new CopyOnWriteArrayList<Point>();
        CopyOnWriteArrayList<Point> settledNodes2 = new CopyOnWriteArrayList<Point>();
        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger stopPoint1 = new AtomicInteger(-1);
        AtomicInteger stopPoint2 = new AtomicInteger(-1);

        Runnable part1 = new AStarThread(graph1, source, destination,
                settledNodes1, path1, stopPoint1, stop);
        Runnable part2 = new AStarThread(graph2, destination, source,
                settledNodes2, path2, stopPoint2, stop);
        Runnable stopRunnable = new StopPointSearcher(stopPoint1, stopPoint2, stop,
                settledNodes1, settledNodes2);

        Thread thread1 = new Thread(part1);
        Thread thread2 = new Thread(part2);
        Thread stopThread = new Thread(stopRunnable);
        thread1.start();
        thread2.start();
        stopThread.start();
        
        thread1.join();
        thread2.join();

        Collections.reverse(path2);
        shortestPath = new ArrayList<Point>();
        sizePath1 = path1.size();
        sizePath2 = path2.size();
        for (int i = 0; i < sizePath1 - 1; i++)
            shortestPath.add(path1.get(i));
        for (int i = 0; i < sizePath2; i++)
            shortestPath.add(path2.get(i));

    }

    public Double getLengthOfPath() {
        Double length = 0.0;
        if (shortestPath == null)
            return 0.0;

        for (int i = 0; i < sizePath1 - 2; i++) {
            DefaultWeightedEdge e = graph1.getEdge(shortestPath.get(i), shortestPath.get(i + 1));
            length += (double) graph1.getEdgeWeight(e);
        }
        for (int i = 0; i < sizePath2 - 1; i++) {
            DefaultWeightedEdge e = graph2.getEdge(shortestPath.get(i), shortestPath.get(i + 1));
            length += (double) graph2.getEdgeWeight(e);
        }
        return length;
    }
}
