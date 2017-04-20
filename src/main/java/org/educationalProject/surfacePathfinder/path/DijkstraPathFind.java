package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.*;


public class DijkstraPathFind {

    private SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
    private Set<Point> settledNodes;
    private FibonacciHeap<Point> unSettledNodes;
    private Map<Point, Point> predecessors;
    private Map<Point, Double> distance;


    public List<Point> getShortestPath(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph,
                                       Point source, Point destination) {
        this.init(graph, source);
        this.findAllPaths();
        return this.retrievalPath(destination);
    }

    private void init(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, Point source) {
        this.graph = graph;
        settledNodes = new HashSet<Point>();
        unSettledNodes = new FibonacciHeap<Point>();
        distance = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();
        distance.put(source, 0.0);
        unSettledNodes.insert(new FibonacciHeapNode<Point>(source), 0.0);
    }

    private void findAllPaths() {
        while (unSettledNodes.size() > 0) {
            FibonacciHeapNode<Point> heapNode = unSettledNodes.removeMin();
            settledNodes.add(heapNode.getData());
            findMinimalDistances(heapNode.getData());
        }
    }

    private void findMinimalDistances(Point node) {
        List<Point> adjacentNodes = getNeighbors(node);
        for (Point target : adjacentNodes) {
            double getDistanceTarget = getDistance(target);
            double getDistanceNode = getDistance(node);
            double getDistanceNodeTarget = getDistance(node, target);
            if (getDistanceTarget > getDistanceNode
                    + getDistanceNodeTarget) {
                distance.put(target, getDistanceNode + getDistanceNodeTarget);
                predecessors.put(target, node);
                unSettledNodes.insert(new FibonacciHeapNode<Point>(target),
                        getDistanceNode + getDistanceNodeTarget);
            }
        }

    }

    private Double getDistance(Point node, Point target) {
        DefaultWeightedEdge e = graph.getEdge(node, target);
        return (Double)graph.getEdgeWeight(e);
    }

    private Double getDistance(Point destination) {
        Double d = distance.get(destination);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }

    private List<Point> getNeighbors(Point node) {
        List<Point> neighbors = new ArrayList<Point>();
        Set<DefaultWeightedEdge> edges = graph.edgesOf(node);
        for (DefaultWeightedEdge edge : edges) {
            Point source = graph.getEdgeSource(edge);
            Point target = graph.getEdgeTarget(edge);
            if (source.equals(node) && !isSettled(target)) {
                neighbors.add(target);
                continue;
            }
            if (target.equals(node) && !isSettled(source)) {
                neighbors.add(source);
            }
        }
        return neighbors;
    }

    private boolean isSettled(Point vertex) {
        return settledNodes.contains(vertex);
    }

    private List<Point> retrievalPath(Point target) {
        List<Point> shortestPath = new ArrayList<Point>();
        Point step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        shortestPath.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            shortestPath.add(step);
        }
        // Put it into the correct order
        Collections.reverse(shortestPath);
        return shortestPath;
    }
}