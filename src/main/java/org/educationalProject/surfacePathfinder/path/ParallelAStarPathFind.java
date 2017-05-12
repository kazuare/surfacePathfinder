package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class ParallelAStarPathFind {

    private static volatile WeightedGraph<Point, DefaultWeightedEdge> graph;
    private static Point source;
    private static Point destination;
    private List<Point> shortestPath;

    public List<Point> getShortestPath(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination){
        initialize(graph, source, destination);
        findPath();

        return shortestPath;
    }
    private void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination){

        this.graph = graph;
        this.source = source;
        this.destination = destination;
    }

    private void findPath(){

        ArrayList<Point> path1 = new ArrayList<Point>();
        ArrayList<Point> path2 = new ArrayList<Point>();
        ArrayList<Point> settledNodes1 = new ArrayList<Point>();
        ArrayList<Point> settledNodes2 = new ArrayList<Point>();
        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger stopPoint = new AtomicInteger(-1);

        Runnable part1 = new AStarThread(graph, source, destination,
                settledNodes1, settledNodes2, path1, stopPoint, stop);
        Runnable part2 = new AStarThread(graph, destination, source,
                settledNodes2, settledNodes1, path2, stopPoint, stop);
        Thread thread1 = new Thread(part1);
        Thread thread2 = new Thread(part2);
        thread1.start();
        thread2.start();
        while (thread1.isAlive() || thread2.isAlive());

        Collections.reverse(path2);
        System.out.println("length path1: " + path1.size());
        System.out.println("length path2: " + path2.size());
        shortestPath = new ArrayList<Point>();
        for (int i = 0; i < path1.size() - 1; i++)
            shortestPath.add(path1.get(i));
        for (int i = 0; i < path2.size(); i++)
            shortestPath.add(path2.get(i));

    }

    public Double getLengthOfPath() {
        Double length = 0.0;
        if (shortestPath == null)
            return 0.0;

        for (int j = 0; j < shortestPath.size() - 1; j++) {
            DefaultWeightedEdge e = graph.getEdge(shortestPath.get(j), shortestPath.get(j + 1));
            length += (double) graph.getEdgeWeight(e);
        }
        return length;
    }
}