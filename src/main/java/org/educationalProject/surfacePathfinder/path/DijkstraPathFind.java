package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;


public class DijkstraPathFind {

    private SimpleWeightedGraph<Point, DefaultWeightedEdge> g;
    private Set<Point> settledNodes;
    private Set<Point> unSettledNodes;
    private Map<Point, Point> predecessors;
    private Map<Point, Double> distance;


    public List<Point> getShortestPath(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph,
                                       Point source, Point destination) {
        this.init(graph, source);
        this.findAllPaths();
        return this.retrievalPath(destination);
    }

    private void init(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, Point source) {
        g = graph;
        settledNodes = new HashSet<Point>();
        unSettledNodes = new HashSet<Point>();
        distance = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();
        distance.put(source, 0.0);
        unSettledNodes.add(source);
    }


    private void findAllPaths() {
        while (unSettledNodes.size() > 0) {
            Point node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Point node) {
        List<Point> adjacentNodes = getNeighbors(node);
        for (Point target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private Double getDistance(Point node, Point target) {
        DefaultWeightedEdge e = g.getEdge(node, target);
        return (Double)g.getEdgeWeight(e);
    }

    private List<Point> getNeighbors(Point node) {
        List<Point> neighbors = new ArrayList<Point>();
        Set<DefaultWeightedEdge> edges = g.edgesOf(node);
        for (DefaultWeightedEdge edge : edges) {
            if (g.getEdgeSource(edge).equals(node) && !isSettled(g.getEdgeTarget(edge))) {
                neighbors.add(g.getEdgeTarget(edge));
            }
            if (g.getEdgeTarget(edge).equals(node) && !isSettled(g.getEdgeSource(edge))){
                neighbors.add(g.getEdgeSource(edge));
            }
        }
        return neighbors;
    }

    private Point getMinimum(Set<Point> vertexes) {
        Point minimum = null;
        for (Point vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Point vertex) {
        return settledNodes.contains(vertex);
    }

    private Double getShortestDistance(Point destination) {
        Double d = distance.get(destination);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    private List<Point> retrievalPath(Point target) {
        List<Point> shortestPath = new LinkedList<Point>();
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